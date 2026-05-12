package no.uio.ifi.in2000.ieulrich.team32.viewmodel

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.ieulrich.team32.data.victoriaWMS.WeatherRepository
import no.uio.ifi.in2000.ieulrich.team32.model.metAlerts.MetAlert
import no.uio.ifi.in2000.ieulrich.team32.model.victoriaWMS.WeatherLayer
import org.maplibre.android.geometry.LatLng
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

data class MapUiState(
    val currentLayer: WeatherLayer? = WeatherLayer.TEMPERATURE,
    val wmsUrl: String = "",
    val showAlerts: Boolean = false,
    val alertsUrl: String = "",
    val alerts: List<MetAlert> = emptyList(),
    val selectedAlert: MetAlert? = null,
    val mapCenter: LatLng = LatLng(60.0, 11.0),
    val mapZoom: Double = 5.0
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: WeatherRepository
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
                val fetchedAlerts = repository.getAllAlerts()
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

    fun updateMapPosition(latLng: LatLng, zoom: Double) {

        if (latLng.latitude == 0.0 && latLng.longitude == 0.0 && zoom < 1.0) {
            Log.d("MapPos", "Ignorerer ugyldig start-posisjon")
            return
        }
        Log.d("MapPos", "Lagrer posisjon: ${latLng.latitude}, ${latLng.longitude} Zoom: $zoom")
        _uiState.update { it.copy(
            mapCenter = latLng,
            mapZoom = zoom
        ) }
    }

    fun onPlaceSelected(location: Location){
        _zoomToLocation.value = location
    }


}
