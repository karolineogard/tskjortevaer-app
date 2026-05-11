package no.uio.ifi.in2000.ieulrich.team32.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import no.uio.ifi.in2000.ieulrich.team32.model.metAlerts.MetAlert
import no.uio.ifi.in2000.ieulrich.team32.model.metAlerts.iconUrl
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.FarevarselGul
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.FarevarselGulGjennomsiktig
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.FarevarselOransje
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.FarevarselOranjeGjennomsiktig
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.FarevarselRød
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.FarevarselRødGjennomsiktig
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.RobotoMono

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDetailScreen(
    alert: MetAlert,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(FormatEventName(alert.event)) },
                navigationIcon = {
                    IconButton(onClick = onBack){
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Tilbake"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Log.d("MetAlert", "severity: ${alert.severity}")

        Column(
            modifier = Modifier
                .padding(top = innerPadding.calculateTopPadding())
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SeverityColor(alert.severity).copy(alpha = 0.3f)
                ),
                border = BorderStroke(2.dp, SeverityColor(alert.severity))
            ){
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = alert.iconUrl,
                        contentDescription = "Ikon for ${alert.event}",
                        modifier = Modifier
                            .size(120.dp)
                            .padding(vertical = 8.dp),
                        contentScale = ContentScale.Fit,
                        onState = { state ->
                            when (state) {
                                is coil3.compose.AsyncImagePainter.State.Error -> {
                                    Log.e(
                                        "MetAlertIcon",
                                        "Feil ved lasting av ikon: ${state.result.throwable.message}"
                                    )
                                    Log.e("MetAlertIcon", "Prøvde å hente: ${alert.iconUrl}")
                                }

                                is coil3.compose.AsyncImagePainter.State.Success -> {
                                    Log.d("MetAlertIcon", "Vellykket lasting av: ${alert.iconUrl}")
                                }

                                else -> {}
                            }
                        }

                    )

                    alert.title?.let {
                        InfoSection(
                            label = "Varsel",
                            value = "${FormatEventName(alert.event)} – ${alert.area ?: ""}"
                        )
                    }
                    alert.severity?.let {
                        InfoSection(label = "Alvorlighetsgrad", value = FormatSeverity(it))
                    }
                    alert.area?.let {
                        InfoSection(label = "Område", value = it)
                    }
                    alert.description?.let {
                        InfoSection(label = "Beskrivelse", value = it)
                    }
                    alert.consequence?.let {
                        InfoSection(label = "Konsekvens", value = it)
                    }
                    alert.instruction?.let {
                        InfoSection(label = "Instruksjon", value = it)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(2.dp))
                    SeverityLegend()
                    Spacer(modifier = Modifier.height(4.dp))

                }

            }

        }
    }
}

@Composable
fun InfoSection(label: String, value: String, color: Color = MaterialTheme.colorScheme.onPrimary) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = color,
            fontFamily = RobotoMono,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            fontFamily = RobotoMono,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}


fun SeverityColor(severity: String?): Color = when (severity?.lowercase()){
    "moderate" -> FarevarselGul
    "severe"   -> FarevarselOransje
    "extreme"  -> FarevarselRød
    else -> Color.LightGray
}

fun FormatEventName(event: String?): String = when (event?.lowercase()) {
    "blowingsnow" -> "Snøfokk"
    "forestfire"  -> "Skogbrannfare"
    "gale"        -> "Kuling"
    "ice"         -> "Is"
    "icing"       -> "Isingsfare"
    "lightning"   -> "Lyn"
    "polarlow"    -> "Polart lavtrykk"
    "rain"        -> "Regn"
    "rainflood"   -> "Flom"
    "snow"        -> "Snø"
    "stormsurge"  -> "Stormflo"
    "wind"        -> "Vind"
    else          -> event ?: "Ukjent varsel"
}

fun FormatSeverity(severity: String?): String = when (severity?.lowercase()) {
    "moderate" -> "Gul"
    "severe"   -> "Oransje"
    "extreme"  -> "Rød"
    else       -> severity ?: "Ukjent"
}

@Composable
fun SeverityLegend(modifier: Modifier = Modifier) {
    val levels = listOf(
        Triple("minor",    FarevarselGul, "Gult: Moderat fare"),
        Triple("moderate", FarevarselOransje, "Oransje: Stor fare"),
        Triple("severe",   FarevarselRød, "Rødt: Ekstrem fare og ekstremvær"),
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "Farenivåer",
            fontFamily = RobotoMono,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
        )
        levels.forEach { (_, color, label) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(color)
                        .border(1.dp, Color.Black.copy(alpha = 0.3f), RoundedCornerShape(3.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    fontFamily = RobotoMono,
                    //style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}