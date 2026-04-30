package no.uio.ifi.in2000.ieulrich.team32.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.svg.SvgDecoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import no.uio.ifi.in2000.ieulrich.team32.R
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.Format
import no.uio.ifi.in2000.ieulrich.team32.model.locationForecast.ForecastHourDetails
import no.uio.ifi.in2000.ieulrich.team32.model.locationForecast.imageUrl
import no.uio.ifi.in2000.ieulrich.team32.ui.Routes
import no.uio.ifi.in2000.ieulrich.team32.model.clothes.ClothesRecommendation
import no.uio.ifi.in2000.ieulrich.team32.viewmodel.ClothesViewModel
import no.uio.ifi.in2000.ieulrich.team32.viewmodel.LocationForecastViewmodel
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.text.Normalizer
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent


data class SimpleLatLng(val lat: Double, val lon: Double)

data class Farevarsel(
    val tittel: String,
    val beskrivelse: String,
    val alvorlighet: String
)

suspend fun getCoordsFromService(sted: String): SimpleLatLng? {
    if (sted.isBlank()) return null
    return withContext(Dispatchers.IO) {
        try {
            val url = URL("https://nominatim.openstreetmap.org/search?q=${sted}&format=json&limit=1")
            val connection = url.openConnection() as HttpURLConnection
            connection.setRequestProperty("User-Agent", "IN2000-Team32-WeatherApp")
            val response = connection.inputStream.bufferedReader().readText()
            val jsonArray = JSONArray(response)
            if (jsonArray.length() > 0) {
                val firstResult = jsonArray.getJSONObject(0)
                SimpleLatLng(firstResult.getDouble("lat"), firstResult.getDouble("lon"))
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewmodel: LocationForecastViewmodel,
    clothesViewModel: ClothesViewModel
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewmodel.loadForecastForDevice(context)
    }
    val currentLocation by viewmodel.currentLocation.collectAsState()
    LaunchedEffect(currentLocation) {
        currentLocation?.let { (lat, lon) ->
            clothesViewModel.updateLocation(lat, lon)
        }
    }
    val padding = 16.dp
    val forecastNow by viewmodel.forecastNow.collectAsState()
    var isVisible by remember { mutableStateOf(true) }


    val focusManager = LocalFocusManager.current

    var isSearchExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            },


        verticalArrangement = Arrangement.spacedBy(padding),
        contentPadding = PaddingValues(
            start = padding,
            end = padding,
            top = padding,
            bottom = padding
        ),
    ) {
        item {
            HomeSearchBar(
                navController = navController,
                viewmodel = viewmodel
            )
        }

        item {
            WeatherCard(
                navController = navController,
                forecastHourDetails = forecastNow,
                location = currentLocation
            )
        }
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                MetalertCarousel(modifier = Modifier.height(80.dp))
            }
        }
        item {
            ClothingCard(navController = navController, clothesViewModel = clothesViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun HomeSearchBar(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewmodel: LocationForecastViewmodel
) {
    val scope = rememberCoroutineScope()
    var søkeTekst by remember { mutableStateOf("") }
    var visForslag by remember { mutableStateOf(false) }
    var forslag by remember { mutableStateOf<List<Pair<String, SimpleLatLng>>>(emptyList()) }
    var sisteSearcher by rememberSaveable { mutableStateOf<List<String>>(emptyList()) }
    val søkeFlow = remember { MutableStateFlow("") }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var harFokus by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        søkeFlow
            .debounce(300)
            .distinctUntilChanged()
            .collect { tekst ->
                if (tekst.length >= 2) {
                    withContext(Dispatchers.IO) {
                        try {
                            val url = URL("https://nominatim.openstreetmap.org/search?q=${tekst}&format=json&limit=5&featuretype=city")
                            val connection = url.openConnection() as HttpURLConnection
                            connection.setRequestProperty("User-Agent", "IN2000-Team32-WeatherApp")
                            val response = connection.inputStream.bufferedReader().readText()
                            val jsonArray = JSONArray(response)
                            val results = mutableListOf<Pair<String, SimpleLatLng>>()
                            for (i in 0 until jsonArray.length()) {
                                val obj = jsonArray.getJSONObject(i)
                                val name = obj.optString("display_name").split(",").take(2).joinToString(", ")
                                results.add(Pair(name, SimpleLatLng(obj.getDouble("lat"), obj.getDouble("lon"))))
                            }
                            withContext(Dispatchers.Main) {
                                forslag = results
                                visForslag = results.isNotEmpty() && harFokus
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    forslag = emptyList()
                    visForslag = false
                }
            }
    }

    fun navigerTilBy(navn: String, coords: SimpleLatLng) {
        navController.navigate("forecast?lat=${coords.lat}&lon=${coords.lon}&city=${navn}")
        sisteSearcher = (listOf(navn) + sisteSearcher).distinct().take(5)
        søkeTekst = ""
        forslag = emptyList()
        visForslag = false
        focusManager.clearFocus()
        harFokus = false
    }

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = søkeTekst,
            onValueChange = { nyTekst ->
                søkeTekst = nyTekst
                scope.launch { søkeFlow.emit(nyTekst) }
                if (nyTekst.isEmpty()) visForslag = false
            },
            placeholder = { Text("Søk etter by...") },
            singleLine = true,
            shape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    harFokus = focusState.isFocused
                    if (!focusState.isFocused) {
                        // Når man mister fokus, skjul forslag
                        visForslag = false
                    } else {
                        // Når man får fokus og har tekst, vis forslag igjen
                        if (søkeTekst.length >= 2) {
                            visForslag = forslag.isNotEmpty()
                        }
                    }
                },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            },
            trailingIcon = {
                if (søkeTekst.isNotEmpty()) {
                    IconButton(onClick = {
                        søkeTekst = ""
                        forslag = emptyList()
                        visForslag = false
                        scope.launch { søkeFlow.emit("") }
                        focusManager.clearFocus()
                        harFokus = false
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Tøm")
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    val første = forslag.firstOrNull()
                    if (første != null) {
                        navigerTilBy(første.first, første.second)
                    } else {
                        scope.launch {
                            val coords = getCoordsFromService(søkeTekst)
                            if (coords != null) navigerTilBy(søkeTekst, coords)
                        }
                    }
                }
            )
        )

        if (visForslag && forslag.isNotEmpty() && harFokus) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column {
                    forslag.forEach { (navn, coords) ->
                        ListItem(
                            headlineContent = { Text(navn) },
                            modifier = Modifier
                                .clickable { navigerTilBy(navn, coords) }
                                .fillMaxWidth()
                        )
                        HorizontalDivider()
                    }
                }
            }
        }

        if (søkeTekst.isEmpty() && sisteSearcher.isNotEmpty() && harFokus) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column {
                    Text(
                        text = "Sist søkt",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    sisteSearcher.forEach { navn ->
                        ListItem(
                            headlineContent = { Text(navn) },
                            leadingContent = {
                                Icon(
                                    painter = painterResource(id = android.R.drawable.ic_menu_recent_history),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            modifier = Modifier.clickable {
                                scope.launch {
                                    val coords = getCoordsFromService(navn)
                                    if (coords != null) navigerTilBy(navn, coords)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetalertCarousel(modifier: Modifier = Modifier) {
    val varselTekster = listOf("Sterk vind", "Flom", "Orkan")
    Column(modifier = modifier) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(varselTekster.size) { index ->
                Box(modifier = Modifier.width(280.dp)) {
                    MetalertCard(tekst = varselTekster[index])
                }
            }
        }
    }
}

@Composable
fun WeatherCard(
    navController: NavController,
    forecastHourDetails: ForecastHourDetails?,
    modifier: Modifier = Modifier,
    location: Pair<Double, Double>?,

) {
    val svgLoader = rememberSvgImageLoader()
    Card(
        modifier = modifier.height(280.dp).fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        onClick = {  // ← gjør kortet klikkbart
            location?.let { (lat, lon) ->
                navController.navigate("forecast?lat=$lat&lon=$lon&city=Min posisjon")
            }
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val now = LocalDateTime.now()
            val today = LocalDate.now()
            val datePart = if (now.toLocalDate().isEqual(today)) "I dag"
            else now.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            val timePart = if (forecastHourDetails == null) "00:00"
                else Format.extractTime(forecastHourDetails.timestamp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.klokke_ikon),
                    contentDescription = null,
                    modifier = Modifier.weight(0.4f)
                )
                Text(text = "$datePart $timePart", modifier = Modifier.weight(3f))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Oslo", fontSize = 40.sp)
            }
            forecastHourDetails?.let { details ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = details.imageUrl,
                        imageLoader = svgLoader,
                        contentDescription = "Ikon for ${details.symbolCode}",
                        modifier = Modifier.size(120.dp).padding(vertical = 8.dp),
                        contentScale = ContentScale.Fit,
                        onState = { state ->
                            when (state) {
                                is coil3.compose.AsyncImagePainter.State.Error -> Log.e(
                                    "WeatherCard",
                                    "Feil: ${state.result.throwable.message}"
                                )

                                is coil3.compose.AsyncImagePainter.State.Success -> Log.d(
                                    "WeatherCard",
                                    "Loaded: ${details.imageUrl}"
                                )

                                else -> {}
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(text = details.temperature, fontSize = 40.sp)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) { Text(text = "H:14°  L: 5°") }
            }
        }
    }
}

@Composable
fun MetalertCard(tekst: String) {
    Card(
        modifier = Modifier.fillMaxWidth().height(80.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
            Text(text = tekst, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ClothingCard(
    navController: NavController,
    clothesViewModel: ClothesViewModel,
    modifier: Modifier = Modifier
) {
    var showInfo by rememberSaveable { mutableStateOf(false) }
    val recommendation by clothesViewModel.recommendation.collectAsState()
    val isLoading by clothesViewModel.isLoading.collectAsState()

    Box(modifier = modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            onClick = { navController.navigate(Routes.CLOTHES) }
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Tittelrad
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.size(48.dp))
                    Text(
                        text = "Bekledning",
                        fontSize = 30.sp,
                        modifier = Modifier.weight(2f),
                        textAlign = TextAlign.Center
                    )
                    IconButton(onClick = { showInfo = !showInfo }) {
                        Icon(Icons.Default.Info, contentDescription = "Info")
                    }
                }

                HorizontalDivider()

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                    }
                } else if (recommendation != null) {
                    ClothingCardRecommendationRows(rec = recommendation!!)
                } else {
                    Text(
                        text = "Ingen værdata tilgjengelig ennå.",
                        modifier = Modifier.padding(8.dp),
                        fontSize = 14.sp
                    )
                }
            }
        }

        if (showInfo) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                ElevatedCard(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(modifier = Modifier.size(48.dp))
                            Text(
                                text = "Anbefaling",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(2f),
                                textAlign = TextAlign.Center
                            )
                            IconButton(onClick = { showInfo = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Lukk")
                            }
                        }
                        Text(
                            text = "Anbefalingen tar utgangspunkt i fremkomst til og fra skole eller jobb. Vi antar at reisetidspunktet skjer mellom 8–10 på morgenen og 16–18 på kvelden.",
                        fontSize = 15.sp
                        )
                        OutlinedButton(
                            onClick = { navController.navigate(Routes.ADJUSTMENT) },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Jeg har andre behov")
                            Icon(
                                painter = painterResource(id = R.drawable.arrow_forward_icon),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun rememberSvgImageLoader(): ImageLoader{
    val context = LocalContext.current
    return remember {
        ImageLoader.Builder(context)
            .components { add(SvgDecoder.Factory()) }
            .build()
    }
}
@Composable
private fun ClothingCardRecommendationRows(rec: ClothesRecommendation) {
    // Hodeplagg (kun hvis relevant)
    if (rec.wearSunglasses) {
        ClothingCardRow(iconRes = R.drawable.solbriller, text = "Solbriller anbefales — det er sol.")
    }
    if (rec.wearHatGloves) {
        ClothingCardRow(iconRes = R.drawable.caps, text = "Lue og hansker anbefales.")
    }
    if (rec.wearScarf) {
        ClothingCardRow(iconRes = R.drawable.skjerf, text = "Ta på skjerf.")
    }

    // Overkropp
    when {
        rec.wearTshirt      -> ClothingCardRow(iconRes = R.drawable.t_skjorte, text = "T-skjorte holder fint.")
        rec.wearSweater     -> ClothingCardRow(iconRes = R.drawable.genser,     text = "Genser passer bra.")
        rec.wearLightJacket -> ClothingCardRow(iconRes = R.drawable.lett_jakke,     text = "Ta på en lett jakke.")
        rec.wearHeavyJacket -> ClothingCardRow(iconRes = R.drawable.tykk_jakke,     text = "Tykk jakke anbefales.")
    }
    if (rec.wearThermalUnderwear) {
        ClothingCardRow(iconRes = R.drawable.tskjorte_ikon, text = "Ullundertøy er lurt.")
    }

    // Underkropp
    if (rec.wearShorts) {
        ClothingCardRow(iconRes = R.drawable.shorts, text = "Shorts passer fint.")
    } else {
        ClothingCardRow(iconRes = R.drawable.jeans, text = "Bukse passer til temperaturen.")
    }

    // Sko
    when {
        rec.wearWinterBoots     -> ClothingCardRow(iconRes = R.drawable.st_vler, text = "Vintersko/støvler anbefales.")
        rec.wearWaterproofShoes -> ClothingCardRow(iconRes = R.drawable.st_vler, text = "Vanntette sko anbefales.")
        else                    -> ClothingCardRow(iconRes = R.drawable.sneaker, text = "Hverdagssko passer fint.")
    }

    // Regn
    if (rec.bringUmbrella) {
        ClothingCardRow(iconRes = R.drawable.paraply_ikon, text = "Husk paraply.")
    }
    if (rec.wearRainGear) {
        ClothingCardRow(iconRes = R.drawable.paraply_ikon, text = "Ta på regntøy.")
    }
}

@Composable
private fun ClothingCardRow(iconRes: Int, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(36.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, fontSize = 14.sp, modifier = Modifier.weight(1f))
    }
}