package no.uio.ifi.in2000.ieulrich.team32.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.LocationForecastRepository
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.dto.LocationForecastResponse
import no.uio.ifi.in2000.ieulrich.team32.model.locationForecast.ForecastHourDetails

class LocationForecastViewmodel(): ViewModel() {
    private val repository: LocationForecastRepository = LocationForecastRepository()
    private val _forecast = MutableStateFlow<LocationForecastResponse?>(null)
    val forecast: StateFlow<LocationForecastResponse?> = _forecast.asStateFlow()
    private val _forecastByDay = MutableStateFlow<Map<String, List<ForecastHourDetails>>?>(null)
    val forecastByDay: StateFlow<Map<String, List<ForecastHourDetails>>?> = _forecastByDay.asStateFlow()

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
}