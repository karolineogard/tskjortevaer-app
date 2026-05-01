package no.uio.ifi.in2000.ieulrich.team32.viewmodel

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigationevent.NavigationEventDispatcher
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.LocationForecastRepository
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.dto.LocationForecastResponse
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.mapper.toForecastByDay
import no.uio.ifi.in2000.ieulrich.team32.model.locationForecast.ForecastHourDetails
import kotlin.coroutines.resume

class LocationForecastViewmodel(
    private val locationClient: FusedLocationProviderClient
): ViewModel() {
    private val repository: LocationForecastRepository = LocationForecastRepository()
    private val _forecast = MutableStateFlow<LocationForecastResponse?>(null)
    val forecast: StateFlow<LocationForecastResponse?> = _forecast.asStateFlow()
    private val _forecastByDay = MutableStateFlow<Map<String, List<ForecastHourDetails>>?>(null)
    val forecastByDay: StateFlow<Map<String, List<ForecastHourDetails>>?> = _forecastByDay.asStateFlow()

    private val _forecastNow = MutableStateFlow<ForecastHourDetails?>(null)
    val forecastNow: StateFlow<ForecastHourDetails?> = _forecastNow.asStateFlow()

    private val _currentLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val currentLocation: StateFlow<Pair<Double, Double>?> = _currentLocation.asStateFlow()

    init {
        Log.d("LocationForecastViewModel", "ViewModel initialized")
    }

    fun loadForecastForDevice(context: Context){
        val appContext = context.applicationContext
        viewModelScope.launch {
            val location = locationClient.getDeviceLocation(appContext)

            val lat: Double
            val lon: Double

            if (location != null) {
                lat = location.latitude
                lon = location.longitude
                Log.d("LocationDebug", "Using device location: $lat, $lon")
            }
            else {
                lat = 59.91
                lon = 10.75
                Log.w("LocationDebug", "Location failed, using default (Oslo)")
            }

            _currentLocation.value = Pair(lat, lon)

            val response = repository.getForecast(lat, lon)
            _forecast.value = response
            val groupedByDay = response.toForecastByDay()
            _forecastByDay.value = groupedByDay
            val now = java.time.Instant.now()
            val allHours = groupedByDay.values.flatten()
            _forecastNow.value = allHours.minByOrNull { hour ->
                try {
                    val hourInstant = java.time.Instant.parse(hour.timestamp)
                    java.time.Duration.between(hourInstant, now).abs().toMinutes()
                } catch (e: Exception){
                    Long.MAX_VALUE
                }
            }

        }
    }

    fun loadForecast(lat: Double, lon: Double){
        viewModelScope.launch {
            val response = repository.getForecast(lat, lon)
            _forecast.value = response
            _forecastByDay.value = response.toForecastByDay()
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