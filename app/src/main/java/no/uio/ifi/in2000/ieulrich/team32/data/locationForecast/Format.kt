package no.uio.ifi.in2000.ieulrich.team32.data.locationForecast


import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.text.format


object Format {
    const val degreeSign = "°"
    const val precipitationSuffix = " mm"
    const val windSuffix = " m/s"


    fun extractHour(time: String): String {
        val parsed = Instant.parse(time).atZone(ZoneId.systemDefault())
        return parsed.format(DateTimeFormatter.ofPattern("HH"))
    }

    fun formatTemp(temp: String): String {
        return temp + degreeSign
    }

    fun formatPrecipitation(amount: String): String{
        return amount
    }

    fun formatWind(speed: String): String{
        return speed + windSuffix
    }

    fun extractDate(time: String): String{
        val parsed =Instant.parse(time).atZone(ZoneId.systemDefault()).toLocalDate()
        val today = LocalDate.now(ZoneId.systemDefault())
        return when(parsed){
            today -> "I dag"
            today.plusDays(1) -> "I morgen"
            else -> {
                val formatter = DateTimeFormatter.ofPattern("EEEE d. MMMM", Locale("no"))
                parsed.format(formatter).replaceFirstChar{it.uppercase()}
            }
        }
    }

    fun extractDayName(time: String): String{
        val parsed = Instant.parse(time).atZone(ZoneId.systemDefault())
        return parsed.format((DateTimeFormatter.ofPattern("EEEE", Locale("no"))))
    }

    fun extractSixHourInterval(time: String): String{
        val parsed = Instant.parse(time).atZone(ZoneId.systemDefault())
        val hour = parsed.hour
        val intervalStart = (hour/6)*6
        val intervalEnd = intervalStart + 6
        return "%02d - %02d".format(intervalStart, intervalEnd)
    }

    fun extractTime(time: String): String{
        val parsed = Instant.parse(time).atZone(ZoneId.systemDefault())
        return parsed.format(DateTimeFormatter.ofPattern("HH:mm"))
    }
}