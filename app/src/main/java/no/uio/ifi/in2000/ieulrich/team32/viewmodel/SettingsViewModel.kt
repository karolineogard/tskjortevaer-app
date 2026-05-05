package no.uio.ifi.in2000.ieulrich.team32.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel : ViewModel() {
    private val _defaultDepartueHour = MutableStateFlow(8)
    private val _defaultReturnHour = MutableStateFlow(16)
    private val _defaultDepartureMinute = MutableStateFlow(0)
    private val _defaultReturnMinute = MutableStateFlow(0)

    val defaultDepartureMinute: StateFlow<Int> = _defaultDepartureMinute.asStateFlow()
    val defaultReturnMinute: StateFlow<Int> = _defaultReturnMinute.asStateFlow()

    val defaultDepartureHour: StateFlow<Int> = _defaultDepartueHour.asStateFlow()
    val defaultReturnHour: StateFlow<Int> = _defaultReturnHour.asStateFlow()

    fun updateDefaultTimes(
        departureHour: Int,
        departureMinute: Int,
        returnHour: Int,
        returnMinute: Int
    ) {
        _defaultDepartueHour.value = departureHour
        _defaultDepartureMinute.value = departureMinute
        _defaultReturnHour.value = returnHour
        _defaultReturnMinute.value = returnMinute
    }
}

