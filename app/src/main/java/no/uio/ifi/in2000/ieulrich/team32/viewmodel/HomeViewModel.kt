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
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import no.uio.ifi.in2000.ieulrich.team32.data.geocoding.LocationRepository
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.LocationForecastRepository
import no.uio.ifi.in2000.ieulrich.team32.data.metAlert.AlertFeature
import no.uio.ifi.in2000.ieulrich.team32.data.metAlert.AlertsRepository
import no.uio.ifi.in2000.ieulrich.team32.data.metAlert.NetworkAlertsRepository
import no.uio.ifi.in2000.ieulrich.team32.model.locationForecast.ForecastHourDetails
import no.uio.ifi.in2000.ieulrich.team32.ui.screens.LocationForecastScreen
import kotlin.coroutines.resume

sealed class UiState {
    object Loading : UiState()

    data class Success(
        val location: Location,
        val forecast: ForecastHourDetails?,
        val place: String,
        val alerts: List<AlertFeature>
    ) : UiState()

    object Error : UiState()
}

class HomeViewModel(
    private val locationClient: FusedLocationProviderClient,
): ViewModel() {
    private val locationForecastRepository: LocationForecastRepository = LocationForecastRepository()
    private val alertsRepository: AlertsRepository = NetworkAlertsRepository()
    private val locationRepository: LocationRepository = LocationRepository()
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        Log.d("HomeViewModel", "ViewModel initialized")
    }

    fun loadData(context: Context){
        val appContext = context.applicationContext
        viewModelScope.launch {
            Log.d("HomeVIewModel", "Prøver å loade data")
            val location = locationClient.getDeviceLocation(appContext)

            try {
                if (location == null) {
                    Log.d("HomeViewModel", "could not find location, using default")
                }
                val lat = location?.latitude ?: 59.91
                val lon = location?.longitude ?: 10.73

                coroutineScope {
                    val forecastDeferred = async { locationForecastRepository.getForecastNow(lat, lon) }
                    val placeDeferred = async { locationRepository.getPlaceName(lat, lon) }
                    val alertsDeferred = async { alertsRepository.getCurrentAlerts(lat, lon) }

                    val forecast = forecastDeferred.await()
                    val place = placeDeferred.await()
                    val alerts = alertsDeferred.await()

                    if (forecast != null){
                        _uiState.value = UiState.Success(
                            location = location ?: Location("appDefault").apply {
                                latitude = lat
                                longitude = lon },
                            forecast = forecast,
                            place = place,
                            alerts = alerts
                        )
                    } else {
                        _uiState.value = UiState.Error
                    }
                }

            }
            catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading data")
                _uiState.value = UiState.Error
            }
        }
    }
}

suspend fun FusedLocationProviderClient.getDeviceLocation(context: Context): Location?{
    val hasPermission = ActivityCompat.checkSelfPermission(
        context, android.Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    if (!hasPermission) return null


    return suspendCancellableCoroutine { continuation ->
        val cts = CancellationTokenSource()
        getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cts.token)
            .addOnSuccessListener { location ->
                if (location != null) {
                    continuation.resume(location)
                } else {
                    lastLocation.addOnSuccessListener { lastLoc ->
                        continuation.resume(lastLoc)
                    }.addOnFailureListener {
                        continuation.resume(null)
                    }
                }
            }.addOnFailureListener {
                continuation.resume(null)
            }
        continuation.invokeOnCancellation {
            cts.cancel()
        }
    }
}