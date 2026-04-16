package no.uio.ifi.in2000.ieulrich.team32.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.FormatTime
import no.uio.ifi.in2000.ieulrich.team32.ui.components.ForecastHour
import no.uio.ifi.in2000.ieulrich.team32.viewmodel.LocationForecastViewmodel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationForecastScreen(
    viewmodel: LocationForecastViewmodel = viewModel(),
    lat: Double?,
    lon: Double?,
    navController: NavController // Husk å legge til denne som parameter!
) {
    if (lat == null || lon == null){
        // TODO: håndter null-verdier her
    }
    else {
        LaunchedEffect(lat, lon) {
            viewmodel.getForecast(lat, lon)
        }
    }
    val forecast by viewmodel.forecast.collectAsState()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Værvarsel", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Tilbake"
                        )
                    }
                },
                actions = {

                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {

            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Dato",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.weight(1.5f))
                Text("Tid", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text("Temp", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text("Vind", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(forecast?.properties?.timeseries ?: emptyList()) { timeseries ->
                    val time = FormatTime(timeseries.time)
                    val data = timeseries.data.instant.details
                    ForecastHour(
                        time,
                        data.airTemperature.toString(),
                        data.windSpeed.toString(),
                        timeseries.data.next1Hours?.summary?.symbolCode ?: ""
                    )
                }
            }
        }
    }

}