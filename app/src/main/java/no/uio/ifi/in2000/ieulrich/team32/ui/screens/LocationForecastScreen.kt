package no.uio.ifi.in2000.ieulrich.team32.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.Format
import no.uio.ifi.in2000.ieulrich.team32.ui.components.ForecastHour
import no.uio.ifi.in2000.ieulrich.team32.viewmodel.LocationForecastViewmodel

@Composable
fun LocationForecastScreen(
    viewmodel: LocationForecastViewmodel = viewModel(),
    lat: Double?,
    lon: Double?
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

    Column (){
        Row(
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Dato",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Row (
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.weight(1.5f))
            Text(
                "Tid",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Left
            )
            Spacer(modifier = Modifier.weight(0.5f))
            Text(
                "Temp",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Left
            )
            Spacer(modifier = Modifier.weight(0.5f))
            Text(
                "Nedbør",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Left
                )
            Spacer(modifier = Modifier.weight(0.5f))
            Text(
                "Vind",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Left
            )

        }
        LazyColumn() {
            items(forecast?.properties?.timeseries ?: emptyList()) { timeseries ->
                val time = Format.extractHour(timeseries.time)
                val instantDetails = timeseries.data.instant.details
                val next1HoursDetails = timeseries.data.next1Hours?.details?.precipitationAmount
                val symbolCode = timeseries.data.next1Hours?.summary?.symbolCode ?: ""
                ForecastHour(
                    time,
                    temp = instantDetails.airTemperature.toString(),
                    windSpeed = instantDetails.windSpeed.toString(),
                    symbolCode = symbolCode,
                    precipitationAmount = next1HoursDetails.toString()
                )
            }
        }
    }
}