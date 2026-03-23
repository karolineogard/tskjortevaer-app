package no.uio.ifi.in2000.ieulrich.team32.data.locationForecast

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun FormatTime(time: String): String {
    return Instant.parse(time)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("HH:mm")) ?: ""
}