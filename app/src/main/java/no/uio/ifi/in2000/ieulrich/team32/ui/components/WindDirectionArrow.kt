package no.uio.ifi.in2000.ieulrich.team32.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun WindDirectionArrow(
    degrees: Float,
    modifier: Modifier = Modifier
){
    Canvas(
        modifier = modifier
            .size(48.dp)
            .rotate(degrees)
    ) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w / 2f, 0f)
            lineTo(w * 0.65f, h * 0.4f)
            lineTo(w * 0.55f, h * 0.4f)
            lineTo(w * 0.55f, h)
            lineTo(w * 0.45f, h)
            lineTo(w * 0.45f, h * 0.4f)
            lineTo(w * 0.35f, h * 0.4f)
            close()
        }
        drawPath(path = path, color = Color.Black)
    }
}

@Preview
@Composable
fun PreviewWindDirectionArrow(){
    WindDirectionArrow(degrees = 11f)
}