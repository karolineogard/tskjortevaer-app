package no.uio.ifi.in2000.ieulrich.team32.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import no.uio.ifi.in2000.ieulrich.team32.model.metAlerts.MetAlert
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDetailScreen(
    alert : MetAlert,
    onBack : () -> Unit
){
    Scaffold(
        topBar = {
        TopAppBar(
            title = {Text(alert.event?: "Farevarsel")},
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Tilbake")
                }
            }
        )
        }
    ){
        padding ->
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                alert.title?.let{
                    Text(text = it, style = MaterialTheme.typography.headlineSmall)
                }

                alert.severity?.let{
                    InfoSelection(lable = "Alvorlighetsgrad", value = it)
                }

                alert.area?.let{
                    InfoSelection(lable = "Område", value = it)
                }

                alert.description?.let{
                    InfoSelection(lable = "Beskrivelse", value = it)
                }

                alert.consequence?.let{
                    InfoSelection(lable = "Konsekvens", value = it)
                }

                alert.instruction?.let{
                    InfoSelection(lable = "Instruksjon", value = it)
                }
            }
    }
}

@Composable
fun InfoSection(label: String, value: String){
    Column{
        Text(text = label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}