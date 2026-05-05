package no.uio.ifi.in2000.ieulrich.team32.data.metAlert

import kotlinx.serialization.Serializable

@Serializable
class TimeInterval(
    val interval: List<String> // format [validFrom, validTo]
) {
    val validFrom get() = interval.getOrNull(0)
    val validTo get() = interval.getOrNull(1)
}