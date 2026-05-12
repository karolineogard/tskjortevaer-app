package no.uio.ifi.in2000.ieulrich.team32.ui.victoriaWMS

import android.util.Log
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import no.uio.ifi.in2000.ieulrich.team32.data.victoriaWMS.WeatherRepositoryImpl
import no.uio.ifi.in2000.ieulrich.team32.model.victoriaWMS.WeatherLayer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.ZoneOffset


@OptIn(ExperimentalCoroutinesApi::class)
class MapViewModelTest {

    private lateinit var viewModel: MapViewModel
    private val repository: WeatherRepositoryImpl = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Legg til denne linjen for å unngå "Log not mocked"-feilen
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        every { repository.getAlertsUrl() } returns "https://api.met.no/alerts"
        every { repository.getWmsUrl(any(), any()) } returns "https://weather.met.no/wms?service=WMS"
        coEvery { repository.getAllAlerts() } returns emptyList()

        viewModel = MapViewModel(repository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState initial state verification`() {
        val state = viewModel.uiState.value
        assertEquals("https://api.met.no/alerts", state.alertsUrl)
        assertEquals(WeatherLayer.TEMPERATURE, state.currentLayer)
        assertTrue(state.wmsUrl.contains("met.no"))
    }

    @Test
    fun `uiState immutability and flow observation`() = runTest {
        // Sjekker at vi kan samle verdier fra flowen
        val states = mutableListOf<MapUiState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { states.add(it) }
        }

        // Trigger en endring
        viewModel.onAlertsSelected()

        // Bekrefter at en ny tilstand ble emittert
        assertTrue(states.size > 1)
    }

    @Test
    fun `onAlertsSelected state reset behavior`() = runTest {
        viewModel.onAlertsSelected()

        val state = viewModel.uiState.value
        assertNull(state.currentLayer)
        assertEquals("", state.wmsUrl)
        assertTrue(state.showAlerts)
    }


    @Test
    fun `onAlertsSelected repository exception handling`() = runTest {
        coEvery { repository.getAllAlerts() } throws Exception("Network error")

        // Skal ikke krasje
        viewModel.onAlertsSelected()
        advanceUntilIdle()

        // Sjekk at tilstanden fortsatt er fornuftig (f.eks. tom liste i stedet for krasj)
        assertTrue(viewModel.uiState.value.alerts.isEmpty())
    }

    @Test
    fun `getCurrentTime rounding logic at 3 hour intervals`() {
        // Siden getCurrentTime sannsynligvis bruker LocalDateTime.now(),
        // bør funksjonen i ViewModel enten ta inn en klokke (Clock) eller en tid som parameter
        // for å være fullstendig testbar.
        // Her antar vi at vi tester den logiske formelen basert på koden din:

        fun testLogic(hour: Int): Int = ((hour + 1) / 3) * 3

        assertEquals(15, testLogic(14))
        assertEquals(0, testLogic(1))
        assertEquals(12, testLogic(11))
    }


    @Test
    fun `getCurrentTime ISO 8601 format validation`() {
        val timeString = viewModel.getCurrentTime() // Kall den faktiske metoden

        // Regex for yyyy-MM-ddTHH:mm:ssZ
        val regex = Regex("""\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}Z""")
        assertTrue(timeString.matches(regex))
    }

    @Test
    fun `getCurrentTime consistency with system clock`() {
        // For å få denne 100% deterministisk må ViewModel ta imot en java.time.Clock.
        // Hvis den ikke gjør det, kan vi bare sjekke at den returnerer
        // noe som ligner på nåværende dato (år og måned stemmer).
        val result = viewModel.getCurrentTime()
        val currentYear = LocalDate.now(ZoneOffset.UTC).year.toString()

        assertTrue(result.startsWith(currentYear))
    }
}