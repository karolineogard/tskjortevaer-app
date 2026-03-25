package no.uio.ifi.in2000.ieulrich.team32.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import no.uio.ifi.in2000.ieulrich.team32.ui.victoriaWMS.MapViewModel
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.RasterLayer
import org.maplibre.android.style.sources.RasterSource
import org.maplibre.android.style.sources.TileSet
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.ieulrich.team32.model.victoriaWMS.WeatherLayer
import org.maplibre.android.style.sources.GeoJsonSource
import java.net.URI
import org.maplibre.android.style.layers.FillLayer
import org.maplibre.android.style.layers.PropertyFactory


@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var mapRef by remember { mutableStateOf<org.maplibre.android.maps.MapLibreMap?>(null)}

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                MapView(context).apply {
                    onCreate(null)
                    getMapAsync { map ->
                        mapRef = map
                        val styleUrl = "https://tiles.openfreemap.org/styles/liberty"
                        map.setStyle(Style.Builder().fromUri(styleUrl)) { style: Style ->
                            map.cameraPosition = CameraPosition.Builder()
                                .target(LatLng(60.0, 11.0))
                                .zoom(5.0)
                                .build()
                            
                            updateWmsLayer(style, uiState.wmsUrl, uiState.currentLayer.name)
                            updateAlertsLayer(style, uiState.showAlerts, uiState.alertsUrl)
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        LaunchedEffect(uiState.wmsUrl, uiState.showAlerts) {
            val map = mapRef ?: return@LaunchedEffect

            map.getStyle { style ->
                updateWmsLayer(style, uiState.wmsUrl, uiState.currentLayer.name)
                updateAlertsLayer(style, uiState.showAlerts, uiState.alertsUrl)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Button(
                    onClick = { viewModel.onLayerSelected(WeatherLayer.TEMPERATURE) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.currentLayer == WeatherLayer.TEMPERATURE) Color(
                            0xFF3F51B5
                        ) else Color.Gray
                    )
                ) {
                    Text("Temp", maxLines = 1)
                }

                Button(
                    onClick = { viewModel.onLayerSelected(WeatherLayer.PRECIPITATION) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.currentLayer == WeatherLayer.PRECIPITATION) Color(
                            0xFF3F51B5
                        ) else Color.Gray
                    )
                ) {
                    Text("Nedbør", maxLines = 1)
                }

                Button(
                    onClick = { viewModel.onLayerSelected(WeatherLayer.WIND) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.currentLayer == WeatherLayer.WIND) Color(
                            0xFF3F51B5
                        ) else Color.Gray
                    )
                ) {
                    Text("Vind", maxLines = 1)
                }
            }
            Button(
                onClick = { viewModel.toggleAlerts()},
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (uiState.showAlerts) Color(0xFFFF9800) else Color.Gray)
            ) {
                Text(if (uiState.showAlerts) "Skjul farevarsel" else "Vis farevarsel")
            }

        }
    }
}

private fun updateWmsLayer(style: Style, wmsUrl: String, layerId: String) {

    style.layers.filter { it.id.startsWith("victoria-") }.forEach { style.removeLayer(it) }
    style.sources.filter { it.id.startsWith("victoria-") }.forEach { style.removeSource(it) }

    if (wmsUrl.isEmpty()) return

    val sourceId = "victoria-source-$layerId"
    val layerId = "victoria-layer$layerId"

    style.getLayer(layerId)?.let { style.removeLayer(it) }
    style.getSource(sourceId)?.let { style.removeSource(it) }

    val tileSet = TileSet("2.1.0", wmsUrl)

    val rasterSource = RasterSource(sourceId, tileSet, 256)
    style.addSource(rasterSource)

    val rasterLayer = RasterLayer(layerId, sourceId)
    style.addLayer(rasterLayer)
}

private fun updateAlertsLayer(style: Style, show: Boolean, alertsUrl: String){
    style.removeLayer("alerts-layer")
    style.removeSource("alerts-source")

    if (!show || alertsUrl.isEmpty()) return

    val geoJsonSource = GeoJsonSource("alerts-source", URI(alertsUrl))
    style.addSource(geoJsonSource)

    val fillLayer = FillLayer("alerts-layer", "alerts-source")
    fillLayer.setProperties(
        PropertyFactory.fillColor(Color(0x80FF0000).hashCode()),
        PropertyFactory.fillOutlineColor(Color.Red.hashCode())
    )
    style.addLayer(fillLayer)
}
