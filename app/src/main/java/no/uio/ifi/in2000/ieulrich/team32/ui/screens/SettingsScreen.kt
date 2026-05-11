package no.uio.ifi.in2000.ieulrich.team32.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.ieulrich.team32.R
import no.uio.ifi.in2000.ieulrich.team32.ui.components.TimeInputField
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.DarkBlue
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.MediumBlue
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.MinusTekst
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.PlussTekst
import no.uio.ifi.in2000.ieulrich.team32.viewmodel.SettingsViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    settingsViewModel: SettingsViewModel
) {
    val savedDepHour   by settingsViewModel.defaultDepartureHour.collectAsStateWithLifecycle()
    val savedDepMinute by settingsViewModel.defaultDepartureMinute.collectAsStateWithLifecycle()
    val savedRetHour   by settingsViewModel.defaultReturnHour.collectAsStateWithLifecycle()
    val savedRetMinute by settingsViewModel.defaultReturnMinute.collectAsStateWithLifecycle()
    val savedOffset    by settingsViewModel.temperatureOffset.collectAsStateWithLifecycle()

    var startHour   by remember(savedDepHour)   { mutableIntStateOf(savedDepHour) }
    var startMinute by remember(savedDepMinute) { mutableIntStateOf(savedDepMinute) }
    var endHour     by remember(savedRetHour)   { mutableIntStateOf(savedRetHour) }
    var endMinute   by remember(savedRetMinute) { mutableIntStateOf(savedRetMinute) }

    var sliderValue by remember(savedOffset) {
        mutableFloatStateOf(savedOffset / 5f * 50f + 50f)
    }

    var showSliderInfo by remember { mutableStateOf(false) }
    var showTimeInfo by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = DarkBlue,
                    contentColor = MaterialTheme.colorScheme.surfaceVariant,
                    actionColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Innstillinger", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Tilbake")
                    }
                },
                expandedHeight = 32.dp,
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = 16.dp,
                    end = 16.dp,
                    bottom = innerPadding.calculateBottomPadding()
                )
        ) {
            Text("Preferanser", fontWeight = FontWeight.Bold)

            // Kort 1 — Temperaturjustering
            Box {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    val degrees = ((sliderValue - 50f) / 50f * 5).roundToInt()
                    val sign = if (degrees >= 0) "+" else ""
                    val tempColor = if (degrees <= 0) MinusTekst else PlussTekst

                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                        // Øverste rad: tittel + infoknapp
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Hvor varm er du?", fontWeight = FontWeight.Bold)
                            IconButton(onClick = { showSliderInfo = true }) {
                                Icon(Icons.Default.Info, contentDescription = "Info", modifier = Modifier.size(20.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Gradstal på egen linje
                        Text(
                            text = "${sign}${degrees}° C",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = tempColor
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(painter = painterResource(id = R.drawable.ispinne_ikon), contentDescription = "Ispinne – fryser lett")
                            Text("Ispinne", style = MaterialTheme.typography.bodySmall)
                        }
                        AdjustmentSlider(modifier = Modifier.weight(1f), value = sliderValue, onValueChange = { sliderValue = it })
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(painter = painterResource(id = R.drawable.viking_ikon), contentDescription = "Viking – varm av seg")
                            Text("Viking", style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (showSliderInfo) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                            Text("Info kommer", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                            IconButton(onClick = { showSliderInfo = false }, modifier = Modifier.size(20.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Lukk", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // Kort 2 — Reisetider
            Box {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Reiser du fast til andre tidspunkter?",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { showTimeInfo = true }) {
                                Icon(Icons.Default.Info, contentDescription = "Info", modifier = Modifier.size(20.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Dra", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        TimeInputField(initialHour = startHour, initialMinute = startMinute, onTimeChanged = { h, m -> startHour = h; startMinute = m })
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        Text("Tilbake", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        TimeInputField(initialHour = endHour, initialMinute = endMinute, onTimeChanged = { h, m -> endHour = h; endMinute = m })
                    }
                }

                if (showTimeInfo) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                            Text("Info kommer", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                            IconButton(onClick = { showTimeInfo = false }, modifier = Modifier.size(20.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Lukk", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                OutlinedButton(
                    border = BorderStroke(2.dp, MediumBlue),
                    onClick = {
                        val offsetDegrees = (sliderValue - 50f) / 50f * 5f
                        settingsViewModel.saveSettings(
                            departureHour     = startHour,
                            departureMinute   = startMinute,
                            returnHour        = endHour,
                            returnMinute      = endMinute,
                            temperatureOffset = offsetDegrees
                        )
                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "Innstillinger lagret!",
                                actionLabel = "✕",
                                duration = SnackbarDuration.Short
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                snackbarHostState.currentSnackbarData?.dismiss()
                            }
                        }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text("Lagre")
                }
            }
        }
    }
}

@Composable
fun AdjustmentSlider(
    modifier: Modifier = Modifier,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = modifier) {
        Slider(
            value = value,
            onValueChange = onValueChange,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            steps = 9,
            valueRange = 0f..100f
        )
    }
}