package no.uio.ifi.in2000.ieulrich.team32.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.Format
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.LocationForecastRepository
import no.uio.ifi.in2000.ieulrich.team32.model.clothes.ClothesRecommendation
import no.uio.ifi.in2000.ieulrich.team32.model.clothes.ClothesRecommendationEngine
import no.uio.ifi.in2000.ieulrich.team32.model.clothes.UserSettings
import no.uio.ifi.in2000.ieulrich.team32.model.locationForecast.ForecastHourDetails
import no.uio.ifi.in2000.ieulrich.team32.ui.components.ActivityLevel

class ClothesViewModel : ViewModel() {

    private val repository = LocationForecastRepository()

    var currentLat: Double = 59.9139
        private set
    var currentLon: Double = 10.7522
        private set

    private val _settings = MutableStateFlow(UserSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    private val _recommendation = MutableStateFlow<ClothesRecommendation?>(null)
    val recommendation: StateFlow<ClothesRecommendation?> = _recommendation.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Temperaturoffset fra innstillinger (-5 til +5). Settes utenfra via MapApp.
    private var temperatureOffset: Float = 0f

    init {
        Log.d("ClothesViewModel", "Initialiserer ClothesViewModel")
    }

    fun clearRecommendationIfOffline(isOnline: Boolean) {
        if (!isOnline) {
            _recommendation.value = null
        }
    }

    /** Kall fra MapApp når SettingsViewModel.temperatureOffset endres. */
    fun updateTemperatureOffset(offset: Float) {
        temperatureOffset = offset
        computeRecommendation()
    }

    /** Kall fra andre skjermer (f.eks. HomeScreen) for å sette posisjon. */
    fun updateLocation(lat: Double, lon: Double) {
        currentLat = lat
        currentLon = lon
        computeRecommendation()
    }

    /** Kall fra ClothesScreen ved oppstart. */
    fun loadRecommendation() {
        computeRecommendation()
    }

    /** Oppdater brukerinnstillinger og beregn ny anbefaling. */
    fun updateSettings(
        departureHour: Int,
        departureMinute: Int,
        returnHour: Int,
        returnMinute: Int,
        isOutdoors: Boolean,
        isPhysicallyActive: Boolean,
        activityLevel: ActivityLevel?
    ) {
        _settings.value = UserSettings(
            departureHour = departureHour,
            departureMinute = departureMinute,
            returnHour = returnHour,
            returnMinute = returnMinute,
            isOutdoors = isOutdoors,
            isPhysicallyActive = isPhysicallyActive,
            activityLevel = activityLevel
        )
        computeRecommendation()
    }

    private fun computeRecommendation() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val forecastByDay = repository.getForecastByDay(currentLat, currentLon)
                val todayForecasts = forecastByDay["I dag"] ?: emptyList()
                val relevantForecasts = filterByTimeWindow(
                    todayForecasts,
                    _settings.value.departureHour,
                    _settings.value.returnHour
                )
                val forecastsToUse = relevantForecasts.ifEmpty { todayForecasts }
                _recommendation.value = ClothesRecommendationEngine.recommend(
                    forecasts = forecastsToUse,
                    settings = _settings.value,
                    temperatureOffset = temperatureOffset
                )
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun filterByTimeWindow(
        forecasts: List<ForecastHourDetails>,
        departureHour: Int,
        returnHour: Int
    ): List<ForecastHourDetails> {
        return forecasts.filter { forecast ->
            val hour = Format.extractHour(forecast.timestamp).toIntOrNull() ?: return@filter false
            if (departureHour <= returnHour) {
                hour in departureHour until returnHour
            } else {
                hour >= departureHour || hour < returnHour
            }
        }
    }
}