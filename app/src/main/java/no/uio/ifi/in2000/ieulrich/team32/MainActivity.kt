package no.uio.ifi.in2000.ieulrich.team32

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.svg.SvgDecoder
import dagger.hilt.android.AndroidEntryPoint
import no.uio.ifi.in2000.ieulrich.team32.ui.MapApp
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.Team32Theme
import no.uio.ifi.in2000.ieulrich.team32.viewmodel.HomeViewModel
import no.uio.ifi.in2000.ieulrich.team32.viewmodel.LocationForecastViewmodel
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: LocationForecastViewmodel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission())
    { granted ->
        if (granted) {
            viewModel.loadForecastForDevice(this)
            homeViewModel.loadData(this)
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
                MapApp()
            }
        }
    }
}