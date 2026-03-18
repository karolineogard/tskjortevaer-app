package no.uio.ifi.in2000.ieulrich.team32.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import no.uio.ifi.in2000.ieulrich.team32.data.victoriaWMS.WeatherRepository
import no.uio.ifi.in2000.ieulrich.team32.data.victoriaWMS.WeatherRepositoryImpl

data class MapUiState(
    // Enkel WMS-URL for temperatur. {bbox-epsg-3857} er påkrevd for at MapLibre skal vite hvor flisene skal hentes.
    val wmsUrl: String = ""
        )

class MapViewModel(
    private val repository: WeatherRepository = WeatherRepositoryImpl()
) : ViewModel() {
    private val _uiState = MutableStateFlow(MapUiState(wmsUrl = repository.getWmsUrl()))
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()
}
