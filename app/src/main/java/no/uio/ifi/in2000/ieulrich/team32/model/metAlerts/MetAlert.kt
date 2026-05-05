package no.uio.ifi.in2000.ieulrich.team32.model.metAlerts

import kotlinx.serialization.Serializable

@Serializable
data class MetAlert(
    val event: String? = null,
    val severity: String? = null,
    val description: String? = null,
    val area: String? = null,
    val instruction: String? = null,
    val consequence: String? = null,
    val awarnessResponse: String? = null,
    val awarnessLevel: String? = null,
    val title: String? = null,

)

@Serializable
data class MetAlertsResponse(
    val features: List<MetFeature>
)

@Serializable
data class MetFeature(
    val properties: MetAlert
)