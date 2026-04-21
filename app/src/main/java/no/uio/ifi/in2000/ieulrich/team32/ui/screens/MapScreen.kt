package no.uio.ifi.in2000.ieulrich.team32.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.DeviceThermostat
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import no.uio.ifi.in2000.ieulrich.team32.model.metAlerts.MetAlert
import no.uio.ifi.in2000.ieulrich.team32.model.victoriaWMS.WeatherLayer
import no.uio.ifi.in2000.ieulrich.team32.ui.victoriaWMS.MapViewModel
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.FillLayer
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.layers.RasterLayer
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.android.style.sources.RasterSource
import org.maplibre.android.style.sources.TileSet
import java.net.URI
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = viewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    var mapRef by remember { mutableStateOf<org.maplibre.android.maps.MapLibreMap?>(null) }
    val showAlertsActive by rememberUpdatedState(uiState.showAlerts)
    var isMenuExpanded by remember { mutableStateOf(false) }

    if (uiState.selectedAlert != null) {
        AlertDetailScreen(
            alert = uiState.selectedAlert!!,
            onBack = { viewModel.selectAlert(null) }
        )
    } else {
        Box(modifier = modifier.fillMaxSize()) {
            AndroidView(
                factory = { context ->
                    MapView(context).apply {
                        onCreate(null)
                        getMapAsync { map ->
                            mapRef = map
                            val styleUrl = "https://tiles.openfreemap.org/styles/liberty"
                            map.setStyle(Style.Builder().fromUri(styleUrl)) { style ->
                                map.cameraPosition = CameraPosition.Builder()
                                    .target(LatLng(60.0, 11.0))
                                    .zoom(5.0)
                                    .build()

                                updateWmsLayer(style, uiState.wmsUrl, uiState.currentLayer?.name ?: "none")
                                updateAlertsLayer(style, uiState.showAlerts, uiState.alertsUrl)
                            }

                            map.addOnMapClickListener { point ->
                                if (showAlertsActive) {
                                    val features = map.queryRenderedFeatures(
                                        map.projection.toScreenLocation(point), "alerts-layer"
                                    )
                                    if (features.isNotEmpty()) {
                                        val feature = features[0]
                                        val alert = MetAlert(
                                            event = feature.getStringProperty("event") ?: "",
                                            severity = feature.getStringProperty("severity") ?: "",
                                            description = feature.getStringProperty("description") ?: "",
                                            area = feature.getStringProperty("area") ?: "",
                                            instruction = feature.getStringProperty("instruction") ?: "",
                                            consequence = feature.getStringProperty("consequence") ?: "",
                                            title = feature.getStringProperty("title") ?: "",
                                        )
                                        viewModel.selectAlert(alert)
                                        return@addOnMapClickListener true
                                    }
                                    return@addOnMapClickListener false
                                }
                                
                                val lat = point.latitude
                                val lon = point.longitude
                                navController.navigate("forecast?lat=$lat&lon=$lon")
                                true
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            LaunchedEffect(uiState.wmsUrl, uiState.showAlerts) {
                mapRef?.getStyle { style ->
                    updateWmsLayer(style, uiState.wmsUrl, uiState.currentLayer?.name ?: "none")
                    updateAlertsLayer(style, uiState.showAlerts, uiState.alertsUrl)
                }
            }

            // Top Search Bar
            SearchBar(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp)
            )

            // Legend (Top Left)
            if (uiState.currentLayer == WeatherLayer.TEMPERATURE) {
                TemperatureLegendCard(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 110.dp, start = 16.dp)
                )
            } else if (uiState.currentLayer == WeatherLayer.PRECIPITATION) {
                 PrecipitationLegendCard(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 110.dp, start = 16.dp),
                )
            }

            // Layer Selection Menu (Right)
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp, bottom = 100.dp),
            ) {
                AnimatedVisibility(
                    visible = isMenuExpanded,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically(),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        LayerButton("Nedbør", Icons.Outlined.WaterDrop, uiState.currentLayer == WeatherLayer.PRECIPITATION) {
                            viewModel.onLayerSelected(WeatherLayer.PRECIPITATION)
                        }
                        LayerButton("Temperatur", Icons.Outlined.DeviceThermostat, uiState.currentLayer == WeatherLayer.TEMPERATURE) {
                            viewModel.onLayerSelected(WeatherLayer.TEMPERATURE)
                        }
                        LayerButton("Vind", Icons.Outlined.Air, uiState.currentLayer == WeatherLayer.WIND) {
                            viewModel.onLayerSelected(WeatherLayer.WIND)
                        }
                        LayerButton("Farevarsler", Icons.Default.Warning, uiState.showAlerts) {
                            viewModel.onAlertsSelected()
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                FloatingActionButton(
                    onClick = { isMenuExpanded = !isMenuExpanded },
                    containerColor = Color.White.copy(alpha = 0.9f),
                    contentColor = Color.Black,
                    shape = CircleShape,
                    modifier = Modifier.size(56.dp).align(Alignment.End)
                ) {
                    Icon(
                        if (isMenuExpanded) Icons.Default.Close else Icons.Default.KeyboardArrowUp,
                        contentDescription = "Meny"
                    )
                }
            }

            // Bottom Time Slider Card
            TimeSliderCard(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
            )
        }
    }
}

@Composable
fun TemperatureLegendCard(modifier: Modifier = Modifier) {
    val tempRanges = listOf(
        ">25", "20", "15", "10", "5",
        "3", "1", "0", "-1", "-3",
        "-5", "-10", "-15", "<-20"
    )
    val colors = listOf(
        "#FF2000", "#FF6000", "#FF9F00", "#FFDF00", "#FFFF2A", 
        "#FFFF7E", "#FFFFD2", "#F5F5FF", "#C4C4FF", "#9393FF", 
        "#6262FF", "#3131FF", "#0000FF", "#0000E7"
    )

    Surface(
        modifier = modifier.width(65.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.9f),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.DeviceThermostat, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("°C", style = MaterialTheme.typography.labelMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier.height(240.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                itemsIndexed(tempRanges) { index, range ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(android.graphics.Color.parseColor(colors[index])))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(range, fontSize = 10.sp, color = Color.Black)
                    }
                }
            }
        }
    }
}


