package no.uio.ifi.in2000.ieulrich.team32.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.ieulrich.team32.R

@Composable
fun ForecastHour(time: String, temp: String, windSpeed: String, symbolCode: String) {
    // må finne ut hvordan jeg kan bruke symbolCode til å hente riktig bilde, prøvde med et eksempel først
    val size = 30.sp
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            time,
            fontSize = size
        )
        Text(
            temp,
            fontSize = size
        )
        Text(
            windSpeed,
            fontSize = size
        )
        Image(
            painter = painterResource(id = R.drawable.clearsky_day),
            contentDescription = symbolCode
        )

    }
}

@Preview(showBackground = true)
@Composable
fun PreviewForecastHour(){
    ForecastHour("16:00", "17", "5", "clearsky_day")
}