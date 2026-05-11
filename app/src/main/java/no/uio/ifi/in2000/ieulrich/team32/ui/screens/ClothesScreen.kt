package no.uio.ifi.in2000.ieulrich.team32.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.ieulrich.team32.R
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.Format
import no.uio.ifi.in2000.ieulrich.team32.model.clothes.ClothesRecommendation
import no.uio.ifi.in2000.ieulrich.team32.ui.components.ActivityLevel
import no.uio.ifi.in2000.ieulrich.team32.ui.components.TimeInputField
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.DarkBlue
import no.uio.ifi.in2000.ieulrich.team32.viewmodel.ClothesViewModel
import no.uio.ifi.in2000.ieulrich.team32.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClothesScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    clothesViewModel: ClothesViewModel,
    settingsViewModel: SettingsViewModel,
    isOnline: Boolean
) {
    val defaultDepHour by settingsViewModel.defaultDepartureHour.collectAsStateWithLifecycle()
    val defaultRetHour by settingsViewModel.defaultReturnHour.collectAsStateWithLifecycle()

    var showBanner by remember { mutableStateOf(false) }
    var bannerMessage by remember { mutableStateOf("") }

    val today = remember {
        java.time.LocalDate.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("EEEE d. MMMM", java.util.Locale("no")))
            .replaceFirstChar { it.uppercase() }
    }

    val recommendation by clothesViewModel.recommendation.collectAsStateWithLifecycle()
    val settings by clothesViewModel.settings.collectAsStateWithLifecycle()
    val isLoading by clothesViewModel.isLoading.collectAsStateWithLifecycle()

    val sheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded,
            skipHiddenState = false
        )
    )
    val scope = rememberCoroutineScope()

    val rotation by animateFloatAsState(
        targetValue = if (sheetState.bottomSheetState.currentValue == SheetValue.Expanded) 180f else 0f,
        animationSpec = tween(200)
    )

    BackHandler(enabled = sheetState.bottomSheetState.currentValue == SheetValue.Expanded) {
        scope.launch { sheetState.bottomSheetState.partialExpand() }
    }

    LaunchedEffect(Unit) {
        clothesViewModel.loadRecommendation()
    }
    LaunchedEffect(Unit) {
        clothesViewModel.clearRecommendationIfOffline(isOnline)
    }

    BottomSheetScaffold(
        scaffoldState = sheetState,
        sheetPeekHeight = 48.dp,
        containerColor = MaterialTheme.colorScheme.background,
        sheetDragHandle = null,
        sheetShadowElevation = 16.dp,
        sheetTonalElevation = 16.dp,
        sheetContainerColor = MaterialTheme.colorScheme.background,
        sheetContent = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        scope.launch {
                            if (sheetState.bottomSheetState.currentValue == SheetValue.Expanded) {
                                sheetState.bottomSheetState.partialExpand()
                            } else {
                                sheetState.bottomSheetState.expand()
                            }
                        }
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tilpass klesanbefaling",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
                Icon(
                    painter = painterResource(id = R.drawable.arrow_up_icon),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp).rotate(rotation)
                )
            }

            var localIsOutdoors by remember { mutableStateOf<Boolean?>(if (settings.isOutdoors) true else null) }
            var localIsPhysical by remember { mutableStateOf<Boolean?>(if (settings.isPhysicallyActive) true else null) }
            var localActivityLevel by remember { mutableStateOf(settings.activityLevel) }

            var localDepHour by remember(settings) { mutableIntStateOf(settings.departureHour) }
            var localDepMinute by remember(settings) { mutableIntStateOf(settings.departureMinute) }
            var localRetHour by remember(settings) { mutableIntStateOf(settings.returnHour) }
            var localRetMinute by remember(settings) { mutableIntStateOf(settings.returnMinute) }

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Reisekort
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Når reiser du?", fontSize = 32.sp, fontWeight = FontWeight.Bold)
                        Text("Dra", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                        TimeInputField(
                            initialHour = localDepHour,
                            initialMinute = localDepMinute,
                            onTimeChanged = { hour, minute ->
                                localDepHour = hour
                                localDepMinute = minute
                            }
                        )
                        HorizontalDivider()
                        Text("Tilbake", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                        TimeInputField(
                            initialHour = localRetHour,
                            initialMinute = localRetMinute,
                            onTimeChanged = { hour, minute ->
                                localRetHour = hour
                                localRetMinute = minute
                            }
                        )
                    }
                }

                // Kort 1 — Utendørs
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Skal du være utendørs")
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = localIsOutdoors == true,
                                onCheckedChange = {
                                    localIsOutdoors = if (it) true else null
                                    if (!it) { localIsPhysical = null; localActivityLevel = null }
                                }
                            )
                            Text("Ja")
                            Spacer(modifier = Modifier.width(16.dp))
                            Checkbox(
                                checked = localIsOutdoors == false,
                                onCheckedChange = {
                                    localIsOutdoors = if (it) false else null
                                    if (it) { localIsPhysical = null; localActivityLevel = null }
                                }
                            )
                            Text("Nei")
                        }
                    }
                }

                // Kort 2 — Fysisk aktiv, glir inn når ute = true
                AnimatedVisibility(
                    visible = localIsOutdoors == true,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Skal du være fysisk aktiv")
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = localIsPhysical == true,
                                    onCheckedChange = {
                                        localIsPhysical = if (it) true else null
                                        if (!it) localActivityLevel = null
                                    }
                                )
                                Text("Ja")
                                Spacer(modifier = Modifier.width(16.dp))
                                Checkbox(
                                    checked = localIsPhysical == false,
                                    onCheckedChange = {
                                        localIsPhysical = if (it) false else null
                                        if (it) localActivityLevel = null
                                    }
                                )
                                Text("Nei")
                            }

                            // Radioknapper, glir inn når fysisk = true
                            AnimatedVisibility(visible = localIsPhysical == true) {
                                Column {
                                    Text("Nivå:")
                                    ActivityLevel.entries.forEach { level ->
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            RadioButton(
                                                selected = localActivityLevel == level,
                                                onClick = { localActivityLevel = level }
                                            )
                                            Text(level.displayValue)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                OutlinedButton(
                    onClick = {
                        clothesViewModel.updateSettings(
                            departureHour = localDepHour,
                            departureMinute = localDepMinute,
                            returnHour = localRetHour,
                            returnMinute = localRetMinute,
                            isOutdoors = localIsOutdoors == true,
                            isPhysicallyActive = localIsPhysical == true,
                            activityLevel = localActivityLevel
                        )
                        bannerMessage = "Klesanbefalingen er oppdatert."
                        showBanner = true
                        scope.launch { sheetState.bottomSheetState.partialExpand() }
                    },
                    modifier = Modifier.wrapContentWidth(
                        align = Alignment.CenterHorizontally
                    ).align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text("Gi ny klesanbefaling")
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_forward_icon),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.size(24.dp))
            }
        }
    ) { _ ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(12.dp)
                        .background(MaterialTheme.colorScheme.background),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    item {
                        Text(text = "Klær", fontSize = 48.sp)
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = "Anbefalingen tar utgangspunkt i fremkomst til og fra skole eller jobb.\n" +
                                    "Swipe opp for å tilpasse klesanbefalingen!"
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = today,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(
                            text = "Reise: %02d:%02d → %02d:%02d".format(
                                settings.departureHour, settings.departureMinute,
                                settings.returnHour, settings.returnMinute
                            ),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    if (recommendation != null) {
                        item { EffectiveTempCard(recommendation!!) }
                        item { ClothingCard(recommendation!!) }
                        if (recommendation!!.bringUmbrella || recommendation!!.wearRainGear) {
                            item { RainCard(recommendation!!) }
                        }
                    } else {
                        item {
                            Text(
                                text = "Ingen værdata tilgjengelig. Prøv å søk opp et sted på hjemskjermen først.",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = showBanner,
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                    modifier = Modifier.align(Alignment.TopCenter).zIndex(1f)
                ) {
                    TopBanner(
                        message = bannerMessage,
                        onDismiss = { showBanner = false }
                    )
                }
            }
        }
    }
}

@Composable
private fun EffectiveTempCard(rec: ClothesRecommendation) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Effektiv temperatur", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(
                text = Format.formatTemp(rec.effectiveTemp),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Justert for vind, skydekke og aktivitet",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun ClothingCard(rec: ClothesRecommendation) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.kl_r_ikon),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Klesanbefaling",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            if (rec.wearSunglasses) ClothingRow(R.drawable.solbriller, "Solbriller anbefales — det er sol.")
            if (rec.wearHatGloves) ClothingRow(R.drawable.caps, "Lue og hansker/votter anbefales.")
            if (rec.wearScarf) ClothingRow(R.drawable.skjerf, "Hals/buff/skjerf er lurt i denne kulden.")
            if (rec.wearHeavyJacket) ClothingRow(R.drawable.tykk_jakke, "Tykk jakke — det er skikkelig kaldt ute.")
            if (rec.wearLightJacket) ClothingRow(R.drawable.lett_jakke, "Lett jakke eller mellomlag passer bra.")
            if (rec.wearSweater) ClothingRow(R.drawable.genser, "En genser holder deg komfortabel.")
            if (rec.wearTshirt) ClothingRow(R.drawable.t_skjorte, "T-skjorte holder — det er fint og varmt ute.")
            if (rec.wearShorts) ClothingRow(R.drawable.shorts, "Shorts — det er varmt nok til å droppe lange bukser.")
            if (rec.wearPants) ClothingRow(R.drawable.jeans, "Bukse passer til dagens temperatur.")
            if (rec.wearThermalUnderwear) ClothingRow(R.drawable.jeggings, "Ullundertøy — det er kaldt nok til å trenge ekstra lag.")

            if (rec.wearWinterBoots) {
                ClothingRow(R.drawable.st_vler, "Vintersko/støvler — det er frost og nedbør.")
            } else if (rec.wearWaterproofShoes) {
                ClothingRow(R.drawable.st_vler, "Vanntette sko anbefales — det er ventet nedbør.")
            } else {
                ClothingRow(R.drawable.sneaker, "Hverdagssko passer fint i dag.")
            }
        }
    }
}

@Composable
private fun RainCard(rec: ClothesRecommendation) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (rec.bringUmbrella) {
                ClothingRow(R.drawable.paraply_ikon, "Ta med paraply — det er ventet nedbør og lite vind.")
            }
            if (rec.wearRainGear) {
                ClothingRow(R.drawable.paraply_ikon, "Ta på regntøy — det er nedbør kombinert med sterk vind.")
            }
        }
    }
}

@Composable
private fun ClothingRow(iconRes: Int, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, modifier = Modifier.weight(1f))
    }
}

@Composable
fun TopBanner(message: String, onDismiss: () -> Unit) {
    LaunchedEffect(message) {
        kotlinx.coroutines.delay(3500)
        onDismiss()
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(DarkBlue)
            .clickable { onDismiss() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.kl_r_ikon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.surfaceVariant,
                fontSize = 13.sp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}