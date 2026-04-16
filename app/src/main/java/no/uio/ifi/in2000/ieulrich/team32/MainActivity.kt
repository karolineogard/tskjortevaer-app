package no.uio.ifi.in2000.ieulrich.team32

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import no.uio.ifi.in2000.ieulrich.team32.ui.screens.LocationForecastScreen
import no.uio.ifi.in2000.ieulrich.team32.ui.MapApp
import no.uio.ifi.in2000.ieulrich.team32.ui.screens.MapScreen
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.Team32Theme
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer

// ingrid var her
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        

        MapLibre.getInstance(this, null, WellKnownTileServer.MapLibre)
        
        enableEdgeToEdge()
        setContent {
            Team32Theme {
                MapApp()
            }
        }
    }
}
