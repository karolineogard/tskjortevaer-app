package no.uio.ifi.in2000.ieulrich.team32.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.Format
import androidx.navigation.NavController
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.dto.Instant
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.dto.TimeSeries
import no.uio.ifi.in2000.ieulrich.team32.ui.components.ForecastHour
import no.uio.ifi.in2000.ieulrich.team32.viewmodel.LocationForecastViewmodel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.collections.emptyList
import kotlin.math.exp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationForecastScreen(
    viewmodel: LocationForecastViewmodel = viewModel(),
    lat: Double?,
    lon: Double?,
    city: String = "Værvarsel",
    navController: NavController
) {



    if (lat == null || lon == null) {
        // TODO: håndter null-verdier her
    } else {
        LaunchedEffect(lat, lon) {
            viewmodel.getForecast(lat, lon)
        }
    }
    val forecast by viewmodel.forecast.collectAsState()
    val groupedByDay = (forecast?.properties?.timeseries ?: emptyList())
        .groupBy{Format.extractDate(it.time)}
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(city, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Tilbake"
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
            modifier = Modifier.padding(innerPadding)  // <- legg til denne
        ) {
            groupedByDay.entries.forEach { (dato, timeseriesForDag) ->
                item {
                    DayForecastCard(dato = dato, timeseries = timeseriesForDag)
                }
            }
        }
            }


}


@Composable
fun DayForecastCard(dato: String, timeseries: List<TimeSeries>) {
    var expanded by remember {mutableStateOf(false)}

    val groupedByInterval = timeseries.groupBy{
        Format.extractSixHourInterval(it.time)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface // hvit i ditt scheme
        )
    ){
       Column(

       ){
           Text(
               text = dato,
               fontSize = 20.sp,
               fontWeight = FontWeight.Bold,
               modifier = Modifier.padding(16.dp)
           )

           if (!expanded){
               groupedByInterval.forEach { (intervall, timer) ->
                   val temps = timer.map{it.data.instant.details.airTemperature}

                   val avgTemp = timer.map { it.data.instant.details.airTemperature }.average()
                   val avgWind = timer.map { it.data.instant.details.windSpeed }.average()
                   val totalNedbør = timer.sumOf {
                       it.data.next1Hours?.details?.precipitationAmount ?: 0.0
                   }

                   Row(
                       modifier = Modifier
                           .fillMaxWidth()
                           .padding(horizontal = 16.dp, vertical = 8.dp),
                       horizontalArrangement = Arrangement.SpaceBetween
                   ){
                       Text(text = intervall, modifier = Modifier.weight(1f))
                       Text(text = Format.formatTemp("%.1f".format(avgTemp)), modifier = Modifier.weight(1f))
                       Text(text = Format.formatPrecipitation("%.1f".format(totalNedbør)), modifier = Modifier.weight(1f))
                       Text(text = Format.formatWind("%.1f".format(avgWind)), modifier = Modifier.weight(1f))

                   }
               }
           }else {
               timeseries.forEach { ts ->
                   val time = Format.extractHour(ts.time)
                   val instantDetails = ts.data.instant.details
                   val nedbør = ts.data.next1Hours?.details?.precipitationAmount
                   val symbolCode = ts.data.next1Hours?.summary?.symbolCode ?: ""

                   ForecastHour(
                       time = time,
                       temp = Format.formatTemp(instantDetails.airTemperature.toString()),
                       windSpeed = Format.formatWind(instantDetails.windSpeed.toString()),
                       symbolCode = symbolCode,
                       precipitationAmount = Format.formatPrecipitation(nedbør.toString()),
                       compact = true
                   )
               }
           }

               TextButton(
                   onClick = { expanded = !expanded },
                   modifier = Modifier.fillMaxWidth()
               ) {
                   Text(if (expanded) "Vis mindre" else "Detaljer")
                   Icon(
                       imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                       contentDescription = null
                   )
               }
           }

       }
}


