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
    bodyLarge = TextStyle(
        fontFamily = RobotoMono,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    titleLarge = TextStyle(
        fontFamily = RobotoMono,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    labelSmall = TextStyle(
        fontFamily = RobotoMono,
        fontWeight = FontWeight.Light,
        fontSize = 11.sp
    )

    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)