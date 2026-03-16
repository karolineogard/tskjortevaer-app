package no.uio.ifi.in2000.ieulrich.team32.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style

@Composable
fun MapScreen(modifier: Modifier = Modifier){

    val context = LocalContext.current
    Box(modifier = Modifier
        .fillMaxSize()
    ){
        AndroidView(factory = {
            MapView(context).apply {
                getMapAsync { map ->
                    val styleUrl = "https://tiles.openfreemap.org/styles/liberty"
                    map.setStyle(Style.Builder().fromUri(styleUrl)){ style ->
                        //skal inneholde URL og startpunkt
                    }

                }
            }
        })
    }

}