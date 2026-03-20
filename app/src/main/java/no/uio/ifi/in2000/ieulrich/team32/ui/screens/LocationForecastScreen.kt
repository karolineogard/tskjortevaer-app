package no.uio.ifi.in2000.ieulrich.team32.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.FormatTime
import no.uio.ifi.in2000.ieulrich.team32.ui.components.ForecastHour
import no.uio.ifi.in2000.ieulrich.team32.viewmodel.LocationForecastViewmodel

@Composable
fun LocationForecastScreen(
    viewmodel: LocationForecastViewmodel = viewModel(),
    lat: Double,
    lon: Double
) {
    LaunchedEffect(lat, lon) {
        viewmodel.getForecast(lat, lon)
    }
    val forecast by viewmodel.forecast.collectAsState()

    Box(){
        LazyColumn() {
            items(forecast?.properties?.timeseries ?: emptyList()) { timeseries ->
                val time = FormatTime(timeseries.time)
                val data = timeseries.data.instant.details
                ForecastHour(time, data.airTemperature.toString(), data.windSpeed.toString(), timeseries.data.next1Hours?.summary?.symbolCode ?: "")
            }
        }
    }
}