package no.uio.ifi.in2000.ieulrich.team32.ui.screens


import android.R.attr.contentDescription
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import no.uio.ifi.in2000.ieulrich.team32.R
import no.uio.ifi.in2000.ieulrich.team32.ui.Destination
import no.uio.ifi.in2000.ieulrich.team32.ui.Routes
import java.net.HttpURLConnection




data class SimpleLatLng(val lat: Double, val lon: Double)

// placeholder
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
                val lat = firstResult.getDouble("lat")
                val lon = firstResult.getDouble("lon")
                SimpleLatLng(lat, lon)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController
){
    val textFieldState = rememberTextFieldState()
    val scope = rememberCoroutineScope()
    val padding = 16.dp
    var søkeTekst by remember{mutableStateOf("")}
    var søkAktiv by remember { mutableStateOf(false) }

    //hardkodet
    var isVisible by remember { mutableStateOf(true) }

    LazyColumn(modifier = Modifier.padding(padding),
        verticalArrangement = Arrangement.spacedBy(padding)) {
//        val now = LocalDateTime.now()
//        val today = LocalDate.now()
//        val datePart = if (now.toLocalDate().isEqual(today)) {
//            "I dag"
//        } else {
//            now.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
//        }
//
//        val timePart = now.format(DateTimeFormatter.ofPattern("HH:mm"))
//        val currentDateAndTime = "$datePart $timePart"
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
//            Icon(painter = painterResource(id = R.drawable.klokke_ikon), contentDescription = null,
//                modifier = Modifier.weight(weight = 0.4f)
//            )
//            Text(text = currentDateAndTime,
//                modifier = Modifier.weight(weight = 3f)
//               )
                Text(text = "Noe?",  modifier = Modifier.weight(5f))


                IconButton(onClick = { søkAktiv = !søkAktiv },) {
                    Icon(Icons.Default.Search, contentDescription = "Lukk søk")

                }
            }


            if (søkAktiv) {
                TextField(
                    value = søkeTekst,
                    onValueChange = { søkeTekst = it },
                    //leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            scope.launch {
                                try {
                                    val coords = getCoordsFromService(søkeTekst)
                                    if (coords != null) {
                                        navController.navigate("forecast?lat=${coords.lat}&lon=${coords.lon}")
                                    }
                                } catch (e: Exception) {

                                }
                            }
                            søkAktiv = false
                        }
                    )
                )
            }

        }




        item {
        weatherCard(navController = navController)}

        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
//                    Text(
//                        text = "Farevarsler",
//                        style = MaterialTheme.typography.titleMedium,
//                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
//                    )
                    MetalertCarousel(modifier = Modifier.height(80.dp))
                   // Spacer(Modifier.height(padding))
                }
            }
        }

        item {
            clothingCard(navController = navController)
        }
    }



    }


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetalertCarousel(modifier: Modifier = Modifier){
    val varselTekster = listOf("Sterk vind", "Flom", "Orkan")

    Column(modifier = modifier) {


        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            //contentPadding = PaddingValues(horizontal = 16.dp), // Luft på sidene
            horizontalArrangement = Arrangement.spacedBy(12.dp) // Luft mellom kortene
        ) {
            items(varselTekster.size) { index ->
                val tekst = varselTekster[index]
                Box(modifier = Modifier.width(280.dp)) {
                    metalertCard(tekst = tekst)
                }
            }
    }
    }




}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun weatherCard(navController : NavController){



    Card(
        modifier = Modifier
            .height(280.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        )
        ){

        Column(modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ){
            val now = LocalDateTime.now()
            val today = LocalDate.now()
            val datePart = if (now.toLocalDate().isEqual(today)) {
                "I dag"
            } else {
                now.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            }

            val timePart = now.format(DateTimeFormatter.ofPattern("HH:mm"))
            val currentDateAndTime = "$datePart $timePart"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(painter = painterResource(id = R.drawable.klokke_ikon), contentDescription = null,
                    modifier = Modifier.weight(weight = 0.4f)
                )
                Text(text = currentDateAndTime,
                    modifier = Modifier.weight(weight = 3f)
                )

            }


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(text = "Oslo",
                    fontSize = 40.sp,

                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(painter = painterResource(id = R.drawable.clearsky_day), contentDescription = null,
                    modifier = Modifier
                        .size(70.dp)
                )

                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "8°C",
                    modifier = Modifier,
                    fontSize = 40.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(text = "Strålende sol",
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(text = "H:14°  L: 5°",
                )
            }


        }





    }

}


