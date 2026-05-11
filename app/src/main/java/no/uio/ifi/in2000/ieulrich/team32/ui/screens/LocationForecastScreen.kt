package no.uio.ifi.in2000.ieulrich.team32.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import no.uio.ifi.in2000.ieulrich.team32.R
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.Format
import no.uio.ifi.in2000.ieulrich.team32.model.locationForecast.ForecastHourDetails
import no.uio.ifi.in2000.ieulrich.team32.ui.components.ForecastHour
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.MinusTekst
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.PlussTekst
import no.uio.ifi.in2000.ieulrich.team32.viewmodel.LocationForecastViewmodel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationForecastScreen(
    viewmodel: LocationForecastViewmodel,
    lat: Double?,
    lon: Double?,
    city: String = "Værvarsel",
    navController: NavController
) {
    if (lat == null || lon == null) {
        // TODO: handle null values
    } else {
        LaunchedEffect(lat, lon) {
            viewmodel.loadForecast(lat, lon)
        }
    }

    val forecast by viewmodel.forecast.collectAsStateWithLifecycle()
    val groupedByDay = viewmodel.forecastByDay.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(city, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_up_icon),
                            contentDescription = "Back",
                            modifier = Modifier.rotate(-90f)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
            )
        }
    ) { innerPadding: PaddingValues ->
        LazyColumn(
            modifier = Modifier.padding(
                top = innerPadding.calculateTopPadding(),
                start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                bottom = 0.dp
            )
        ) {
            groupedByDay.value?.forEach { (date, forecastForDay) ->
                item {
                    DayForecastCard(
                        date = date,
                        forecastForDay = forecastForDay
                    )
                }
            }
        }
    }
}


@Composable
fun DayForecastCard(
    date: String,
    forecastForDay: List<ForecastHourDetails>
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val groupedByInterval = forecastForDay.groupBy {
        Format.extractSixHourInterval(it.timestamp)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 4.dp
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            Text(
                text = date,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            if (expanded) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 4.dp
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.weight(1.3f))
                    Text(
                      text = "Tid", 
                      style = MaterialTheme.typography.bodySmall, 
                      modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.weight(0.5f))
                    Text(
                      text = "Temp", 
                      style = MaterialTheme.typography.bodySmall, 
                      modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.weight(0.5f))
                    Text(
                      text = "Regn", 
                      style = MaterialTheme.typography.bodySmall, 
                      modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.weight(0.5f))
                    Text(
                      text = "Vind", 
                      style = MaterialTheme.typography.bodySmall, 
                      modifier = Modifier.weight(1f)
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                forecastForDay.forEachIndexed { index, details ->
                    if (index > 0) HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    ForecastHour(
                        time = Format.extractHour(details.timestamp),
                        temp = Format.formatTemp(details.temperature),
                        windSpeed = Format.formatWind(details.windSpeed),
                        windDirection = details.windDirection,
                        precipitationAmount = Format.formatPrecipitation(details.precipitationAmount),
                        symbolCode = details.symbolCode,
                        compact = true,
                        tempColor = if (details.temperature <= 0.0) MinusTekst else PlussTekst
                    )

                }
            } else {
                groupedByInterval.entries.forEachIndexed { index, (interval, hours) ->
                    val maxTemp = hours.maxOf { it.temperature }
                    val avgWind = hours.map { it.windSpeed }.average()
                    val totalPrecipitation = hours.sumOf { it.precipitationAmount }
                    val symbolCode = hours.firstOrNull()?.symbolCode ?: ""
                    val imageUrl =
                        "https://raw.githubusercontent.com/metno/weathericons/main/weather/svg/$symbolCode.svg"

                    if (index > 0) HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = symbolCode,
                            modifier = Modifier
                                .size(36.dp)
                                .weight(0.8f),
                            contentScale = ContentScale.Fit
                        )
                        Text(text = interval, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1.5f))
                        Text(
                            text = Format.formatTemp(maxTemp),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (maxTemp <= 0.0) MinusTekst else PlussTekst,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = Format.formatPrecipitation(totalPrecipitation),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = Format.formatWind(avgWind),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            TextButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (expanded) "Vis mindre" else "Detaljer",
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}