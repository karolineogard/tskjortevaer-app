package no.uio.ifi.in2000.ieulrich.team32.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import no.uio.ifi.in2000.ieulrich.team32.R
import no.uio.ifi.in2000.ieulrich.team32.ui.util.Format
import no.uio.ifi.in2000.ieulrich.team32.model.locationForecast.ForecastHourDetails
import no.uio.ifi.in2000.ieulrich.team32.ui.components.ForecastHour
import no.uio.ifi.in2000.ieulrich.team32.ui.components.TopAppBar
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.BlueText
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.RedText
import no.uio.ifi.in2000.ieulrich.team32.viewmodel.LocationForecastUiState
import no.uio.ifi.in2000.ieulrich.team32.viewmodel.LocationForecastViewmodel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationForecastScreen(
    lat: Double?,
    lon: Double?,
    navController: NavController
) {
    val viewModel: LocationForecastViewmodel = hiltViewModel()
    if (lat == null || lon == null) {
        ErrorScreen()
    } else {
        LaunchedEffect(lat, lon) {
            viewModel.loadForecast(lat, lon)
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is LocationForecastUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            }
        }

        is LocationForecastUiState.Error -> {
            ErrorScreen()
        }

        is LocationForecastUiState.Success -> {
            val forecastByDay = state.forecastByDay
            val placeName = state.placeName

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = placeName,
                        onBack = { navController.popBackStack() }
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
                    forecastByDay.forEach { (date, forecastForDay) ->
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
                    Spacer(modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.weight(0.5f))

                    Text(
                      text = stringResource(R.string.forecast_time),
                      style = MaterialTheme.typography.bodySmall, 
                      modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.weight(0.5f))
                    Text(
                      text = stringResource(R.string.forecast_temp),
                      style = MaterialTheme.typography.bodySmall, 
                      modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.weight(0.5f))
                    Text(
                      text = stringResource(R.string.forecast_precipitation),
                      style = MaterialTheme.typography.bodySmall, 
                      modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.weight(0.5f))
                    Text(
                      text = stringResource(R.string.forecast_wind),
                      style = MaterialTheme.typography.bodySmall, 
                      modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.weight(0.5f))
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                forecastForDay.forEachIndexed { index, details ->
                    if (index > 0) HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    ForecastHour(
                        time = Format.extractHour(details.timestamp),
                        temp = details.temperature,
                        windSpeed = details.windSpeed,
                        windDirection = details.windDirection,
                        precipitationAmount = details.precipitationAmount,
                        symbolCode = details.symbolCode,
                        tempColor = if (details.temperature <= 0.0) BlueText else RedText,
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
                            color = if (maxTemp <= 0.0) BlueText else RedText,
                            modifier = Modifier.weight(1f)
                        )
                        if (totalPrecipitation > 0) {
                            Text(
                                text = Format.formatPrecipitation(totalPrecipitation),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f),
                                color = BlueText
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
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
                    if (expanded) stringResource(R.string.forecast_minimize) else stringResource(R.string.forecast_details),
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