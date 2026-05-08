package no.uio.ifi.in2000.ieulrich.team32.viewmodel

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import no.uio.ifi.in2000.ieulrich.team32.data.geocoding.LocationRepository
import no.uio.ifi.in2000.ieulrich.team32.data.location.DeviceLocationDataSource
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.LocationForecastRepository
import no.uio.ifi.in2000.ieulrich.team32.data.metAlert.AlertFeature
import no.uio.ifi.in2000.ieulrich.team32.data.metAlert.AlertsRepository
import no.uio.ifi.in2000.ieulrich.team32.model.locationForecast.ForecastHourDetails
import javax.inject.Inject
import kotlin.coroutines.resume
data class AppLocation(
    val lat: Double,
    val lon: Double
)
sealed class UiState {
    object Loading : UiState()

    data class Success(
        val location: AppLocation?,
        val forecast: ForecastHourDetails?,
        val place: String,
        val alerts: List<AlertFeature>
    ) : UiState()

    object Error : UiState()
}

@HiltViewModel
class HomeViewModel @Inject constructor (
    private val deviceLocationDataSource: DeviceLocationDataSource,
    private val locationForecastRepository: LocationForecastRepository,
    private val alertsRepository: AlertsRepository,
    private val locationRepository: LocationRepository
): ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        Log.d("HomeViewModel", "ViewModel initialized")
    }

    fun loadData(){
        viewModelScope.launch {
            try {
                Log.d("HomeViewModel", "Prøver å laste data")
                val location = deviceLocationDataSource.getCurrentLocation()
                val appLocation = AppLocation(
                    location?.lat ?: 59.9432,
                    location?.lon ?: 10.7173)
                val lat = appLocation.lat
                val lon = appLocation.lon

                coroutineScope {
                    val forecastDeferred = async { locationForecastRepository.getForecastNow(lat, lon) }
                    val placeDeferred = async { locationRepository.getPlaceName(lat, lon) }
                    val alertsDeferred = async { alertsRepository.getCurrentAlerts(lat, lon) }

                    val forecast = forecastDeferred.await()
                    val place = placeDeferred.await()
                    val alerts = alertsDeferred.await()

                    if (forecast != null){
                        _uiState.value = UiState.Success(
                            location = appLocation,
                            forecast = forecast,
                            place = place,
                            alerts = alerts
                        )
                    } else {
                        Log.e("HomeViewModel", "Error loading data")
                        _uiState.value = UiState.Error
                    }
                }

            }
            catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading data", e)
                _uiState.value = UiState.Error
            }
        }
    }

}

