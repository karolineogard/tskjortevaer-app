package no.uio.ifi.in2000.ieulrich.team32.ui.components

import android.util.Log
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import no.uio.ifi.in2000.ieulrich.team32.R
import no.uio.ifi.in2000.ieulrich.team32.model.locationForecast.ForecastHourDetails
import no.uio.ifi.in2000.ieulrich.team32.model.locationForecast.imageUrl

@Composable
fun ForecastHour(
    time: String,
    temp: String,
    windSpeed: String,
    precipitationAmount: String,
    symbolCode: String,
    compact: Boolean = false,
    forecastHourDetails: ForecastHourDetails?=null
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

        AsyncImage(
            model = "https://raw.githubusercontent.com/metno/weathericons/refs/heads/main/weather/svg/$symbolCode.svg",
            contentDescription = "Ikon for ${forecastHourDetails?.symbolCode}",
            modifier = Modifier
                .size(60.dp)
                .padding(vertical = 8.dp),
            contentScale = ContentScale.Fit,
            onState = { state ->
                when (state) {
                    is coil3.compose.AsyncImagePainter.State.Error -> {
                        Log.e(
                            "MetAlertIcon",
                            "Feil ved lasting av ikon: ${state.result.throwable.message}"
                        )
                        Log.e("MetAlertIcon", "Prøvde å hente: ${forecastHourDetails?.imageUrl}")
                    }

                    is coil3.compose.AsyncImagePainter.State.Success -> {
                        Log.d("MetAlertIcon", "Vellykket lasting av: ${forecastHourDetails?.imageUrl}")
                    }

                    else -> {}
                }
            }
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

//@Preview(showBackground = true)
//@Composable
//fun PreviewForecastHour(){
//    ForecastHour("16:00", "17°", "5 m/s", precipitationAmount = "3 mm", symbolCode = "clearsky_day")
//}