package no.uio.ifi.in2000.ieulrich.team32.ui.components

import android.location.Location
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.ieulrich.team32.viewmodel.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onPlaceSelected: (name: String, location: Location) -> Unit
) {
    val viewModel: SearchViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    var searchText by remember { mutableStateOf("") }
    var hasFocus by remember { mutableStateOf(false) }

    val suggestions by viewModel.suggestions.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()

    val showSuggestions = hasFocus && suggestions.isNotEmpty()
    val showRecent = hasFocus && searchText.isEmpty() && recentSearches.isNotEmpty()

    fun selectPlace(name: String, location: Location) {
        viewModel.addRecentSearch(name)
        searchText = ""
        viewModel.clearSearch()
        onPlaceSelected(name, location)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(28.dp),
            shadowElevation = 4.dp
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { newText ->
                    searchText = newText
                    viewModel.onQueryChange(newText)
                },
                placeholder = { Text("Søk etter by...") },
                singleLine = true,
                shape = MaterialTheme.shapes.extraLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { hasFocus = it.isFocused },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Søk") },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = {
                            searchText = ""
                            viewModel.onQueryChange("")
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Tøm")
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        val first = suggestions.firstOrNull()
                        if (first != null) {
                            selectPlace(first.first, first.second)
                        } else {
                            scope.launch {
                                val location = viewModel.getCoordinatesForName(searchText)
                                if (location != null) selectPlace(searchText, location)
                            }
                        }
                    }
                )
            )
        }

        if (showSuggestions) {
            SearchCard {
                suggestions.forEach { (name, location) ->
                    ListItem(
                        headlineContent = { Text(name) },
                        modifier = Modifier.clickable { selectPlace(name, location) }
                    )
                    HorizontalDivider()
                }
            }
        }

        if (showRecent) {
            SearchCard {
                Text(
                    text = "Sist søkt",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                recentSearches.forEach { name ->
                    ListItem(
                        headlineContent = { Text(name) },
                        leadingContent = {
                            Icon(painterResource(android.R.drawable.ic_menu_recent_history), "Nylige søk")
                        },
                        modifier = Modifier.clickable {
                            scope.launch {
                                val location = viewModel.getCoordinatesForName(name)
                                if (location != null) selectPlace(name, location)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchCard(content: @Composable ColumnScope.() -> Unit){
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(content = content)
    }
}