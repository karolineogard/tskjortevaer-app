package no.uio.ifi.in2000.ieulrich.team32.ui.victoriaWMS

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import no.uio.ifi.in2000.ieulrich.team32.data.victoriaWMS.WeatherRepository
import no.uio.ifi.in2000.ieulrich.team32.data.victoriaWMS.WeatherRepositoryImpl
import no.uio.ifi.in2000.ieulrich.team32.model.victoriaWMS.WeatherLayer

data class MapUiState(
    // Enkel WMS-URL for temperatur. {bbox-epsg-3857} er påkrevd for at MapLibre skal vite hvor filene skal hentes.
    val currentLayer: WeatherLayer = WeatherLayer.TEMPERATURE,
    val wmsUrl: String = ""
        )

class MapViewModel(
    private val repository: WeatherRepository = WeatherRepositoryImpl()
) : ViewModel() {
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    fun onLayerSelected(layer: WeatherLayer) {
        _uiState.update { it.copy(
            currentLayer = layer,
            wmsUrl = repository.getWmsUrl(layer)
        ) }
    }
}
