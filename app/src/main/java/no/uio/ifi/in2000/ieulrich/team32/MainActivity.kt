package no.uio.ifi.in2000.ieulrich.team32

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import no.uio.ifi.in2000.ieulrich.team32.ui.MapApp
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.Team32Theme
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer

// ingrid var her
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Coil 3 with OkHttp for network support
        SingletonImageLoader.setSafe { context ->
            ImageLoader.Builder(context)
                .components {
                    add(OkHttpNetworkFetcherFactory())
                }
                .build()
        }

        // Initialize MapLibre before setContent
        MapLibre.getInstance(this, null, WellKnownTileServer.MapLibre)
        
        enableEdgeToEdge()
        setContent {
            Team32Theme {
                MapApp()
            }
        }
    }
}
