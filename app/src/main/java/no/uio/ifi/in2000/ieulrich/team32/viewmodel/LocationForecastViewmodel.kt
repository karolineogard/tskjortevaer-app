package no.uio.ifi.in2000.ieulrich.team32.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.ieulrich.team32.data.geocoding.LocationRepository
import no.uio.ifi.in2000.ieulrich.team32.data.location.DeviceLocationDataSource
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.LocationForecastRepository
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.dto.LocationForecastResponse
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.mapper.toForecastByDay
import no.uio.ifi.in2000.ieulrich.team32.model.locationForecast.ForecastHourDetails
import javax.inject.Inject

@HiltViewModel
class LocationForecastViewmodel @Inject constructor(
    private val deviceLocationDataSource: DeviceLocationDataSource,
    private val repository: LocationForecastRepository,
    private val locationRepository: LocationRepository
): ViewModel() {
    private val _forecast = MutableStateFlow<LocationForecastResponse?>(null)
    val forecast: StateFlow<LocationForecastResponse?> = _forecast.asStateFlow()
    private val _forecastByDay = MutableStateFlow<Map<String, List<ForecastHourDetails>>?>(null)
    val forecastByDay: StateFlow<Map<String, List<ForecastHourDetails>>?> = _forecastByDay.asStateFlow()
    private val _placeName = MutableStateFlow<String>("Unknown")
    val placeName = _placeName.asStateFlow()

    init {
        Log.d("LocationForecastViewModel", "ViewModel initialized")
    }

    fun loadForecastForDevice() {
        viewModelScope.launch {
            val location = deviceLocationDataSource.getCurrentLocation()

            // fallback to default location if location permission is denied
            val lat = location?.lat ?: 59.9432
            val lon = location?.lon ?: 10.7173

            if (location == null) {
                Log.w("LocationDebug", "Location failed, using default (Oslo)")
            }
            loadForecast(lat, lon)
        }
    }

    fun loadForecast(lat: Double, lon: Double) {
        viewModelScope.launch {
            _placeName.value = locationRepository.getPlaceName(lat, lon)
            val response = repository.getForecast(lat, lon)
            _forecast.value = response
            _forecastByDay.value = response?.toForecastByDay()
        }
    }
}