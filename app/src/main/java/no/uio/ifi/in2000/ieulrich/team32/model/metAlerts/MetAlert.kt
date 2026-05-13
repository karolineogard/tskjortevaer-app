package no.uio.ifi.in2000.ieulrich.team32.model.metAlerts

import android.util.Log
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.maplibre.geojson.Feature

@Serializable
data class MetAlert(
    val event: String? = null,
    val severity: String? = null,
    val description: String? = null,
    val area: String? = null,
    val instruction: String? = null,
    val consequences: String? = null,
    val eventAwarenessName: String? = null,
    val awarenessResponse: String? = null,
    @SerialName("awareness_level") val awarenessLevel: String? = null,
    val title: String? = null,
    val timeInterval: TimeInterval? = null

)

@Serializable
data class TimeInterval(
    val interval: List<String> = emptyList()
) {
    val validFrom get() = interval.getOrNull(0)
    val validTo get() = interval.getOrNull(1)
}

@Serializable
data class MetAlertsResponse(
    val features: List<MetFeature>
)

@Serializable
data class MetFeature(
    val properties: MetAlert,
    @SerialName("when") val timeInterval: TimeInterval? = null
){
    fun toMetAlert() = properties.copy(timeInterval = timeInterval)
}

fun Feature.toMetAlert(): MetAlert? {
    return try {
        Json { ignoreUnknownKeys = true }
            .decodeFromString<MetFeature>(this.toJson())
            .toMetAlert()
    } catch (e: Exception) {
        Log.e("Feature", "Failed to parse alert feature", e)
        null
    }
}