@Composable
fun PrecipitationLegendCard(modifier: Modifier = Modifier) {
    val tempRanges = listOf(
        "0.2",
        "1.0",
        "2.0",
        "5.0",
        "10.0",
        "15.0",
        "20.0",
        "25.0",
        "30.0",
        "35.0",
        ">40.0"
    )
    val colors = listOf(
        "#80EBFF", "#59CCFF", "#32A2FF", "#0C6BFF", "#0140E5",
        "#0122C1", "#020C9C", "#110286", "#360187", "#5C0187",
        "#830088", "#880066", "#8d0044"
    )

    Surface(
        modifier = modifier.width(70.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.9f),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.WaterDrop, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("mm", style = MaterialTheme.typography.labelMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier.height(180.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                itemsIndexed(tempRanges) { index, range ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(android.graphics.Color.parseColor(colors[index])))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(range, fontSize = 10.sp, color = Color.Black)
                    }
                }
            }
        }
    }
}
@Composable
fun WindLegendCard(modifier: Modifier = Modifier) {
    val windRanges = listOf(
        "0.3", "1.6",
        "3.4", "5.5", "8.0", "10.8",
        "13.9", "17.2", "20.8", "24.5",
        "28.5", ">32.6"
    )
    val colors = listOf(
        "#FCFFF2", "#EFF9CA", "#C4E898", "#88D079",
        "#4DB85A", "#11A03C", "#FFB600", "#FF7E00",
        "#F40009", "#D00028", "#AC0047", "#0000FF"
    )

    Surface(
        modifier = modifier.width(70.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.9f),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Air, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("m/s", style = MaterialTheme.typography.labelMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier.height(180.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                itemsIndexed(windRanges) { index, range ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(android.graphics.Color.parseColor(colors[index])))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(range, fontSize = 10.sp, color = Color.Black)
                    }
                }
            }
        }
    }
}


@Composable
fun SearchBar(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Søk her...",
                color = Color.Gray,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Default.Search, contentDescription = "Søk", tint = Color.Black)
        }
    }
}

@Composable
fun LayerButton(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = if (isSelected) Color(0xFFE1F5FE) else Color.White.copy(alpha = 0.9f),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun TimeSliderCard(modifier: Modifier = Modifier) {
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    
    val now = Instant.now().atZone(ZoneOffset.UTC)
    val baseTime = now.withHour((now.hour / 3) * 3).truncatedTo(java.time.temporal.ChronoUnit.HOURS)
    val selectedTime = baseTime.plusHours((sliderPosition.toInt() * 3).toLong())
    val timeLabel = if (selectedTime.toLocalDate() == now.toLocalDate()) "I dag" else "I morgen"
    val formattedTime = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm"))

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.9f),
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("$timeLabel kl. $formattedTime", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Slider(
                value = sliderPosition,
                onValueChange = { sliderPosition = it },
                valueRange = 0f..8f,
                steps = 7,
                colors = SliderDefaults.colors(
                    thumbColor = Color.Black,
                    activeTrackColor = Color.Black,
                    inactiveTrackColor = Color.LightGray
                )
            )
        }
    }
}

private fun updateWmsLayer(style: Style, wmsUrl: String, layerId: String) {
    style.layers.filter { it.id.startsWith("victoria-") }.forEach { style.removeLayer(it) }
    style.sources.filter { it.id.startsWith("victoria-") }.forEach { style.removeSource(it) }

    if (wmsUrl.isEmpty()) return

    val sourceId = "victoria-source-$layerId"
    val fullLayerId = "victoria-layer-$layerId"

    val tileSet = TileSet("2.1.0", wmsUrl)
    val rasterSource = RasterSource(sourceId, tileSet, 256)
    style.addSource(rasterSource)

    val rasterLayer = RasterLayer(fullLayerId, sourceId)
    style.addLayer(rasterLayer)
}

private fun updateAlertsLayer(style: Style, show: Boolean, alertsUrl: String) {
    style.removeLayer("alerts-layer")
    style.removeSource("alerts-source")

    if (!show || alertsUrl.isEmpty()) return

    val geoJsonSource = GeoJsonSource("alerts-source", URI(alertsUrl))
    style.addSource(geoJsonSource)

    val fillLayer = FillLayer("alerts-layer", "alerts-source")
    fillLayer.setProperties(
        PropertyFactory.fillColor(Color(0x60FF0000).hashCode()),
        PropertyFactory.fillOutlineColor(Color.Red.hashCode())
    )
    style.addLayer(fillLayer)
}
