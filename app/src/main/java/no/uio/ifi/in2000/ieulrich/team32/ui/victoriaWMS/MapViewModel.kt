package no.uio.ifi.in2000.ieulrich.team32.ui.victoriaWMS

import android.location.Location
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import no.uio.ifi.in2000.ieulrich.team32.data.victoriaWMS.WeatherRepository
import no.uio.ifi.in2000.ieulrich.team32.data.victoriaWMS.WeatherRepositoryImpl
import no.uio.ifi.in2000.ieulrich.team32.model.metAlerts.MetAlert
import no.uio.ifi.in2000.ieulrich.team32.model.victoriaWMS.WeatherLayer
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

data class MapUiState(
    val currentLayer: WeatherLayer? = WeatherLayer.TEMPERATURE,
    val wmsUrl: String = "",
    val showAlerts: Boolean = false,
    val alertsUrl: String = "",
    val alerts: List<MetAlert> = emptyList(),
    val selectedAlert: MetAlert? = null
)

class MapViewModel(
    private val repository: WeatherRepository = WeatherRepositoryImpl()
) : ViewModel() {
    private val _uiState = MutableStateFlow(MapUiState(alertsUrl = repository.getAlertsUrl()))
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private val _zoomToLocation = MutableStateFlow<Location?>(null)
    val zoomToLocation = _zoomToLocation.asStateFlow()

    init {
        updateLayer(WeatherLayer.TEMPERATURE)
        Log.d("MapViewModel", "Initialiserer MapViewModel")
    }

    fun onLayerSelected(layer: WeatherLayer) {
        updateLayer(layer)
    }

    fun onAlertsSelected() {

        _uiState.update { it.copy(
        currentLayer = null,
        wmsUrl = "",
        showAlerts = true,
    ) }
        viewModelScope.launch {
            try {
                val fetchedAlerts = repository.getAlerts()
                _uiState.update { it.copy(
                    alerts = fetchedAlerts
                ) }
                Log.d("MapViewModel", "Hentet ${fetchedAlerts.size} varsler")
            } catch (e: Exception) {
                Log.e("MapViewModel", "Feil ved henting av farevarsler")
            }
        }

    }

    fun selectAlert(alert: MetAlert?) {
        _uiState.update { it.copy(
            selectedAlert = alert
        ) }
    }

    fun toggleAlerts() {
        _uiState.update { it.copy(showAlerts = !it.showAlerts) }
    }

    private fun updateLayer(layer: WeatherLayer) {
            _uiState.update { it.copy(
                currentLayer = layer,
                wmsUrl = repository.getWmsUrl(layer, getCurrentTime()),
                showAlerts = false
            ) }
    }

    fun getCurrentTime(): String{
        val currentTime = Instant.now()
            .atZone(ZoneOffset.UTC)
            .let { zdt ->
                val roundedHour = ((zdt.hour + 1) / 3) * 3  // runder til nærmeste, ikke alltid ned
                zdt.withHour(roundedHour).truncatedTo(ChronoUnit.HOURS)
            }
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"))
        return currentTime
    }


    fun onTimeChanged(formattedTimeUTC: String) {
        val current = _uiState.value.currentLayer ?: return
        _uiState.update { it.copy(
            wmsUrl = repository.getWmsUrl(current, formattedTimeUTC)
        ) }
    }

    fun onPlaceSelected(location: Location){
        _zoomToLocation.value = location
    }


}
