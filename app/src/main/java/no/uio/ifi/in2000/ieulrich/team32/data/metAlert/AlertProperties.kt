package no.uio.ifi.in2000.ieulrich.team32.data.metAlert

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/* trenger minst:
 - eventAwarenessname
 -description
 - consequences / instruction
 - area
 - tidsintervall
 - riskMatrixColor? */
@Serializable
data class AlertProperties(
    val area: String,
    val consequences: String,
    val eventAwarenessName: String,
    val description: String? = null,
    val severity: String,
    val event: String,
    @SerialName("when") val timeInterval: TimeInterval? = null,
    val awarenessResponse: String? = null,
    val instruction: String? = null
) {
}