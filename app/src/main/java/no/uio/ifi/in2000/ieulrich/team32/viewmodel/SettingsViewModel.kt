package no.uio.ifi.in2000.ieulrich.team32.viewmodel

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

private object SettingsKeys {
    val DEPARTURE_HOUR     = intPreferencesKey("departure_hour")
    val DEPARTURE_MINUTE   = intPreferencesKey("departure_minute")
    val RETURN_HOUR        = intPreferencesKey("return_hour")
    val RETURN_MINUTE      = intPreferencesKey("return_minute")
    val TEMPERATURE_OFFSET = floatPreferencesKey("temperature_offset")
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    val defaultDepartureHour: StateFlow<Int> = dataStore.data
        .map { it[SettingsKeys.DEPARTURE_HOUR] ?: 8 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 8)

    val defaultDepartureMinute: StateFlow<Int> = dataStore.data
        .map { it[SettingsKeys.DEPARTURE_MINUTE] ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val defaultReturnHour: StateFlow<Int> = dataStore.data
        .map { it[SettingsKeys.RETURN_HOUR] ?: 16 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 16)

    val defaultReturnMinute: StateFlow<Int> = dataStore.data
        .map { it[SettingsKeys.RETURN_MINUTE] ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    /**
     * Temperaturoffset i grader Celsius.
     * -5.0 = ispinne (fryser lett → lavere effektiv temp → tykkere klær)
     *  0.0 = nøytral
     * +5.0 = viking (varm av seg → høyere effektiv temp → lettere klær)
     */
    val temperatureOffset: StateFlow<Float> = dataStore.data
        .map {
            try { it[SettingsKeys.TEMPERATURE_OFFSET] ?: 0f }
            catch (e: ClassCastException) { 0f }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0f)
    /** Lagrer alle innstillinger til disk på én gang. */
    fun saveSettings(
        departureHour: Int,
        departureMinute: Int,
        returnHour: Int,
        returnMinute: Int,
        temperatureOffset: Float
    ) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[SettingsKeys.DEPARTURE_HOUR]     = departureHour
                prefs[SettingsKeys.DEPARTURE_MINUTE]   = departureMinute
                prefs[SettingsKeys.RETURN_HOUR]        = returnHour
                prefs[SettingsKeys.RETURN_MINUTE]      = returnMinute
                prefs[SettingsKeys.TEMPERATURE_OFFSET] = temperatureOffset
            }
        }
    }

    /** Beholdes for bakoverkompatibilitet med ClothesScreen. */
    fun updateDefaultTimes(
        departureHour: Int,
        departureMinute: Int,
        returnHour: Int,
        returnMinute: Int
    ) {
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[SettingsKeys.DEPARTURE_HOUR]   = departureHour
                prefs[SettingsKeys.DEPARTURE_MINUTE] = departureMinute
                prefs[SettingsKeys.RETURN_HOUR]      = returnHour
                prefs[SettingsKeys.RETURN_MINUTE]    = returnMinute
            }
        }
    }
}