@Composable
fun metalertCard(
    tekst : String

){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            ,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        )

    ){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = tekst,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

    }
}

@Composable
fun clothingCard(navController: NavController,
                 modifier: Modifier = Modifier
) {

    var clicked by rememberSaveable { mutableStateOf(false) }
    //var isVisible by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxWidth().height(300.dp),) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            )

        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
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
                    IconButton(onClick = { clicked = !clicked },) {
                        Icon(Icons.Default.Info, contentDescription = "Lukk søk")


                    }


                }



            }

            }


    if (clicked) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentAlignment = Alignment.Center
        ) {
            ElevatedCard(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                modifier = Modifier.width(320.dp),


                ) {
                LazyColumn(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Spacer(modifier = Modifier.size(48.dp))
                            Text(
                                text = "Anbefaling",
                                fontSize = 30.sp,
                                modifier = Modifier.weight(2f),
                                textAlign = TextAlign.Center
                            )
                            IconButton(onClick = { clicked = false }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                )
                            }


                        }






                        Text(
                            text = "Anbefalningen tar utgangspunkt i fremkomst til og fra skole eller jobb. \n" +
                                    "\n" +
                                    "Vi antar at reisetidspunktet skjer mellom 8-10 på morgenen og 16-18 på kvelden."
                        )

                    }

                    item {

                        Button(
                            onClick = { navController.navigate(Routes.ADJUSTMENT) }
                        ) {
                            Text(
                                text = "Jeg har andre behov ->"
                            )

                        }

                    }
                }
            }


        }
    }
    }
}








//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun SearchBar(
//    textFieldState: TextFieldState,
//    onSearch: (String) -> Unit,
//    searchResults: List<String>,
//    modifier: Modifier = Modifier
//){
//    var expanded by rememberSaveable { mutableStateOf((false))}
//
//    Box(
//        modifier
//            .fillMaxWidth()
//            .semantics{isTraversalGroup = true}
//    ){
//        if(!expanded){
//            IconButton(onClick = { expanded = true }) {
//                Icon(
//                    imageVector = Icons.Default.Search,
//                    contentDescription = "Åpne søk"
//                )
//            }
//        } else{
//
//            SearchBar(
//                modifier = Modifier
//                    .align(Alignment.TopCenter)
//                    .semantics{traversalIndex = 0f},
//                inputField = {
//                    SearchBarDefaults.InputField(
//                        query = textFieldState.text.toString(),
//                        onQueryChange = {textFieldState.edit {
//                            replace(0, length, it)
//                            placeCursorAtEnd()}},
//                        onSearch = {
//                            onSearch(textFieldState.text.toString())
//                            expanded = false
//                        },
//                        expanded = expanded,
//                        onExpandedChange = {expanded = it},
//                        placeholder = {Text("Søk")}
//                    )
//                },
//                expanded = expanded,
//                onExpandedChange = {expanded = it},
//            ){
//                Column(Modifier.verticalScroll(rememberScrollState())){
//                    searchResults.forEach { result ->
//                        ListItem(
//                            headlineContent = {Text(result)},
//                            modifier = Modifier
//                                .clickable{
//                                    textFieldState.edit{replace(0, length, result)}
//                                    expanded = false
//                                }
//                                .fillMaxWidth()
//                        )
//                    }
//                }
//            }
//            }
//    }
//
//}




















//@Preview
//@Composable
//fun homescreenPreview(){
//    HomeScreen(modifier = Modifier,
//        navController = rememberNavController()
//    )
//
//}
//
//@Preview
//@Composable
//fun weatherCardPreview(){
//    weatherCard(navController = rememberNavController())
//}

//@Preview
//@Composable
//fun metalertCardPreview(){
//    metalertCard()
//}

//@Preview
//@Composable
//fun clothingCardPreview(){
//    clothingCard(navController = NavController)
//}





//@Preview
//@Composable
//fun metalertCarouselPreview(){
//    metalertCarousel()
//}
