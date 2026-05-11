package no.uio.ifi.in2000.ieulrich.team32.ui.screens

import android.location.Location
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.svg.SvgDecoder
import no.uio.ifi.in2000.ieulrich.team32.R
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.Format
import no.uio.ifi.in2000.ieulrich.team32.data.metAlert.AlertFeature
import no.uio.ifi.in2000.ieulrich.team32.model.clothes.ClothesRecommendation
import no.uio.ifi.in2000.ieulrich.team32.model.locationForecast.ForecastHourDetails
import no.uio.ifi.in2000.ieulrich.team32.model.locationForecast.imageUrl
import no.uio.ifi.in2000.ieulrich.team32.model.metAlerts.MetAlert
import no.uio.ifi.in2000.ieulrich.team32.model.metAlerts.iconUrl
import no.uio.ifi.in2000.ieulrich.team32.ui.Routes
import no.uio.ifi.in2000.ieulrich.team32.ui.components.SearchBar
import no.uio.ifi.in2000.ieulrich.team32.viewmodel.AppLocation
import no.uio.ifi.in2000.ieulrich.team32.viewmodel.ClothesViewModel
import no.uio.ifi.in2000.ieulrich.team32.viewmodel.HomeViewModel
import no.uio.ifi.in2000.ieulrich.team32.viewmodel.UiState
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    navController: NavController,
    clothesViewModel: ClothesViewModel,
    homeViewModel: HomeViewModel,
    isOnline: Boolean
){
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val padding = 16.dp
    var selectedAlert by remember { mutableStateOf<MetAlert?>(null) }

    selectedAlert?.let { alert ->
        AlertDetailScreen(
            alert = alert,
            onBack = { selectedAlert = null }
        )
    } ?: Box(modifier = Modifier.fillMaxSize()) {

        when (val state = uiState) {
            is UiState.Loading -> {
                if (!isOnline) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
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
                                text = "Koble til internett for å se værdata og klesanbefalinger.",
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator( color = Color.Blue)
                    }
                }
            }

            is UiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Kunne ikke hente værdata.",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Sjekk at du har internettforbindelse og prøv igjen.",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            is UiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { focusManager.clearFocus() },
                    verticalArrangement = Arrangement.spacedBy(padding),
                    contentPadding = PaddingValues(padding)
                ) {
                    item {
                        SearchBar(onPlaceSelected = { name, location ->
                            navController.navigate("forecast?lat=${location.latitude}&lon=${location.longitude}&city=$name")
                        })
                    }

                    item {
                        WeatherCard(
                            navController = navController,
                            forecastHourDetails = state.forecast,
                            placeName = state.place,
                            location = state.location
                        )
                    }

                    if (state.alerts.isNotEmpty()) {
                        item {
                            MetalertCarousel(
                                alerts = state.alerts,
                                onAlertClick = { selectedAlert = it }
                            )
                        }
                    }

                    item {
                        ClothingCard(navController = navController, clothesViewModel = clothesViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun MetalertCarousel(
    alerts: List<AlertFeature>,
    onAlertClick: (MetAlert) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(alerts) { alert ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    MetalertCard(alert = alert, onClick = onAlertClick)
                }
            }
        }
    }
}
@Composable
fun MetalertCard(
    alert: AlertFeature,
    onClick: (MetAlert) -> Unit,
    modifier: Modifier = Modifier
) {
    val metAlert = MetAlert(
        event = alert.properties.event,
        severity = alert.properties.severity,
        description = alert.properties.description,
        area = alert.properties.area,
        instruction = alert.properties.instruction,
        consequence = alert.properties.consequences,
        awarnessResponse = alert.properties.awarenessResponse,
        title = alert.properties.eventAwarenessName
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onClick(metAlert) },
        colors = CardDefaults.cardColors(
            containerColor = SeverityColor(alert.properties.severity).copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = metAlert.iconUrl,
                contentDescription = "Ikon for ${alert.properties.event}",
                modifier = Modifier.size(52.dp),
                contentScale = ContentScale.Fit
            )
            Column {
                Text(
                    text = alert.properties.eventAwarenessName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = alert.properties.area,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
            }
        }
    }
}

