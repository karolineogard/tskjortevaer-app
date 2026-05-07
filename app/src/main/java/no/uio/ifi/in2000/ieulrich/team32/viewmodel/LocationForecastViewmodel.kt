package no.uio.ifi.in2000.ieulrich.team32.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.ieulrich.team32.data.location.DeviceLocationDataSource
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.LocationForecastRepository
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.dto.LocationForecastResponse
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.mapper.toForecastByDay
import no.uio.ifi.in2000.ieulrich.team32.model.locationForecast.ForecastHourDetails
import javax.inject.Inject

@HiltViewModel
class LocationForecastViewmodel @Inject constructor(
    private val deviceLocationDataSource: DeviceLocationDataSource,
    private val repository: LocationForecastRepository
): ViewModel() {
    private val _forecast = MutableStateFlow<LocationForecastResponse?>(null)
    val forecast: StateFlow<LocationForecastResponse?> = _forecast.asStateFlow()
    private val _forecastByDay = MutableStateFlow<Map<String, List<ForecastHourDetails>>?>(null)
    val forecastByDay: StateFlow<Map<String, List<ForecastHourDetails>>?> = _forecastByDay.asStateFlow()

    private val _currentLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val currentLocation: StateFlow<Pair<Double, Double>?> = _currentLocation.asStateFlow()

    init {
        Log.d("LocationForecastViewModel", "ViewModel initialized")
    }

    fun loadForecastForDevice() {
        viewModelScope.launch {
            val location = deviceLocationDataSource.getCurrentLocation()

            // fallback to default location if location permission is denied
            val lat = location?.latitude ?: 59.9432
            val lon = location?.longitude ?: 10.7173

            if (location == null) {
                Log.w("LocationDebug", "Location failed, using default (Oslo)")
            }

            _currentLocation.value = Pair(lat, lon)

            val response = repository.getForecast(lat, lon)
            _forecast.value = response
            _forecastByDay.value = response?.toForecastByDay()
        }
    }

    fun loadForecast(lat: Double, lon: Double) {
        viewModelScope.launch {
            val response = repository.getForecast(lat, lon)
            _forecast.value = response
            _forecastByDay.value = response?.toForecastByDay()
        }
    }
}