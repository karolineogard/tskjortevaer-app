package no.uio.ifi.in2000.ieulrich.team32.ui.screens

import no.uio.ifi.in2000.ieulrich.team32.R
import android.app.appsearch.SearchResults
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.placeCursorAtEnd
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import no.uio.ifi.in2000.ieulrich.team32.ui.Destination
import no.uio.ifi.in2000.ieulrich.team32.ui.Routes
import no.uio.ifi.in2000.ieulrich.team32.ui.victoriaWMS.MapViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*



data class SimpleLatLng(val lat: Double, val lon: Double)

suspend fun getCoordsFromService(sted: String): SimpleLatLng? {
    if (sted.isBlank()) return null

    return withContext(Dispatchers.IO) {
        try {
            val url = URL("https://nominatim.openstreetmap.org/search?q=${sted}&format=json&limit=1")
            val connection = url.openConnection() as java.net.HttpURLConnection


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
    Column(modifier = Modifier.padding(padding),
        verticalArrangement = Arrangement.spacedBy(padding)) {
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
            IconButton(onClick = {søkAktiv =! søkAktiv}, modifier = Modifier.weight(weight = 1f)) {
                Icon(Icons.Default.Search, contentDescription = "Lukk søk",
                    modifier = Modifier.weight(weight = 1f))
            }
        }


        if(søkAktiv){
            TextField(
                value = søkeTekst,
                onValueChange = {søkeTekst = it},
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
                            try{
                                val coords = getCoordsFromService(søkeTekst)
                                if(coords != null) {
                                    navController.navigate("forecast?lat=${coords.lat}&lon=${coords.lon}")
                                }
                            }catch (e: Exception){

                            }
                        }
                        søkAktiv = false
                    }
                )
            )}


        weatherCard(navController = navController)
        Spacer(Modifier.size(padding))
        metalertCard()
        Spacer(Modifier.size(padding))
        clothingCard()
    }



    }






@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun weatherCard(navController : NavController){

    //val textFieldState = rememberTextFieldState()
//    var søkeTekst by remember{mutableStateOf("")}
//    val scope = rememberCoroutineScope()
//    var søkAktiv by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .height(280.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        )
        ){

        Column(modifier = Modifier.padding(16.dp)){
            Text(
                text = "Været nå",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom=8.dp)
            )
//            if(søkAktiv){
//            TextField(
//                value = søkeTekst,
//                onValueChange = {søkeTekst = it},
//                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
//                trailingIcon = {
//                    Row(modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.End) {
//                    IconButton(onClick = {
//                        søkAktiv = false
//                        søkeTekst = ""
//                    }){
//                        Icon(Icons.Default.Close, contentDescription = "Lukk søk")
//                    }
//                    }
//                },
//                singleLine = true,
//                colors = TextFieldDefaults.colors(
//                    focusedIndicatorColor = Color.Transparent,
//                    unfocusedIndicatorColor = Color.Transparent
//                ),
//                modifier = Modifier.fillMaxWidth(),
//                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
//                keyboardActions = KeyboardActions(
//                    onSearch = {
//                        scope.launch {
//                            try{
//                                val coords = getCoordsFromService(søkeTekst)
//                                if(coords != null) {
//                                    navController.navigate("forecast?lat=${coords.lat}&lon=${coords.lon}")
//                                }
//                            }catch (e: Exception){
//
//                            }
//                        }
//                    }
//                )
//            )}
//            else{
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.End
//                ) {
//                IconButton(onClick = {søkAktiv = true}){
//                    Icon(Icons.Default.Search, contentDescription = "Søk")
//                }}
//            }
        }



        Text(
            text = "Været nå",
            modifier = Modifier
                .padding(16.dp),
            textAlign = TextAlign.Center,
        )

    }

}


@Composable
fun metalertCard(){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(43.dp)
            ,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        )

    ){
        Text(
            text = "Farevarsel",
            modifier = Modifier
                .padding(16.dp),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun clothingCard(){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(118.5.dp)
            ,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        )

    ){

        Text(
            text = "Klesanbefaling",
            modifier = Modifier
                .padding(16.dp),
            textAlign = TextAlign.Center,
        )


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




















@Preview
@Composable
fun homescreenPreview(){
    HomeScreen(modifier = Modifier,
        navController = rememberNavController()
    )

}

@Preview
@Composable
fun weatherCardPreview(){
    weatherCard(navController = rememberNavController())
}

@Preview
@Composable
fun metalertCardPreview(){
    metalertCard()
}

@Preview
@Composable
fun clothingCardPreview(){
    clothingCard()
}



@Preview
@Composable
fun searchBarPreview(){

}

