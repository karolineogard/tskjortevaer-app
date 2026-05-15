package no.uio.ifi.in2000.ieulrich.team32.ui.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.svg.SvgDecoder
import no.uio.ifi.in2000.ieulrich.team32.R
import no.uio.ifi.in2000.ieulrich.team32.ui.util.Format
import no.uio.ifi.in2000.ieulrich.team32.model.clothes.ClothesRecommendation
import no.uio.ifi.in2000.ieulrich.team32.model.location.AppLocation
import no.uio.ifi.in2000.ieulrich.team32.model.locationForecast.ForecastHourDetails
import no.uio.ifi.in2000.ieulrich.team32.model.locationForecast.imageUrl
import no.uio.ifi.in2000.ieulrich.team32.model.metAlerts.MetAlert
import no.uio.ifi.in2000.ieulrich.team32.model.metAlerts.iconUrl
import no.uio.ifi.in2000.ieulrich.team32.ui.Routes
import no.uio.ifi.in2000.ieulrich.team32.ui.components.SearchBar
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.DarkBlue
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.FarevarselGulGjennomsiktig
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.FarevarselOranjeGjennomsiktig
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.FarevarselRødGjennomsiktig
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.MediumBlue
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.MinusTekst
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.PlussTekst
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
    val recommendation by clothesViewModel.recommendation.collectAsStateWithLifecycle()
    val isLoading by clothesViewModel.isLoading.collectAsStateWithLifecycle()

    selectedAlert?.let { alert ->
        AlertDetailScreen(
            alert = alert,
            onBack = { selectedAlert = null }
        )
    } ?: Box(modifier = Modifier.fillMaxSize()) {

        when (val state = uiState) {
            is UiState.Loading -> {
                if (!isOnline) {
                  ErrorScreen()
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = DarkBlue
                        )
                    }
                }
            }

            is UiState.Error -> {
                ErrorScreen()
            }

            is UiState.Success -> {
                LaunchedEffect(state.location) {
                    state.location?.let {
                        clothesViewModel.updateLocation(it.lat, it.lon)
                    }
                }
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
                        ClothingCard(
                            navController = navController,
                            recommendation = recommendation,
                            isLoading =  isLoading
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetalertCarousel(
    alerts: List<MetAlert>,
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
    alert: MetAlert,
    onClick: (MetAlert) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onClick(alert) },
        colors = CardDefaults.cardColors(
            containerColor = when (alert.severity?.lowercase()) {
                "moderate" -> FarevarselGulGjennomsiktig
                "severe"   -> FarevarselOranjeGjennomsiktig
                "extreme"  -> FarevarselRødGjennomsiktig
                else       -> Color.LightGray
            }
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
                model = alert.iconUrl,
                contentDescription = "Ikon for ${alert.event}",
                modifier = Modifier.size(52.dp),
                contentScale = ContentScale.Fit
            )
            Column {
                Text(
                    text = alert.eventAwarenessName ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = alert.area ?: "",
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
        colors = CardDefaults.cardColors(containerColor = White),
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
                Text(
                    text = placeName,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
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
                    val tempColor = if (details.temperature <= 0.0) MinusTekst else PlussTekst
                    Text(
                        text = Format.formatTemp(details.temperature),
                        style = MaterialTheme.typography.titleLarge,
                        color = tempColor)
                }

            }
        }
    }
}



@Composable
fun ClothingCard(
    modifier: Modifier = Modifier,
    navController: NavController,
    recommendation: ClothesRecommendation?,
    isLoading: Boolean = false,
) {
    var showInfo by rememberSaveable { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = White),
            onClick = { navController.navigate(Routes.CLOTHES) }
        ) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Tittelrad
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.home_outfit),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(2f),
                    )
                    IconButton(onClick = { showInfo = !showInfo }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = stringResource(R.string.info_button),
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
                        Text(
                            text = stringResource(R.string.home_clothing_explanation_title),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(R.string.home_clothing_explanation_text),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                HorizontalDivider()

                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(32.dp))
                        }
                    }
                    recommendation != null -> {
                        ClothingCardRecommendationRows(rec = recommendation)
                    }
                    else -> {
                        Text(
                            text = stringResource(R.string.home_no_recommendation),
                            modifier = Modifier.padding(8.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
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
                                text = stringResource(R.string.home_recommendation_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(2f),
                                textAlign = TextAlign.Center
                            )
                            IconButton(onClick = { showInfo = false }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.close_button)
                                )
                            }
                        }
                        Text(
                            text = stringResource(R.string.home_info_text),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        OutlinedButton(
                            onClick = { navController.navigate("${Routes.CLOTHES}?expandSheet=true") },
                            border = BorderStroke(2.dp, MediumBlue),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(R.string.home_preferences),
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
            rec.wearHeavyJacket -> stringResource(R.string.home_heavy_jacket)
            rec.wearLightJacket -> stringResource(R.string.home_light_jacket)
            rec.wearSweater -> stringResource(R.string.home_sweatshirt)
            rec.wearTshirt -> stringResource(R.string.home_tshirt)
            else -> stringResource(R.string.home_upper_default)
        }
        val lower = if (rec.wearShorts) stringResource(R.string.home_shorts)
         else stringResource(R.string.home_pants)
        val clothingSummary = stringResource(R.string.home_outfit_summary, upper, lower)

        val rainsuggestion = if (rec.bringUmbrella || rec.wearRainGear) {
            stringResource(R.string.home_rain)
        } else {
            stringResource(R.string.home_no_rain)
        }

        val rainIcon = if (rec.bringUmbrella || rec.wearRainGear) {
            R.drawable.paraply
        } else {
            R.drawable.ikke_paraply
        }

        ClothingCardRow(
            iconRes = R.drawable.henger,
            text = clothingSummary
        )

        ClothingCardRow(
            iconRes = rainIcon,
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
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 28.sp,
        )
    }
}
