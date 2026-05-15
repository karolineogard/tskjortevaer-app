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
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.LocationForecastRepository
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.mapper.toForecastByDay
import no.uio.ifi.in2000.ieulrich.team32.model.locationForecast.ForecastHourDetails
import javax.inject.Inject

sealed class LocationForecastUiState {
    data object Loading : LocationForecastUiState()
    data object Error : LocationForecastUiState()
    data class Success(
        val forecastByDay: Map<String, List<ForecastHourDetails>>,
        val placeName: String
    ) : LocationForecastUiState()
}
@HiltViewModel
class LocationForecastViewmodel @Inject constructor(
    private val repository: LocationForecastRepository,
    private val locationRepository: LocationRepository
): ViewModel() {
    private val _uiState = MutableStateFlow<LocationForecastUiState>(LocationForecastUiState.Loading)
    val uiState: StateFlow<LocationForecastUiState> = _uiState.asStateFlow()

    init {
        Log.d("LocationForecastViewModel", "ViewModel initialized")
    }

    fun loadForecast(lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.value = LocationForecastUiState.Loading
            try {
                val placeName = locationRepository.getPlaceName(lat, lon)
                val forecastByDay = repository.getForecast(lat, lon)?.toForecastByDay()
                    ?: throw Exception("No forecast received")

                _uiState.value = LocationForecastUiState.Success(
                    forecastByDay,
                    placeName
                )
            } catch (e: Exception) {
                Log.e("LocationForecastViewModel", "Error loading forecast", e)
                _uiState.value = LocationForecastUiState.Error
            }
    }
    }
}