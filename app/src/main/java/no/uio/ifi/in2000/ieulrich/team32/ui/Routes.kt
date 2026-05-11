package no.uio.ifi.in2000.ieulrich.team32.ui

import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.vector.ImageVector
import no.uio.ifi.in2000.ieulrich.team32.R

object Routes {
    const val MAP = "map"
    const val HOME = "home"
    const val SETTINGS = "settings"
    const val CLOTHES = "clothes"

    const val SPLASH = "splash"

}


enum class Destination(
    val route: String,
    val icon: Int,
    val label: String,
    val contentDescription: String
){
    HOME(route=Routes.HOME, icon = R.drawable.hjem_ikon, label = "Hjem", contentDescription = "hjem"),
    MAP(route=Routes.MAP, icon = R.drawable.kart_ikon, label = "Kart", contentDescription = "kart"),
    CLOTHES(route=Routes.CLOTHES, icon = R.drawable.kl_r_ikon, label = "Klær", contentDescription = "klær"),
    SETTINGS(route=Routes.SETTINGS, icon = R.drawable.innstillinger_ikon, label = "Innstillinger", contentDescription = "instillinger"),

}