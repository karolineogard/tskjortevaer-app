package no.uio.ifi.in2000.ieulrich.team32.data.location

import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.suspendCancellableCoroutine

interface LocationProvider{
    suspend fun getDeviceLocation(): Location?
}

class DefaultLocationProvider(
    private val fusedClient: FusedLocationProviderClient
) : LocationProvider {
    override suspend fun getDeviceLocation(): Location? {
        return suspendCancellableCoroutine { cont ->
            fusedClient.lastLocation
                .addOnSuccessListener { cont.resume(it, null) }
                .addOnFailureListener { cont.resume(null, null) }
        }
    }
}