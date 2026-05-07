package no.uio.ifi.in2000.ieulrich.team32.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import no.uio.ifi.in2000.ieulrich.team32.R
import no.uio.ifi.in2000.ieulrich.team32.model.metAlerts.MetAlert
import no.uio.ifi.in2000.ieulrich.team32.model.metAlerts.iconUrl
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
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import org.maplibre.android.style.expressions.Expression.*
import android.graphics.Color as AndroidColor
import no.uio.ifi.in2000.ieulrich.team32.ui.screens.AlertDetailScreen
import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.ieulrich.team32.ui.components.SearchBar
import org.maplibre.android.camera.CameraUpdateFactory
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    isOnline: Boolean
){
    val viewModel: MapViewModel = hiltViewModel()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var mapRef by remember { mutableStateOf<org.maplibre.android.maps.MapLibreMap?>(null) }
    val showAlertsActive by rememberUpdatedState(uiState.showAlerts)
    var isMenuExpanded by remember { mutableStateOf(false) }
    var isSearchExpanded by remember { mutableStateOf(false) }
    val scaffoldState = rememberBottomSheetScaffoldState()

    val context = LocalContext.current
    val activity = context as Activity
    val scope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        onDispose {
            activity.requestedOrientation = originalOrientation
        }
    }

    // Dynamically calculate the top padding for the legend based on search bar state
    val legendTopPadding by animateDpAsState(
        targetValue = if (isSearchExpanded) 120.dp else 48.dp,
        label = "legendTopPadding"
    )

    if (uiState.selectedAlert != null) {
        AlertDetailScreen(
            alert = uiState.selectedAlert!!,
            onBack = { viewModel.selectAlert(null) }
        )
    } else {
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = if(uiState.showAlerts && uiState.alerts.isNotEmpty()) 90.dp else 0.dp,
            sheetContainerColor = Color.White,
            sheetShadowElevation = 16.dp,
            sheetTonalElevation = 16.dp,
            sheetContent = {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    AlertsSheetContent(
                        alerts = uiState.alerts,
                        onAlertClick = { viewModel.selectAlert(it) },
                        onHeaderClick = {
                            scope.launch {
                                if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                                    scaffoldState.bottomSheetState.partialExpand()
                                } else {
                                    scaffoldState.bottomSheetState.expand()
                                }
                            }
                        }
                    )
                }
            },
            sheetDragHandle = null,
            sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) { innerPadding ->
            Box(modifier = modifier.fillMaxSize()) {
                if (!isOnline) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(32.dp)
                            .zIndex(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Ingen internettforbindelse",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Kartet krever internett for å laste inn.",
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                AndroidView(
                    factory = { context ->
                        MapView(context).apply {
                            onCreate(null)
                            getMapAsync { map ->
                                mapRef = map

                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    uiState.mapCenter,
                                    uiState.mapZoom))

                                val styleUrl = "https://tiles.openfreemap.org/styles/liberty"
                                map.setStyle(Style.Builder().fromUri(styleUrl)) { style ->

                                    updateWmsLayer(
                                        style,
                                        uiState.wmsUrl,
                                        uiState.currentLayer?.name ?: "none"
                                    )
                                    updateAlertsLayer(style, uiState.showAlerts, uiState.alertsUrl)
                                }

                                map.addOnCameraIdleListener {
                                    val pos = map.cameraPosition
                                    viewModel.updateMapPosition(pos.target ?: LatLng(60.0, 11.0), pos.zoom)
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
                                                severity = feature.getStringProperty("severity")
                                                    ?: "",
                                                description = feature.getStringProperty("description")
                                                    ?: "",
                                                area = feature.getStringProperty("area") ?: "",
                                                instruction = feature.getStringProperty("instruction")
                                                    ?: "",
                                                consequence = feature.getStringProperty("consequence")
                                                    ?: "",
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

                val zoomToLocation by viewModel.zoomToLocation.collectAsStateWithLifecycle()
                LaunchedEffect(zoomToLocation) {
                    zoomToLocation?.let { location ->
                        mapRef?.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(location.latitude, location.longitude),
                                12.0
                            )
                        )
                    }
                }

                // Top Search Bar
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 48.dp, start = 16.dp, end = 16.dp)
                ) {
                    if (isSearchExpanded) {
                        SearchBar(
                            modifier = Modifier.fillMaxWidth(),
                            onPlaceSelected = { _, location ->
                                viewModel.onPlaceSelected(location)
                                isSearchExpanded = false
                            }
                        )
                    } else {
                        Surface(
                            onClick = { isSearchExpanded = true},
                            shape = CircleShape,
                            color = Color.White,
                            shadowElevation = 4.dp,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Åpne søk",
                                tint = Color.Black,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }

                if (uiState.showAlerts) {
                    AlertsLegendCard(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top = legendTopPadding, start = 16.dp)
                    )

                }

                // Legend (Top Left) - Padding adjusts based on search bar expansion
                else if (uiState.currentLayer == WeatherLayer.TEMPERATURE) {
                    TemperatureLegendCard(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top = legendTopPadding, start = 16.dp)
                    )
                } else if (uiState.currentLayer == WeatherLayer.PRECIPITATION) {
                    PrecipitationLegendCard(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top = legendTopPadding, start = 16.dp),
                    )
                } else if (uiState.currentLayer == WeatherLayer.WIND) {
                    WindLegendCard(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top = legendTopPadding, start = 16.dp),
                    )
                }

                // Layer Selection Menu
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 150.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    AnimatedVisibility(
                        visible = isMenuExpanded,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically(),
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            LayerButton(
                                "Nedbør",
                                Icons.Outlined.WaterDrop,
                                uiState.currentLayer == WeatherLayer.PRECIPITATION
                            ) {
                                viewModel.onLayerSelected(WeatherLayer.PRECIPITATION)
                                isMenuExpanded = false
                            }
                            LayerButton(
                                "Temperatur",
                                Icons.Outlined.DeviceThermostat,
                                uiState.currentLayer == WeatherLayer.TEMPERATURE
                            ) {
                                viewModel.onLayerSelected(WeatherLayer.TEMPERATURE)
                                isMenuExpanded = false
                            }
                            LayerButton(
                                "Vind",
                                Icons.Outlined.Air,
                                uiState.currentLayer == WeatherLayer.WIND
                            ) {
                                viewModel.onLayerSelected(WeatherLayer.WIND)
                                isMenuExpanded = false
                            }
                            LayerButton("Farevarsler", Icons.Default.Warning, uiState.showAlerts) {
                                viewModel.onAlertsSelected()
                                isMenuExpanded = false
                            }
                        }
                    }

                    if (isMenuExpanded) {
                        Spacer(modifier = Modifier.height(8.dp))
                        FloatingActionButton(
                            onClick = { isMenuExpanded = false },
                            containerColor = Color(0xFFB0BEC5),
                            contentColor = Color.Black,
                            shape = CircleShape,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Lukk")
                        }
                    } else {
                        val currentLabel = when {
                            uiState.showAlerts -> "Farevarsler"
                            uiState.currentLayer == WeatherLayer.PRECIPITATION -> "Nedbør"
                            uiState.currentLayer == WeatherLayer.TEMPERATURE -> "Temperatur"
                            uiState.currentLayer == WeatherLayer.WIND -> "Vind"
                            else -> "Lag"
                        }
                        val currentIcon = when {
                            uiState.showAlerts -> Icons.Default.Warning
                            uiState.currentLayer == WeatherLayer.PRECIPITATION -> Icons.Outlined.WaterDrop
                            uiState.currentLayer == WeatherLayer.TEMPERATURE -> Icons.Outlined.DeviceThermostat
                            uiState.currentLayer == WeatherLayer.WIND -> Icons.Outlined.Air
                            else -> Icons.Outlined.WaterDrop
                        }

                        Surface(
                            onClick = { isMenuExpanded = true },
                            shape = RoundedCornerShape(24.dp),
                            color = Color(0xFFE1F5FE),
                            shadowElevation = 2.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    currentIcon,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    currentLabel,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }

                // Bottom Time Slider Card
                if (!uiState.showAlerts) {
                    TimeSliderCard(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp, start = 16.dp, end = 16.dp),
                        selectionKey = uiState.currentLayer,
                        onTimeSelected = { newUtcTime ->
                            viewModel.onTimeChanged(newUtcTime)
                        }
                    )
                }
            }
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
                modifier = Modifier.height(220.dp),
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
    val precipRanges = listOf(
        "0.2", "1.0", "2.0",
        "5.0", "10.0", "15.0", "20.0",
        "25.0", "30.0", "35.0", ">40.0"
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
                itemsIndexed(precipRanges) { index, range ->
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
fun AlertsLegendCard(modifier: Modifier = Modifier) {
    val severityLevel = listOf(
        "Moderat fare",
        "Stor fare",
        "Ekstrem fare"
    )

    val colors = listOf(
        "#FFFF00",
        "#FFA500",
        "#FF0000"
    )

    Surface(
        modifier = modifier.width(160.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.9f),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(8.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "Farevarsler",
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                severityLevel.forEachIndexed { index, level ->
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    Color(android.graphics.Color.parseColor(colors[index]))
                                )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            level,
                            fontSize = 11.sp,
                            color = Color.Black
                        )
                    }
                }
            }
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
fun TimeSliderCard(
    modifier: Modifier = Modifier,
    selectionKey: Any?,
    onTimeSelected: (String) -> Unit
) {
    var sliderPosition by remember(selectionKey) { mutableFloatStateOf(0f) }
    var isPlaying by remember { mutableStateOf(false) }

    val nowUTC = Instant.now().atZone(ZoneOffset.UTC)
    val baseTime = nowUTC.withHour((nowUTC.hour / 3) * 3).truncatedTo(ChronoUnit.HOURS)

    LaunchedEffect(isPlaying, selectionKey) {
        if (isPlaying) {
            while (isPlaying) {
                delay(1500L)
                if (sliderPosition < 8f) {
                    sliderPosition += 1f
                    val currentSelectedTimeUTC = baseTime.plusHours((sliderPosition.toInt() * 3).toLong())
                    val formattedTimeUTC = currentSelectedTimeUTC.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"))
                    onTimeSelected(formattedTimeUTC)
                } else {
                    isPlaying = false
                }
            }
        }
    }

    

    val selectedTimeUTC = baseTime.plusHours((sliderPosition.toInt() * 3).toLong())

    val userZone = ZoneId.systemDefault()
    val selectedLocalTime = selectedTimeUTC.withZoneSameInstant(userZone)
    val nowLocal = nowUTC.withZoneSameInstant(userZone)
    val timeLabel = if (selectedLocalTime.toLocalDate() == nowLocal.toLocalDate()) "I dag" else "I morgen"
    val formattedTime = selectedLocalTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    val formattedTimeUTC = selectedTimeUTC.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"))

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.9f),
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("$timeLabel kl. $formattedTime", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    onClick = { isPlaying = !isPlaying },
                    shape = CircleShape,
                    color = Color(0xFFE1F5FE),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Spill av",
                        modifier = Modifier.padding(8.dp),
                        tint = Color(0xFF0288D1)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Slider(
                    value = sliderPosition,
                    onValueChange = {
                        sliderPosition = it
                        isPlaying = false // Stop animation if user moves slider manually
                    },
                    onValueChangeFinished = {
                        onTimeSelected(formattedTimeUTC)
                    },
                    valueRange = 0f..8f,
                    steps = 7,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.Black,
                        activeTrackColor = Color.Black,
                        inactiveTrackColor = Color.LightGray
                    )
                )
            }
        }
    }
}

