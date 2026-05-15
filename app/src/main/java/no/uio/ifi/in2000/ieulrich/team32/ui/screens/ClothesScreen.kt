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
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.ieulrich.team32.R
import no.uio.ifi.in2000.ieulrich.team32.model.clothes.ActivityLevel
import no.uio.ifi.in2000.ieulrich.team32.ui.util.Format
import no.uio.ifi.in2000.ieulrich.team32.model.clothes.ClothesRecommendation
import no.uio.ifi.in2000.ieulrich.team32.ui.components.TravelTimesCard
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.DarkBlue
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.MediumBlue
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.BlueText
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.RedText
import no.uio.ifi.in2000.ieulrich.team32.viewmodel.ClothesViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClothesScreen(
    clothesViewModel: ClothesViewModel,
    expandSheet: Boolean = false
) {

    var showBanner by remember { mutableStateOf(false) }

    val today = remember {
        java.time.LocalDate.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("EEEE d. MMMM", Locale.forLanguageTag("no")))
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

    LaunchedEffect(Unit) { clothesViewModel.loadRecommendation() }

    LaunchedEffect(expandSheet) {
        if (expandSheet) {
            sheetState.bottomSheetState.expand()
        }
    }
    if (recommendation != null) {
        BottomSheetScaffold(
            scaffoldState = sheetState,
            sheetPeekHeight = 48.dp,
            containerColor = MaterialTheme.colorScheme.background,
            sheetDragHandle = null,
            sheetShadowElevation = 16.dp,
            sheetTonalElevation = 16.dp,
            sheetContainerColor = White,
            sheetContent = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            scope.launch {
                                if (sheetState.bottomSheetState.currentValue == SheetValue.Expanded)
                                    sheetState.bottomSheetState.partialExpand()
                                else
                                    sheetState.bottomSheetState.expand()
                            }
                        },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.clothes_adjust),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_up_icon),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp).rotate(rotation)
                    )
                }

                var localIsOutdoors by remember { mutableStateOf(if (settings.isOutdoors) true else null) }
                var localIsPhysical by remember { mutableStateOf(if (settings.isPhysicallyActive) true else null) }
                var localActivityLevel by remember { mutableStateOf(settings.activityLevel) }
                var localDepHour by remember(settings) { mutableIntStateOf(settings.departureHour) }
                var localDepMinute by remember(settings) { mutableIntStateOf(settings.departureMinute) }
                var localRetHour by remember(settings) { mutableIntStateOf(settings.returnHour) }
                var localRetMinute by remember(settings) { mutableIntStateOf(settings.returnMinute) }

                Column(
                    modifier = Modifier.padding(horizontal = 16.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TravelTimesCard(
                        title = stringResource(R.string.clothes_when),
                        departureHour = localDepHour,
                        departureMinute = localDepMinute,
                        returnHour = localRetHour,
                        returnMinute = localRetMinute,
                        onDepartureTimeChanged = { h, m -> localDepHour = h; localDepMinute = m },
                        onReturnTimeChanged = { h, m -> localRetHour = h; localRetMinute = m },
                        infoTitle = stringResource(R.string.clothes_traveltime_info_title),
                        infoText = stringResource(R.string.clothes_traveltime_info)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(stringResource(R.string.clothes_outdoors))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = localIsOutdoors == true,
                                    onCheckedChange = {
                                        localIsOutdoors = if (it) true else null
                                        if (!it) { localIsPhysical = null; localActivityLevel = null }
                                    }
                                )
                                Text(stringResource(R.string.check_yes))
                                Spacer(modifier = Modifier.width(16.dp))
                                Checkbox(
                                    checked = localIsOutdoors == false,
                                    onCheckedChange = {
                                        localIsOutdoors = if (it) false else null
                                        if (it) { localIsPhysical = null; localActivityLevel = null }
                                    }
                                )
                                Text(stringResource(R.string.check_no))
                            }
                        }
                    }

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
                                Text(stringResource(R.string.clothes_physical))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = localIsPhysical == true,
                                        onCheckedChange = {
                                            localIsPhysical = if (it) true else null
                                            if (!it) localActivityLevel = null
                                        }
                                    )
                                    Text(stringResource(R.string.check_yes))
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Checkbox(
                                        checked = localIsPhysical == false,
                                        onCheckedChange = {
                                            localIsPhysical = if (it) false else null
                                            if (it) localActivityLevel = null
                                        }
                                    )
                                    Text(stringResource(R.string.check_no))
                                }
                                AnimatedVisibility(visible = localIsPhysical == true) {
                                    Column {
                                        Text(stringResource(R.string.clothes_activity_level))
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
                        border = BorderStroke(2.dp, MediumBlue),
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
                            // warning på aldri lest, men denne blir brukt utenfor klassen
                            showBanner = true
                            scope.launch { sheetState.bottomSheetState.partialExpand() }
                        },
                        modifier = Modifier.wrapContentWidth(align = Alignment.CenterHorizontally).align(Alignment.CenterHorizontally),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )                ) {
                        Text(stringResource(R.string.clothes_new_recommendation))
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
                        modifier = Modifier.padding(12.dp).background(MaterialTheme.colorScheme.background),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        item {
                            Column(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                horizontalAlignment = Alignment.Start)
                            {
                                Text(
                                    text = stringResource(R.string.clothes_title),
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Spacer(modifier = Modifier.size(4.dp))
                                Text(
                                    text = today,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(top = 4.dp),
                                )
                                Spacer(modifier = Modifier.size(4.dp))
                                var infoString = if(settings.isOutdoors) {
                                    stringResource(
                                        R.string.clothes_recommendation_text_outdoors,
                                        settings.departureHour,
                                        settings.departureMinute,
                                        settings.returnHour,
                                        settings.returnMinute
                                    )
                                } else {
                                    stringResource(
                                        R.string.clothes_travel_times,
                                        settings.departureHour,
                                        settings.departureMinute,
                                        settings.returnHour,
                                        settings.returnMinute
                                    )
                                }

                                if(settings.isPhysicallyActive) {
                                    infoString += when (settings.activityLevel) {
                                        ActivityLevel.LOW -> stringResource(R.string.clothes_recommendation_text_physical_low)
                                        ActivityLevel.MEDIUM -> stringResource(R.string.clothes_recommendation_text_physical_medium)
                                        ActivityLevel.HIGH -> stringResource(R.string.clothes_recommendation_text_physical_high)
                                        else -> stringResource(R.string.clothes_recommendation_text_physical)
                                    }
                                }
                                Text(
                                    text = infoString,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(top = 4.dp),
                                )
                            }

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
                                    text = stringResource(R.string.clothes_no_data),
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
                            message = stringResource(R.string.clothes_recommendation_updated),
                            onDismiss = { showBanner = false }
                        )
                    }
                }
            }
        }
    } else {
        ErrorScreen()
    }
}

