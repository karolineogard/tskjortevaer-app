package no.uio.ifi.in2000.ieulrich.team32.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MapUiState(
    // Enkel WMS-URL for temperatur. {bbox-epsg-3857} er påkrevd for at MapLibre skal vite hvor flisene skal hentes.
    val wmsUrl: String = "https://public-victoria.met.no/wms?service=WMS&version=1.3.0&request=GetMap" +
            "&layers=air_temperature_2m_meps_det_vdiv_2_5km_calculations&styles=&crs=EPSG:3857&format=image/png&transparent=true" +
            "&width=256&height=256&bbox={bbox-epsg-3857}&time=2026-03-16T13:00:00Z"
        )

class MapViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()
}
