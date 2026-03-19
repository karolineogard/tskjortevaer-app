package no.uio.ifi.in2000.ieulrich.team32.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                MapView(context).apply {
                    onCreate(null)
                    getMapAsync { map ->
                        val styleUrl = "https://tiles.openfreemap.org/styles/liberty"
                        map.setStyle(Style.Builder().fromUri(styleUrl)) { style ->
                            map.cameraPosition = CameraPosition.Builder()
                                .target(LatLng(60.0, 11.0))
                                .zoom(5.0)
                                .build()
                            
                            addVictoriaLayer(style, uiState.wmsUrl)
                        }
                    }
                }
            },
            update = { _ ->
                // Siden URL-en er statisk i denne versjonen, trenger vi ikke oppdatere her
                // Men hvis tidsstempel legges til senere, kan man kalle addVictoriaLayer igjen her.
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

private fun addVictoriaLayer(style: Style, wmsUrl: String) {
    val sourceId = "victoria-temperature-source"
    val layerId = "victoria-temperature-layer"

    style.getLayer(layerId)?.let { style.removeLayer(it) }
    style.getSource(sourceId)?.let { style.removeSource(it) }

    val tileSet = TileSet("2.1.0", wmsUrl)
    val rasterSource = RasterSource(sourceId, tileSet, 256)
    style.addSource(rasterSource)

    val rasterLayer = RasterLayer(layerId, sourceId)
    style.addLayer(rasterLayer)
}