@Composable
fun AlertsSheetContent(
    alerts: List<MetAlert>,
    onAlertClick: (MetAlert) -> Unit,
    onHeaderClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onHeaderClick() }
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = "Gjeldende farevarsler",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .align(Alignment.CenterHorizontally)
        )

        if (alerts.isEmpty()) {
            Text(
                text = "Ingen aktive varsler i dette området",
                modifier = Modifier.padding(20.dp),
                color = Color.Gray
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(alerts.size) { index ->
                    val alert = alerts[index]
                    AlertListItem(alert = alert, onClick = { onAlertClick(alert) })
                }
            }
        }
    }
}


@Composable
fun AlertListItem(
    alert: MetAlert,
    onClick: () -> Unit
) {
    val backgroundColor = when (alert.severity?.lowercase()) {
        "moderate" -> Color(0xFFFF00)
        "severe" -> Color(0xFFA500)
        "extreme" -> Color(0xFF0000)
        else -> Color(0xFFFF00)
    }

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = alert.iconUrl,
                contentDescription = "",
                modifier = Modifier
                    .size(64.dp)
                    .padding(vertical = 8.dp),
                contentScale = ContentScale.Fit,
                onState = { state ->
                    when (state) {
                        is coil3.compose.AsyncImagePainter.State.Error -> {
                            Log.e(
                                "MetAlertIcon",
                                "Feil ved lasting av ikon: ${state.result.throwable.message}"
                            )
                            Log.e("MetAlertIcon", "Prøvde å hente: ${alert.iconUrl}")
                        }

                        is coil3.compose.AsyncImagePainter.State.Success -> {
                            Log.d("MetAlertIcon", "Vellykket lasting av: ${alert.iconUrl}")
                        }

                        else -> {}
                    }
                }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${FormatEventName(alert.event)}, ${alert.area}" ?: "Farevarsel",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                    maxLines = 1
                )

                Text(
                    text = when(alert.severity?.lowercase()) {
                        "moderate" -> "Gult nivå"
                        "severe" -> "Oransje nivå"
                        "extreme" -> "Rødt nivå"
                        else -> "Ukjent nivå"
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Se detaljer",
                modifier = Modifier.rotate(-90f)
            )
        }
    }
}

