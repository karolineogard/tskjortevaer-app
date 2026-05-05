package no.uio.ifi.in2000.ieulrich.team32.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.LocationForecastRepository
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.dto.LocationForecastResponse
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.mapper.toForecastByDay
import no.uio.ifi.in2000.ieulrich.team32.model.locationForecast.ForecastHourDetails

class LocationForecastViewmodel(
    private val locationClient: FusedLocationProviderClient
): ViewModel() {
    private val repository: LocationForecastRepository = LocationForecastRepository()
    private val _forecast = MutableStateFlow<LocationForecastResponse?>(null)
    val forecast: StateFlow<LocationForecastResponse?> = _forecast.asStateFlow()
    private val _forecastByDay = MutableStateFlow<Map<String, List<ForecastHourDetails>>?>(null)
    val forecastByDay: StateFlow<Map<String, List<ForecastHourDetails>>?> = _forecastByDay.asStateFlow()

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