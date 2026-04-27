package no.uio.ifi.in2000.ieulrich.team32.viewmodel

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.LocationForecastRepository
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.dto.LocationForecastResponse
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.mapper.toForecastByDay
import no.uio.ifi.in2000.ieulrich.team32.model.locationForecast.ForecastHourDetails
import java.util.jar.Manifest
import kotlin.coroutines.resume

class LocationForecastViewmodel(
    private val locationClient: FusedLocationProviderClient
): ViewModel() {

    fun getDeviceLocation(context: Context){
        viewModelScope.launch {
            val location = locationClient.getDeviceLocation(context)
        }
    }
    private val repository: LocationForecastRepository = LocationForecastRepository()
    private val _forecast = MutableStateFlow<LocationForecastResponse?>(null)
    val forecast: StateFlow<LocationForecastResponse?> = _forecast.asStateFlow()
    private val _forecastByDay = MutableStateFlow<Map<String, List<ForecastHourDetails>>?>(null)
    val forecastByDay: StateFlow<Map<String, List<ForecastHourDetails>>?> = _forecastByDay.asStateFlow()

    private val _forecastNow = MutableStateFlow<ForecastHourDetails?>(null)
    val forecastNow: StateFlow<ForecastHourDetails?> = _forecastNow.asStateFlow()

    fun loadForecastForDevice(context: Context){
        viewModelScope.launch {
            val location = locationClient.getDeviceLocation(context) ?: return@launch
            loadForecast(location.latitude, location.longitude)
        }
    }

    fun loadForecast(lat: Double, lon: Double){
        viewModelScope.launch {
            val response = repository.getForecast(lat, lon)
            _forecast.value = response
            _forecastByDay.value = response.toForecastByDay()
            _forecastNow.value = response.toForecastByDay().values.flatten().firstOrNull()
        }
    }
    fun getForecast(lat: Double, lon: Double){
        viewModelScope.launch {
            _forecast.value = repository.getForecast(lat, lon)
        }
    }

    fun getForecastByDay(lat: Double, lon: Double){
        viewModelScope.launch {
            _forecastByDay.value = repository.getForecastByDay(lat, lon)
        }
    }

    fun getForecastNow(lat: Double, lon: Double){
        viewModelScope.launch {
            _forecastNow.value = repository.getForecastNow(lat, lon)
        }
    }



}

suspend fun FusedLocationProviderClient.getDeviceLocation(context: Context): Location?{
    val hasPermission = ActivityCompat.checkSelfPermission(
        context, android.Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    if (!hasPermission) return null

    return suspendCancellableCoroutine { continuation ->
        lastLocation.addOnSuccessListener { location ->
            continuation.resume(location)
        }.addOnFailureListener {
            continuation.resume(null)
        }
    }
}