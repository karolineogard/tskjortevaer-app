package no.uio.ifi.in2000.ieulrich.team32.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.North
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun WindDirectionArrow(
    degrees: Float,
    modifier: Modifier = Modifier
){
        Icon(
            imageVector = Icons.Filled.North,
            contentDescription = "Vindretning",
            modifier = modifier
                .size(48.dp)
                .rotate(degrees)
        )
}

@Preview
@Composable
fun PreviewWindDirectionArrow(){
    WindDirectionArrow(degrees = 11f)
}