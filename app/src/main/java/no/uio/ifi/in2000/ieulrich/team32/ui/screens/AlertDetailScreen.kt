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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import no.uio.ifi.in2000.ieulrich.team32.model.metAlerts.MetAlert
import no.uio.ifi.in2000.ieulrich.team32.model.metAlerts.iconUrl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDetailScreen(
    alert: MetAlert,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(alert.event ?: "Farevarsel") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Tilbake")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Use the extension property from the model layer
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
                Text(
                    text = it,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
            }
            alert.severity?.let {
                InfoSection(label = "Alvorlighetsgrad", value = it)
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
        }
    }
}

@Composable
fun InfoSection(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}
