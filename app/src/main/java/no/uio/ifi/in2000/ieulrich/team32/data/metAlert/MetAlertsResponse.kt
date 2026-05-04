package no.uio.ifi.in2000.ieulrich.team32.data.metAlert

import kotlinx.serialization.Serializable

@Serializable
data class MetAlertsResponse (
    val features: List<AlertFeature>
)
