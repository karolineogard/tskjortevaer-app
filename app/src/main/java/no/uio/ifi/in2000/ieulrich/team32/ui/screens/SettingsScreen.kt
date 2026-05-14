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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.ieulrich.team32.R
import no.uio.ifi.in2000.ieulrich.team32.ui.components.TopAppBar
import no.uio.ifi.in2000.ieulrich.team32.ui.components.TravelTimesCard
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.DarkBlue
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.MediumBlue
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.MinusTekst
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.PlussTekst
import no.uio.ifi.in2000.ieulrich.team32.viewmodel.SettingsViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
) {
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()

    var startHour   by remember(uiState.departureHour)   { mutableIntStateOf(uiState.departureHour) }
    var startMinute by remember(uiState.departureMinute) { mutableIntStateOf(uiState.departureMinute) }
    var endHour     by remember(uiState.returnHour)   { mutableIntStateOf(uiState.returnHour) }
    var endMinute   by remember(uiState.returnMinute) { mutableIntStateOf(uiState.returnMinute) }

    var sliderValue by remember(uiState.temperatureOffset) {
        mutableFloatStateOf(uiState.temperatureOffset / 5f * 50f + 50f)
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
            TopAppBar(
                title = stringResource(R.string.settings_title),
                onBack = { navController.popBackStack() }
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
            Text(
                stringResource(R.string.settings_preferences),
                fontWeight = FontWeight.Bold
            )

            // Kort 1 — Temperaturjustering
            Box {
                Card(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(vertical = 8.dp),
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
                            Text(
                              stringResource(R.string.settings_heat_level),
                              fontWeight = FontWeight.Bold)
                            IconButton(onClick = { showSliderInfo = true }) {
                                Icon(Icons.Default.Info, contentDescription = "Info", modifier = Modifier.size(20.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Gradstal på egen linje
                        Text(
                            text = stringResource(
                                R.string.settings_temperature_value,
                                sign,
                                degrees
                            ),
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
                            Icon(
                              painter = painterResource(id = R.drawable.ispinne_ikon), 
                              contentDescription = stringResource(R.string.settings_cold_description)
                            )
                            Text(
                              stringResource(R.string.settings_cold),
                              style = MaterialTheme.typography.bodySmall
                            )
                        }
                        AdjustmentSlider(
                          modifier = Modifier.weight(1f), 
                          value = sliderValue, 
                          onValueChange = { sliderValue = it })
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(id = R.drawable.viking_ikon),
                                contentDescription = stringResource(R.string.settings_warm_description)
                            )
                            Text(
                              stringResource(R.string.settings_warm), 
                              style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (showSliderInfo) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.settings_slider_info_title),
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { showSliderInfo = false },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Lukk",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = stringResource(R.string.settings_slider_info),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            // Kort 2 — Reisetider
            TravelTimesCard(
                title = stringResource(R.string.settings_traveltimes),
                departureHour = startHour,
                departureMinute = startMinute,
                returnHour = endHour,
                returnMinute = endMinute,
                onDepartureTimeChanged = { hour, minute -> startHour = hour; startMinute = minute },
                onReturnTimeChanged = { hour, minute -> endHour = hour; endMinute = minute }
            )
            val settingsSaved = stringResource(R.string.settings_saved)
            val dismiss = stringResource(R.string.dismiss)
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
                            snackbarHostState.showSnackbar(
                                message = settingsSaved,
                                actionLabel = dismiss,
                                duration = SnackbarDuration.Short
                            )
                        }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(stringResource(R.string.settings_save))
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