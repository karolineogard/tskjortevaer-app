package no.uio.ifi.in2000.ieulrich.team32.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


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
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}