@Composable
private fun EffectiveTempCard(rec: ClothesRecommendation) {
    var showInfo by remember { mutableStateOf(false) }

    Box {
        Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                      stringResource(R.string.clothes_effective_temp),
                      style = MaterialTheme.typography.titleSmall, 
                      fontWeight = FontWeight.SemiBold
                    )
                    IconButton(onClick = { showInfo = true }, modifier = Modifier.size(24.dp)) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = stringResource(R.string.info_button),
                            modifier = Modifier.size(20.dp))
                    }
                }
                Text(
                    text = Format.formatTemp(rec.effectiveTemp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (rec.effectiveTemp <= 0.0) BlueText else RedText
                )
                Text(
                    text = stringResource(R.string.clothes_effective_temp_text),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        if (showInfo) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
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
                            text = stringResource(R.string.clothes_effective_temp_info_title_text),
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { showInfo = false },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = stringResource(R.string.close_button),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = stringResource(R.string.clothes_effective_temp_info_text),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun ClothingCard(rec: ClothesRecommendation) {
    var showInfo by remember { mutableStateOf(false) }

    Box {
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
                        text = stringResource(R.string.clothes_recommendation),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { showInfo = true }, modifier = Modifier.size(24.dp)) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = stringResource(R.string.info_button),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                if (rec.wearSunglasses) ClothingRow(
                    R.drawable.solbriller,
                    stringResource(R.string.clothes_sunglasses)
                )
                if (rec.wearHatGloves) ClothingRow(
                    R.drawable.caps,
                    stringResource(R.string.clothes_hat_gloves)
                )
                if (rec.wearScarf) ClothingRow(
                    R.drawable.skjerf,
                    stringResource(R.string.clothes_scarf)
                )
                if (rec.wearHeavyJacket) ClothingRow(
                    R.drawable.tykk_jakke,
                    stringResource(R.string.clothes_thick_jacket)
                )
                if (rec.wearLightJacket) ClothingRow(
                    R.drawable.lett_jakke,
                    stringResource(R.string.clothes_thin_jacket)
                )
                if (rec.wearSweater) ClothingRow(
                    R.drawable.genser,
                    stringResource(R.string.clothes_sweatshirt)
                )
                if (rec.wearTshirt) ClothingRow(
                    R.drawable.t_skjorte,
                    stringResource(R.string.clothes_tshirt)
                )
                if (rec.wearShorts) ClothingRow(
                    R.drawable.shorts,
                    stringResource(R.string.clothes_shorts)
                )
                if (rec.wearPants) ClothingRow(
                    R.drawable.jeans,
                    stringResource(R.string.clothes_pants)
                )
                if (rec.wearThermalUnderwear) ClothingRow(
                    R.drawable.jeggings,
                    stringResource(R.string.clothes_thermal_underwear)
                )

                if (rec.wearWinterBoots) {
                    ClothingRow(R.drawable.st_vler, stringResource(R.string.clothes_winter_boots))
                } else if (rec.wearWaterproofShoes) {
                    ClothingRow(
                        R.drawable.st_vler,
                        stringResource(R.string.clothes_waterproof_shoes)
                    )
                } else {
                    ClothingRow(R.drawable.sneaker, stringResource(R.string.clothes_shoes))
                }
            }
        }

        if (showInfo) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
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
                            text = stringResource(R.string.clothes_clothing_info_title_text),
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { showInfo = false },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = stringResource(R.string.close_button),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = stringResource(R.string.clothes_clothing_info_text),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

    }
}
@Composable
fun RainCard(rec: ClothesRecommendation) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (rec.bringUmbrella) {
                ClothingRow(
                    R.drawable.paraply_ikon,
                    stringResource(R.string.clothes_bring_umbrella)
                )
            }
            if (rec.wearRainGear) {
                ClothingRow(
                    R.drawable.paraply_ikon,
                    stringResource(R.string.clothes_wear_rain_gear)
                )
            }
        }
    }
}

@Composable
fun ClothingRow(iconRes: Int, text: String) {
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
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}