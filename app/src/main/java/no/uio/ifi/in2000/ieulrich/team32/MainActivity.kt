package no.uio.ifi.in2000.ieulrich.team32

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.svg.SvgDecoder
import com.google.android.gms.location.LocationServices
import no.uio.ifi.in2000.ieulrich.team32.ui.MapApp
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.Team32Theme
import no.uio.ifi.in2000.ieulrich.team32.viewmodel.LocationForecastViewmodel
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer
import java.util.jar.Manifest

// ingrid var her
class MainActivity : ComponentActivity() {
    private val viewModel: LocationForecastViewmodel by viewModels {
        object : ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val locationClient = LocationServices.getFusedLocationProviderClient(this@MainActivity)
                @Suppress("UNCHECKED_CAST")
                return LocationForecastViewmodel(locationClient) as T
            }
        }
    }

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission())
    { granted ->
        if (granted) {
            viewModel.loadForecastForDevice(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        SingletonImageLoader.setSafe { context ->
            ImageLoader.Builder(context)
                .components {
                    add(OkHttpNetworkFetcherFactory())
                    add(SvgDecoder.Factory())
                }
                .build()
        }

        // Initialize MapLibre before setContent
        MapLibre.getInstance(this, null, WellKnownTileServer.MapLibre)
        // request location permission
        locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        enableEdgeToEdge()
        setContent {
            Team32Theme {
                MapApp(locationForecastViewmodel = viewModel)
            }
        }
    }
}