package no.uio.ifi.in2000.ieulrich.team32.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.LightColorScheme

private val DarkColorScheme = darkColorScheme(

)

private val LightColorScheme = lightColorScheme(


    primary = LightBlue,
    onPrimary = Grey,

    background = SuperLightBlue,
    onBackground = Grey,

    surface = Color.White,
    onSurface = Grey,
    surfaceVariant = LightBlue,
    onSurfaceVariant = Grey,


    secondaryContainer = MediumBlue,
    onSecondaryContainer = Grey,

    surfaceContainer = LightBlue,
    surfaceContainerLow = LightBlue,
    surfaceContainerHigh = LightBlue,

)





@Composable
fun Team32Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Sørg for at denne står til false for å tvinge dine egne farger!
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
//    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }
//
//        darkTheme -> DarkColorScheme
//        else -> LightColorScheme
//    }

    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}