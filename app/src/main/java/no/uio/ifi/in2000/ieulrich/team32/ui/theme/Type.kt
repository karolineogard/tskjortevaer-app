package no.uio.ifi.in2000.ieulrich.team32.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import no.uio.ifi.in2000.ieulrich.team32.R

// Set of Material typography styles to start with

val RobotoMono = FontFamily(
    Font(R.font.roboto_mono_regular, FontWeight.Normal),
    Font(R.font.roboto_mono_medium, FontWeight.Medium),
    Font(R.font.roboto_mono_semi_bold, FontWeight.SemiBold),
    Font(R.font.roboto_mono_bold, FontWeight.Bold),
    Font(R.font.roboto_mono_light, FontWeight.Light),
    Font(R.font.roboto_mono_extra_light, FontWeight.ExtraLight),

    // Italic-variantene (Android kobler disse selv hvis du ber om Italic)
    Font(R.font.roboto_mono_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.roboto_mono_medium_italic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.roboto_mono_bold_italic, FontWeight.Bold, FontStyle.Italic)
)


val Typography = Typography(
    titleLarge = TextStyle(
        fontFamily = RobotoMono,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    ),
    titleMedium = TextStyle(
        fontFamily = RobotoMono,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    titleSmall = TextStyle(
        fontFamily = RobotoMono,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = RobotoMono,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = RobotoMono,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = RobotoMono,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    labelLarge = TextStyle(
        fontFamily = RobotoMono,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = RobotoMono,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    labelSmall = TextStyle(
        fontFamily = RobotoMono,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp
    )
)