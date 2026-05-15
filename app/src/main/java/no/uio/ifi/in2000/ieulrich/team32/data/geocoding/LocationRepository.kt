package no.uio.ifi.in2000.ieulrich.team32.data.geocoding

import android.location.Location
import no.uio.ifi.in2000.ieulrich.team32.data.geocoding.dto.NominatimAddress
import javax.inject.Inject


class LocationRepository @Inject constructor (
    private val api: LocationDatasource
){
    suspend fun getPlaceName(lat: Double, lon: Double): String {
        return try {
            val response = api.getPlaceName(lat, lon)
            response?.address?.bestName() ?: "Ukjent lokasjon"
        } catch (e: Throwable) {
            "Ukjent lokasjon"
        }
    }

    suspend fun getCoordinatesForName(name: String): Location?{
        return api.getCoordinatesFromName(name)
    }

    suspend fun searchPlaces(query: String): List<Pair<String, Location>> {
        return api.searchPlaces(query)
    }
}

fun NominatimAddress.bestName(): String =
    city ?: town?: suburb ?: cityDistrict ?: municipality ?: county ?: "Ukjent lokasjon"
