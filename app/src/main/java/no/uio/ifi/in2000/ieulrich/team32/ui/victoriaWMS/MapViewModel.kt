package no.uio.ifi.in2000.ieulrich.team32.ui.victoriaWMS

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import no.uio.ifi.in2000.ieulrich.team32.data.victoriaWMS.WeatherRepository
import no.uio.ifi.in2000.ieulrich.team32.data.victoriaWMS.WeatherRepositoryImpl
import no.uio.ifi.in2000.ieulrich.team32.model.metAlerts.MetAlert
import no.uio.ifi.in2000.ieulrich.team32.model.victoriaWMS.WeatherLayer

data class MapUiState(
    val currentLayer: WeatherLayer? = WeatherLayer.TEMPERATURE,
    val wmsUrl: String = "",
    val showAlerts: Boolean = false,
    val alertsUrl: String = "",
    val selectedAlert: MetAlert? = null
)

class MapViewModel(
    private val repository: WeatherRepository = WeatherRepositoryImpl()
) : ViewModel() {
    private val _uiState = MutableStateFlow(MapUiState(alertsUrl = repository.getAlertsUrl()))
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        updateLayer(WeatherLayer.TEMPERATURE)
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
            wmsUrl = repository.getWmsUrl(layer),
            showAlerts = false
        ) }
    }
}
