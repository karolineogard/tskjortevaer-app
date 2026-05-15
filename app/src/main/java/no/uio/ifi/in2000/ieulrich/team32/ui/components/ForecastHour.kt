package no.uio.ifi.in2000.ieulrich.team32.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import no.uio.ifi.in2000.ieulrich.team32.ui.util.Format
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.BlueText

@Composable
fun ForecastHour(
    time: String,
    temp: Double,
    windSpeed: Double,
    windDirection: Double,
    precipitationAmount: Double,
    symbolCode: String,
    tempColor: Color = Color.Unspecified
)

{
    val size = 14.sp
    val imageSize = 36.dp
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                end = 16.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            model = "https://raw.githubusercontent.com/metno/weathericons/refs/heads/main/weather/svg/$symbolCode.svg",
            contentDescription = "Ikon for $symbolCode",
            modifier = Modifier
                .size(imageSize),
            contentScale = ContentScale.Fit,
            onState = { state ->
                when (state) {
                    is coil3.compose.AsyncImagePainter.State.Error -> {
                        Log.e(
                            "ForecastHourIcon",
                            "Feil ved lasting av ikon: ${state.result.throwable.message}"
                        )
                        Log.e("ForecastHourIcon", "Prøvde å hente: $symbolCode")
                    }

                    is coil3.compose.AsyncImagePainter.State.Success -> {
                        Log.d("ForecastHourIcon", "Vellykket lasting av: $symbolCode")
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
            Format.formatTemp(temp),
            fontSize = size,
            color = tempColor,
            modifier = Modifier
                .weight(1f),
            textAlign = TextAlign.Left
        )
        Spacer(modifier = Modifier.weight(0.5f))
        if (precipitationAmount > 0) {
            Text(
                text = Format.formatPrecipitation(precipitationAmount),
                fontSize = size,
                modifier = Modifier
                    .weight(1f),
                textAlign = TextAlign.Left,
                color = BlueText
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.weight(0.5f))
        Text(
            Format.formatWind(windSpeed),
            fontSize = size,
            modifier = Modifier
                .weight(1f),
            textAlign = TextAlign.Left
        )
        WindDirectionArrow(
            degrees = windDirection,
            modifier = Modifier
                .weight(0.5f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewForecastHour(){
    ForecastHour(
        time = "16:00",
        temp = 17.5,
        windSpeed = 5.0,
        precipitationAmount = 0.0,
        windDirection = 10.0,
        symbolCode = "clearsky_day")
}