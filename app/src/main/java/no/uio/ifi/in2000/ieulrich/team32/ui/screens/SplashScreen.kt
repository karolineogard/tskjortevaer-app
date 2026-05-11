//package no.uio.ifi.in2000.ieulrich.team32.ui.screens
//
//import androidx.compose.animation.core.Animatable
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.size
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.alpha
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import coil3.compose.AsyncImage
//import coil3.request.ImageRequest
//import kotlinx.coroutines.delay
//
//@Composable
//fun SplashScreen(onSplashFinished: () -> Unit) {
//    val alpha = remember { Animatable(0f) }
//
//    LaunchedEffect(Unit) {
//        alpha.animateTo(
//            targetValue = 1f,
//            animationSpec = tween(durationMillis = 700)
//        )
//        delay(1200)
//        alpha.animateTo(
//            targetValue = 0f,
//            animationSpec = tween(durationMillis = 500)
//        )
//        onSplashFinished()
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xffDBF9FF)),
//        contentAlignment = Alignment.Center
//    ) {
//        AsyncImage(
//            model = ImageRequest.Builder(LocalContext.current)
//                .data("file:///android_asset/logo_frame4.svg")
//                .build(),
//            contentDescription = "T-skjortevær logo",
//            modifier = Modifier
//                .size(260.dp)
//                .alpha(alpha.value)
//        )
//    }
//}