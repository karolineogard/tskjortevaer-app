package no.uio.ifi.in2000.ieulrich.team32.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.ieulrich.team32.data.geocoding.LocationRepository
import no.uio.ifi.in2000.ieulrich.team32.data.location.DeviceLocationDataSource
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.LocationForecastRepository
import no.uio.ifi.in2000.ieulrich.team32.data.victoriaWMS.WeatherRepository
import no.uio.ifi.in2000.ieulrich.team32.model.locationForecast.ForecastHourDetails
import no.uio.ifi.in2000.ieulrich.team32.model.metAlerts.MetAlert
import javax.inject.Inject

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
        val alerts: List<MetAlert>
    ) : UiState()

    object Error : UiState()
}

@HiltViewModel
class HomeViewModel @Inject constructor (
    private val deviceLocationDataSource: DeviceLocationDataSource,
    private val locationForecastRepository: LocationForecastRepository,
    private val weatherRepository: WeatherRepository,
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
                    val alertsDeferred = async { weatherRepository.getAlertsByLocation(lat, lon) }

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