private fun updateWmsLayer(style: Style, wmsUrl: String, layerId: String) {
    style.layers.filter { it.id.startsWith("victoria-") }.forEach { style.removeLayer(it) }
    style.sources.filter { it.id.startsWith("victoria-") }.forEach { style.removeSource(it) }

    if (wmsUrl.isEmpty()) return

    if (layerId == "WIND") {
        val speedUrl = wmsUrl

        addSingleLayer(style, speedUrl, "wind-speed")

        val directionUrl = wmsUrl
            .replace("wind_100m_speed", "wind_10m_vector")
            .replace("&styles=", "&styles=wind_barb")

        addSingleLayer(style, directionUrl, "wind-direction")
    } else {
        addSingleLayer(style, wmsUrl, layerId.lowercase())
    }


}

private fun addSingleLayer(style: Style, url: String, id: String) {
    val sourceId = "victoria-source-$id"
    val fullLayerId = "victoria-layer-$id"

    val tileSet = TileSet("2.1.0", url)
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
        PropertyFactory.fillColor(
            match(
                get("severity"),
                literal("Moderate"), color(AndroidColor.argb(0x60, 255, 255, 0)), // Gul med gjennomsiktighet
                literal("Severe"),   color(AndroidColor.argb(0x60, 255, 165, 0)), // Oransje
                literal("Extreme"),  color(AndroidColor.argb(0x60, 255, 0, 0)),   // Rød
                color(AndroidColor.argb(0x60, 128, 128, 128)) // Fallback (Grå)
            )
        ),
        PropertyFactory.fillOutlineColor(
            match(
                get("severity"),
                literal("Moderate"), color(AndroidColor.YELLOW),
                literal("Severe"),   color(AndroidColor.parseColor("#FFA500")),
                literal("Extreme"),  color(AndroidColor.RED),
                color(AndroidColor.GRAY)
            )
        )
    )
    style.addLayer(fillLayer)
}
