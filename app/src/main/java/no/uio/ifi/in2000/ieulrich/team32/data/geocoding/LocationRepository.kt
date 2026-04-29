package no.uio.ifi.in2000.ieulrich.team32.data.geocoding

import io.ktor.utils.io.errors.IOException
import no.uio.ifi.in2000.ieulrich.team32.data.geocoding.dto.NominatimAddress


class LocationRepository (
    private val api: LocationDatasource = LocationDatasource()
){
    suspend fun getPlaceName(lat: Double, lon: Double): String {
        return try {
            val response = api.getPlaceName(lat, lon)
            response.address.bestName()
        } catch (e: IOException){
            "Unknown"
        }
    }
}

fun NominatimAddress.bestName(): String =
    city ?: suburb ?: cityDistrict ?: "Unknown"
