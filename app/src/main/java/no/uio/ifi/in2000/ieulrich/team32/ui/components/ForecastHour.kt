package no.uio.ifi.in2000.ieulrich.team32.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.ieulrich.team32.R

@Composable
fun ForecastHour(
    time: String,
    temp: String,
    windSpeed: String,
    precipitationAmount: String,
    symbolCode: String,
    compact: Boolean = false
)

{
    // TODO: må finne ut hvordan jeg kan bruke symbolCode til å hente riktig bilde, prøvde med et eksempel først
    val size = if (compact) 14.sp else 24.sp
    val imageSize = if (compact) 32.dp else 64.dp
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = painterResource(id = R.drawable.clearsky_day),
            contentDescription = symbolCode,
            modifier = Modifier.padding(top = 12.dp)
                .weight(1f)
                .size(imageSize),
            alignment = Alignment.Center
        )
        Spacer(modifier = Modifier.weight(0.5f))
        Text(
            time,
            fontSize = size,
            modifier = Modifier
                .weight(1f),
            textAlign = TextAlign.Left

        )
        Spacer(modifier = Modifier.weight(0.5f))
        Text(
            temp,
            fontSize = size,
            modifier = Modifier
                .weight(1f),
            textAlign = TextAlign.Left
        )
        Spacer(modifier = Modifier.weight(0.5f))
        Text(
            text = precipitationAmount,
            fontSize = size,
            modifier = Modifier
                .weight(1f),
            textAlign = TextAlign.Left
        )
        Spacer(modifier = Modifier.weight(0.5f))
        Text(
            windSpeed,
            fontSize = size,
            modifier = Modifier
                .weight(1f),
            textAlign = TextAlign.Left
        )


    }
}

@Preview(showBackground = true)
@Composable
fun PreviewForecastHour(){
    ForecastHour("16:00", "17°", "5 m/s", precipitationAmount = "3 mm", symbolCode = "clearsky_day")
}