@Composable
fun WeatherCard(
    navController: NavController,
    forecastHourDetails: ForecastHourDetails?,
    modifier: Modifier = Modifier,
    location: AppLocation?,
    placeName: String
) {
    val svgLoader = rememberSvgImageLoader()
    Card(
        modifier = modifier.height(280.dp).fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        onClick = {
            val lat = location?.lat
            val lon = location?.lon
                navController.navigate("forecast?lat=$lat&lon=$lon&city=Min posisjon")

        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val now = LocalDateTime.now()
            val today = LocalDate.now()
            val datePart = if (now.toLocalDate().isEqual(today)) "I dag"
            else now.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            val timePart = if (forecastHourDetails == null) "00:00"
                else Format.extractTime(forecastHourDetails.timestamp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.klokke_ikon),
                    contentDescription = null,
                    modifier = Modifier.weight(0.4f)
                )
                Text(text = "$datePart $timePart", modifier = Modifier.weight(3f))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = placeName, fontSize = 40.sp)
            }
            forecastHourDetails?.let { details ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = details.imageUrl,
                        imageLoader = svgLoader,
                        contentDescription = "Ikon for ${details.symbolCode}",
                        modifier = Modifier.size(120.dp).padding(vertical = 8.dp),
                        contentScale = ContentScale.Fit,
                        onState = { state ->
                            when (state) {
                                is coil3.compose.AsyncImagePainter.State.Error -> Log.e(
                                    "WeatherCard",
                                    "Feil: ${state.result.throwable.message}"
                                )

                                is coil3.compose.AsyncImagePainter.State.Success -> Log.d(
                                    "WeatherCard",
                                    "Loaded: ${details.imageUrl}"
                                )

                                else -> {}
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = Format.formatTemp(details.temperature),
                        fontSize = 40.sp)
                }

            }
        }
    }
}



@Composable
fun ClothingCard(
    navController: NavController,
    clothesViewModel: ClothesViewModel,
    modifier: Modifier = Modifier
) {
    var showInfo by rememberSaveable { mutableStateOf(false) }
    val recommendation by clothesViewModel.recommendation.collectAsStateWithLifecycle()
    val isLoading by clothesViewModel.isLoading.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            onClick = { navController.navigate(Routes.CLOTHES) }
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Tittelrad
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(60.dp))
                    Text(
                        text = "Klesanbefaling",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { showInfo = !showInfo }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info",
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }

                // Undertekster med mindre mellomrom og riktig innrykk
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.width(60.dp))
                        Text(
                            text = "For deg som pendler:",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.width(60.dp))
                        Text(
                            text = "Basert på værmeldingen kl 08-10 og 16-18",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                HorizontalDivider()

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                    }
                } else if (recommendation != null) {
                    ClothingCardRecommendationRows(rec = recommendation!!)
                } else {
                    Text(
                        text = "Ingen værdata tilgjengelig ennå.",
                        modifier = Modifier.padding(8.dp),
                        fontSize = 14.sp
                    )
                }
            }
        }

        if (showInfo) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                ElevatedCard(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(modifier = Modifier.size(48.dp))
                            Text(
                                text = "Anbefaling",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(2f),
                                textAlign = TextAlign.Center
                            )
                            IconButton(onClick = { showInfo = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Lukk")
                            }
                        }
                        Text(
                            text = "Anbefalingen tar utgangspunkt i fremkomst til og fra skole eller jobb. Vi antar at reisetidspunktet skjer mellom 8–10 på morgenen og 16–18 på kvelden.",
                        fontSize = 15.sp
                        )
                        OutlinedButton(
                            onClick = { navController.navigate(Routes.CLOTHES) },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Jeg har andre behov",
                                color = Color.Black
                            )
                            Icon(
                                painter = painterResource(id = R.drawable.arrow_forward_icon),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun rememberSvgImageLoader(): ImageLoader{
    val context = LocalContext.current
    return remember {
        ImageLoader.Builder(context)
            .components { add(SvgDecoder.Factory()) }
            .build()
    }
}

@Composable
private fun ClothingCardRecommendationRows(rec: ClothesRecommendation) {
    Column(
        modifier = Modifier.padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)

    ) {
        // Hovedbekledning (Overdel og underdel)
        val upper = when {
            rec.wearHeavyJacket -> "Tykk jakke"
            rec.wearLightJacket -> "Lett jakke"
            rec.wearSweater -> "Genser"
            rec.wearTshirt -> "T-skjorte"
            else -> "Lett bekledning"
        }
        val lower = if (rec.wearShorts) "shorts" else "lange bukser"
        val clothingSummary = "$upper og $lower"
        
        val rainsuggestion = if (rec.bringUmbrella || rec.wearRainGear) {
            "Det kan være lurt å ha med paraply eller regnjakke"
        } else {
            "Det er ikke nødvendig å ta med paraply eller regnjakke"
        }

        ClothingCardRow(
            iconRes = R.drawable.henger,
            text = clothingSummary
        )

        ClothingCardRow(
            iconRes = R.drawable.paraply,
            text = rainsuggestion
        )
    }
}

@Composable
private fun ClothingCardRow(iconRes: Int, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            lineHeight = 28.sp,
        )
    }
}
