package no.uio.ifi.in2000.ieulrich.team32.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.ieulrich.team32.R
import no.uio.ifi.in2000.ieulrich.team32.model.clothes.ClothesRecommendation
import no.uio.ifi.in2000.ieulrich.team32.viewmodel.ClothesViewModel
import androidx.compose.runtime.mutableIntStateOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClothesScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    clothesViewModel: ClothesViewModel
) {
    val recommendation by clothesViewModel.recommendation.collectAsState()
    val settings by clothesViewModel.settings.collectAsState()
    val isLoading by clothesViewModel.isLoading.collectAsState()

    val sheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.PartiallyExpanded,
            skipHiddenState = true
        )
    )
    val scope = rememberCoroutineScope()

    BackHandler(enabled = sheetState.bottomSheetState.currentValue == SheetValue.Expanded) {
        scope.launch { sheetState.bottomSheetState.partialExpand() }
    }

    LaunchedEffect(Unit) {
        clothesViewModel.loadRecommendation()
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
                modifier = Modifier.fillMaxWidth(),
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
                    modifier = Modifier.size(20.dp)
                )
            }

            var localIsOutdoors by remember { mutableStateOf(settings.isOutdoors) }
            var localIsPhysical by remember { mutableStateOf(settings.isPhysicallyActive) }
            var localActivityLevel by remember { mutableStateOf(settings.activityLevel) }

            var localDepHour by remember { mutableIntStateOf(settings.departureHour) }
            var localDepMinute by remember { mutableIntStateOf(0) }
            var localRetHour by remember { mutableIntStateOf(settings.returnHour) }
            var localRetMinute by remember { mutableIntStateOf(0) }

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
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

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        CheckboxSection(
                            isOutdoors = localIsOutdoors,
                            onOutdoorsChange = {
                                localIsOutdoors = it
                                if (!it) { localIsPhysical = false; localActivityLevel = null }
                            },
                            isPhysical = localIsPhysical,
                            onPhysicalChange = {
                                localIsPhysical = it
                                if (!it) localActivityLevel = null
                            },
                            activityLevel = localActivityLevel,
                            onActivityLevelChange = { localActivityLevel = it }
                        )
                    }
                }

                OutlinedButton(
                    onClick = {
                        clothesViewModel.updateSettings(
                            departureHour = localDepHour,
                            returnHour = localRetHour,
                            isOutdoors = localIsOutdoors,
                            isPhysicallyActive = localIsPhysical,
                            activityLevel = localActivityLevel
                        )
                        scope.launch { sheetState.bottomSheetState.partialExpand() }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text("Lagre og oppdater")
                }

                Spacer(modifier = Modifier.size(24.dp))
            }
        }
    ) { _ ->
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
                text = "%.1f°C".format(rec.effectiveTemp),
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

            // Hodeplagg øverst
            if (rec.wearSunglasses) {
                ClothingRow(R.drawable.solbriller, "Solbriller anbefales — det er sol.")
            }
            if (rec.wearHatGloves) {
                ClothingRow(R.drawable.caps, "Lue og hansker/votter anbefales.")
            }
            if (rec.wearScarf) {
                ClothingRow(R.drawable.skjerf, "Hals/buff/skjerf er lurt i denne kulden.")
            }

            // Overkropp
            if (rec.wearThermalUnderwear) {
                ClothingRow(R.drawable.tskjorte_ikon, "Ullundertøy — det er kaldt nok til å trenge ekstra lag.")
            }
            if (rec.wearHeavyJacket) {
                ClothingRow(R.drawable.tykk_jakke, "Tykk jakke — det er skikkelig kaldt ute.")
            }
            if (rec.wearLightJacket) {
                ClothingRow(R.drawable.lett_jakke, "Lett jakke eller mellomlag passer bra.")
            }
            if (rec.wearSweater) {
                ClothingRow(R.drawable.genser, "En genser holder deg komfortabel.")
            }
            if (rec.wearTshirt) {
                ClothingRow(R.drawable.t_skjorte, "T-skjorte holder — det er fint og varmt ute.")
            }

            // Underkropp
            if (rec.wearShorts) {
                ClothingRow(R.drawable.shorts, "Shorts — det er varmt nok til å droppe lange bukser.")
            }
            if (rec.wearPants) {
                ClothingRow(R.drawable.jeans, "Bukse passer til dagens temperatur.")
            }

            // Sko nederst
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