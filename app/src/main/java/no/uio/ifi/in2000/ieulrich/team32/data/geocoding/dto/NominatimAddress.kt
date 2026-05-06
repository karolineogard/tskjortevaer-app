package no.uio.ifi.in2000.ieulrich.team32.data.geocoding.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NominatimAddress(
    val road: String? = null,
    val suburb: String? = null,
    val city: String? = null,
    val town: String? = null,
    val county: String? = null,
    @SerialName("city_district") val cityDistrict: String? = null,
)