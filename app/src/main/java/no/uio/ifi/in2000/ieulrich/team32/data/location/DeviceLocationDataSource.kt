package no.uio.ifi.in2000.ieulrich.team32.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.ui.tooling.data.SourceContext
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import no.uio.ifi.in2000.ieulrich.team32.viewmodel.AppLocation
import javax.inject.Inject
import kotlin.coroutines.resume

class DeviceLocationDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val locationClient: FusedLocationProviderClient
) {
    suspend fun getCurrentLocation(): AppLocation? {
        val hasPermission = ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) return null

        return suspendCancellableCoroutine { continuation ->
            val cts = CancellationTokenSource()
            locationClient
                .getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cts.token)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        continuation.resume(AppLocation(lat = location.latitude, lon = location.longitude))
                    } else {
                        locationClient.lastLocation
                            .addOnSuccessListener { lastLocation ->
                                continuation.resume(
                                    lastLocation?.let { AppLocation(lat = it.latitude, lon = it.longitude) }
                                ) }
                            .addOnFailureListener { continuation.resume(null) }
                    }
                }
                .addOnFailureListener { continuation.resume(null) }
            continuation.invokeOnCancellation { cts.cancel() }
        }
    }
}