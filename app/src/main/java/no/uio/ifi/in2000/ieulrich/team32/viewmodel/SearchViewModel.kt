package no.uio.ifi.in2000.ieulrich.team32.viewmodel

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.ieulrich.team32.data.geocoding.LocationRepository
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {
    private val _suggestions = MutableStateFlow<List<Pair<String, Location>>>(emptyList())
    val suggestions = _suggestions.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches = _recentSearches.asStateFlow()

    private val queryFlow = MutableStateFlow("")

    init {
        viewModelScope.launch {
            queryFlow
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.length >= 2) { //TODO: check if online
                        try {
                            _suggestions.value = locationRepository.searchPlaces(query)
                        } catch (e: Exception) {
                            Log.e("SearchViewModel", "Error searching", e)
                            _suggestions.value = emptyList()
                        }
                    }
                    else {
                        _suggestions.value = emptyList()
                    }
                }
        }
    }

    fun onQueryChange(query: String){
        viewModelScope.launch { queryFlow.emit(query) }
    }

    fun addRecentSearch(name: String){
        _recentSearches.value = (listOf(name) + _recentSearches.value)
            .distinct()
            .take(5)
    }

    fun clearSearch(){
        _suggestions.value = emptyList()
    }

    suspend fun getCoordinatesForName(name: String): Location? {
        return locationRepository.getCoordinatesFromName(name)
    }
}