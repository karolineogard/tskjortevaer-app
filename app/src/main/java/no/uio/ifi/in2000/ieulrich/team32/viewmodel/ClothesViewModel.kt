package no.uio.ifi.in2000.ieulrich.team32.viewmodel

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.LocationForecastRepository
import no.uio.ifi.in2000.ieulrich.team32.model.clothes.ClothesRecommendation
import no.uio.ifi.in2000.ieulrich.team32.model.clothes.ClothesRecommendationEngine
import no.uio.ifi.in2000.ieulrich.team32.model.clothes.UserSettings
import no.uio.ifi.in2000.ieulrich.team32.model.locationForecast.ForecastHourDetails
import no.uio.ifi.in2000.ieulrich.team32.ui.components.ActivityLevel
import javax.inject.Inject

private object ClothesSettingsKeys {
    val DEPARTURE_HOUR = intPreferencesKey("departure_hour")
    val DEPARTURE_MINUTE = intPreferencesKey("departure_minute")
    val RETURN_HOUR = intPreferencesKey("return_hour")
    val RETURN_MINUTE = intPreferencesKey("return_minute")
    val TEMPERATURE_OFFSET = floatPreferencesKey("temperature_offset")
}

@HiltViewModel
class ClothesViewModel @Inject constructor(
    private val repository: LocationForecastRepository,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

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

    private var temperatureOffset: Float = 0f
    private var userHasOverriddenTimes = false

    init {
        Log.d("ClothesViewModel", "Initialiserer ClothesViewModel")
        // Les lagrede innstillinger direkte fra DataStore ved oppstart
        viewModelScope.launch {
            dataStore.data.collect { prefs ->
                val tempOffset = try {
                    prefs[ClothesSettingsKeys.TEMPERATURE_OFFSET] ?: 0f
                } catch (e: ClassCastException) { 0f }

                temperatureOffset = tempOffset

                if (!userHasOverriddenTimes) {
                    _settings.value = _settings.value.copy(
                        departureHour = prefs[ClothesSettingsKeys.DEPARTURE_HOUR] ?: 8,
                        departureMinute = prefs[ClothesSettingsKeys.DEPARTURE_MINUTE] ?: 0,
                        returnHour = prefs[ClothesSettingsKeys.RETURN_HOUR] ?: 16,
                        returnMinute = prefs[ClothesSettingsKeys.RETURN_MINUTE] ?: 0
                    )
                }
                computeRecommendation()
            }
        }
    }

    fun updateLocation(lat: Double, lon: Double) {
        currentLat = lat
        currentLon = lon
        computeRecommendation()
    }

    fun loadRecommendation() {
        computeRecommendation()
    }

    /** Kalles fra bottom sheet — overstyrer faste tider for denne sesjonen */
    fun updateSettings(
        departureHour: Int,
        departureMinute: Int,
        returnHour: Int,
        returnMinute: Int,
        isOutdoors: Boolean,
        isPhysicallyActive: Boolean,
        activityLevel: ActivityLevel?
    ) {
        userHasOverriddenTimes = true
        _settings.value = UserSettings(
            departureHour      = departureHour,
            departureMinute    = departureMinute,
            returnHour         = returnHour,
            returnMinute       = returnMinute,
            isOutdoors         = isOutdoors,
            isPhysicallyActive = isPhysicallyActive,
            activityLevel      = activityLevel
        )
        computeRecommendation()
    }

    private fun computeRecommendation() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val forecastByDay = repository.getForecastByDay(currentLat, currentLon)
                val todayForecasts = forecastByDay["I dag"] ?: emptyList()

                val dep = _settings.value.departureHour
                val ret = _settings.value.returnHour

                val forecastPool = if (ret < dep) {
                    todayForecasts + (forecastByDay["I morgen"] ?: emptyList())
                } else {
                    todayForecasts
                }

                val relevantForecasts = filterByTimeWindow(forecastPool, dep, ret)
                val forecastsToUse    = relevantForecasts.ifEmpty { todayForecasts }

                _recommendation.value = ClothesRecommendationEngine.recommend(
                    forecasts         = forecastsToUse,
                    settings          = _settings.value,
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
            val hour = no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.Format
                .extractHour(forecast.timestamp).toIntOrNull() ?: return@filter false
            if (departureHour <= returnHour) {
                hour in departureHour until returnHour
            } else {
                hour >= departureHour || hour < returnHour
                //Forklaring av warning: kan ikke bruke en vanlig range her fordi logikken er "enten
                // etter avgang ELLER før retur" (midnatt-kryssing).
                // En range ville ikke fungert riktig.
            }
        }
    }
}