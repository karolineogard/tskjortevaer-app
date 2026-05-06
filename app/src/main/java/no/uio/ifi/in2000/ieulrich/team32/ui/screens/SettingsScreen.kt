package no.uio.ifi.in2000.ieulrich.team32.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import no.uio.ifi.in2000.ieulrich.team32.R
import no.uio.ifi.in2000.ieulrich.team32.ui.components.TimeInputField
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.DarkBlue
import no.uio.ifi.in2000.ieulrich.team32.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    settingsViewModel: SettingsViewModel
) {
    val savedDepHour   by settingsViewModel.defaultDepartureHour.collectAsState()
    val savedDepMinute by settingsViewModel.defaultDepartureMinute.collectAsState()
    val savedRetHour   by settingsViewModel.defaultReturnHour.collectAsState()
    val savedRetMinute by settingsViewModel.defaultReturnMinute.collectAsState()
    val savedOffset    by settingsViewModel.temperatureOffset.collectAsState()

    var startHour   by remember(savedDepHour)   { mutableIntStateOf(savedDepHour) }
    var startMinute by remember(savedDepMinute) { mutableIntStateOf(savedDepMinute) }
    var endHour     by remember(savedRetHour)   { mutableIntStateOf(savedRetHour) }
    var endMinute   by remember(savedRetMinute) { mutableIntStateOf(savedRetMinute) }

    // Slider internt 0..100, der 50 = nøytral (0°), 0 = ispinne (-5°), 100 = viking (+5°)
    var sliderValue by remember(savedOffset) {
        mutableFloatStateOf(savedOffset / 5f * 50f + 50f)
    }

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
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Tilbake"
                        )
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

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                val degrees = ((sliderValue - 50f) / 50f * 5).roundToInt()
                val sign = if (degrees >= 0) "+" else ""

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Hvor varm er du?", fontWeight = FontWeight.Bold)
                    Text("${sign}${degrees}° C", fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(id = R.drawable.ispinne_ikon),
                            contentDescription = "Ispinne – fryser lett"
                        )
                        Text("Ispinne", fontSize = 12.sp)
                    }
                    AdjustmentSlider(
                        modifier = Modifier.weight(1f),
                        value = sliderValue,
                        onValueChange = { sliderValue = it }
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(id = R.drawable.viking_ikon),
                            contentDescription = "Viking – varm av seg"
                        )
                        Text("Viking", fontSize = 12.sp)
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Reiser du fast til andre tidspunkter?",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Dra", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    TimeInputField(
                        initialHour = startHour,
                        initialMinute = startMinute,
                        onTimeChanged = { h, m ->
                            startHour = h
                            startMinute = m
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    Text("Tilbake", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    TimeInputField(
                        initialHour = endHour,
                        initialMinute = endMinute,
                        onTimeChanged = { h, m ->
                            endHour = h
                            endMinute = m
                        }
                    )
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                OutlinedButton(
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
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
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