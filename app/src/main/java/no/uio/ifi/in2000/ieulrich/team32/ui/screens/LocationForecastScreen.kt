package no.uio.ifi.in2000.ieulrich.team32.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.Format
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.dto.TimeSeries
import no.uio.ifi.in2000.ieulrich.team32.ui.components.ForecastHour
import no.uio.ifi.in2000.ieulrich.team32.viewmodel.LocationForecastViewmodel
import kotlin.collections.emptyList


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

    val forecast by viewmodel.forecast.collectAsState()
    val groupedByDay = (forecast?.properties?.timeseries ?: emptyList())
        .groupBy { Format.extractDate(it.time) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(city, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                expandedHeight = 32.dp
            )
        }
    ) { innerPadding: PaddingValues ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding)
        ) {
            groupedByDay.entries.forEach { (date, timeseriesForDay) ->
                item {
                    DayForecastCard(date = date, timeseries = timeseriesForDay)
                }
            }
        }
    }
}


@Composable
fun DayForecastCard(date: String, timeseries: List<TimeSeries>) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val groupedByInterval = timeseries.groupBy {
        Format.extractSixHourInterval(it.time)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            Text(
                text = date,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            if (expanded) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.weight(1.3f))
                    Text("Tid", fontSize = 13.sp, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.weight(0.5f))
                    Text("Temp", fontSize = 13.sp, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.weight(0.5f))
                    Text("Regn", fontSize = 13.sp, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.weight(0.5f))
                    Text("Vind", fontSize = 13.sp, modifier = Modifier.weight(1f))
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                timeseries.forEachIndexed { index, ts ->
                    val symbolCode = ts.data.next1Hours?.summary?.symbolCode ?: ""
                    if (index > 0) HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    ForecastHour(
                        time = Format.extractHour(ts.time),
                        temp = Format.formatTemp("%.0f".format(ts.data.instant.details.airTemperature)),
                        windSpeed = Format.formatWind("%.0f".format(ts.data.instant.details.windSpeed)),
                        precipitationAmount = Format.formatPrecipitation(
                            "%.0f".format(ts.data.next1Hours?.details?.precipitationAmount ?: 0.0)
                        ),
                        symbolCode = symbolCode,
                        compact = true
                    )

                }
            } else {
                groupedByInterval.entries.forEachIndexed { index, (interval, hours) ->
                    val maxTemp = hours.map { it.data.instant.details.airTemperature }.max()
                    val avgWind = hours.map { it.data.instant.details.windSpeed }.average()
                    val totalPrecipitation = hours.sumOf {
                        it.data.next1Hours?.details?.precipitationAmount ?: 0.0
                    }
                    val symbolCode =
                        hours.firstOrNull()?.data?.next1Hours?.summary?.symbolCode ?: ""
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
                        Text(text = interval, fontSize = 13.sp, modifier = Modifier.weight(1.5f))
                        Text(
                            text = Format.formatTemp("%.0f".format(maxTemp)),
                            fontSize = 13.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = Format.formatPrecipitation("%.0f".format(totalPrecipitation)),
                            fontSize = 13.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = Format.formatWind("%.0f".format(avgWind)),
                            fontSize = 13.sp,
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