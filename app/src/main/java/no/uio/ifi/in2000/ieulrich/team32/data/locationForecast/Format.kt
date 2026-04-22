package no.uio.ifi.in2000.ieulrich.team32.data.locationForecast

import androidx.compose.ui.res.stringResource
import no.uio.ifi.in2000.ieulrich.team32.R
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object Format {
    const val degreeSign = "°"
    const val precipitationSuffix = " mm"
    const val windSuffix = " m/s"

    fun extractDate(time: String): String {
        val parsed = Instant.parse(time).atZone(ZoneId.systemDefault())
        return parsed.format(DateTimeFormatter.ofPattern("DD.MM"))

    }

    fun extractHour(time: String): String {
        val parsed = Instant.parse(time).atZone(ZoneId.systemDefault())
        return parsed.format(DateTimeFormatter.ofPattern("HH"))
    }

    fun formatTemp(temp: String): String {
        return temp + degreeSign
    }

    fun formatPrecipitation(amount: String): String{
        return amount + precipitationSuffix
    }

    fun formatWind(speed: String): String{
        return speed + windSuffix
    }
}