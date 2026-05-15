package no.uio.ifi.in2000.ieulrich.team32.viewmodel

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import no.uio.ifi.in2000.ieulrich.team32.data.geocoding.LocationRepository
import no.uio.ifi.in2000.ieulrich.team32.data.location.DeviceLocationDataSource
import no.uio.ifi.in2000.ieulrich.team32.data.locationForecast.LocationForecastRepository
import no.uio.ifi.in2000.ieulrich.team32.data.metAlert.MetAlertsRepository
import no.uio.ifi.in2000.ieulrich.team32.model.location.AppLocation
import no.uio.ifi.in2000.ieulrich.team32.model.locationForecast.ForecastHourDetails
import no.uio.ifi.in2000.ieulrich.team32.model.metAlerts.MetAlert
import no.uio.ifi.in2000.ieulrich.team32.model.metAlerts.TimeInterval
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val deviceLocationDataSource: DeviceLocationDataSource = mockk()
    private val locationForecastRepository: LocationForecastRepository = mockk()
    private val alertsRepository: MetAlertsRepository = mockk()
    private val locationRepository: LocationRepository = mockk()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: HomeViewModel

    private val fakeLocation = AppLocation(
        lat = 59.9432,
        lon = 10.7173
    )

    private val fakeForecast = ForecastHourDetails(
        symbolCode = "cloudy",
        timestamp = "2026-05-01T10:00:00Z",
        windSpeed = 10.4,
        temperature = 9.7,
        precipitationAmount = 0.0,
        duration = 1,
        humidity = 50.5,
        windDirection = 120.1
    )

    // brukte et ekte varsel som mock data
    private val fakeAlerts = listOf(
        MetAlert(
                area = "Frøyabanken",
                consequences = "Middels høye bølger: Bølgekammene er ved å brytes opp til sjørokk.",
                eventAwarenessName = "Kuling",
                description = "Sørvest sterk kuling 20 m/s.",
                severity = "Moderate",
                event = "gale",
                timeInterval = TimeInterval(
                    interval = listOf("2026-05-09T10:00:00+00:00", "2026-05-09T16:00:00+00:00")
                ),
                awarenessResponse = "Følg med",
                instruction = "Ikke dra ut i småbåt: Det er farlig å være ute i småbåt."
        ),
        MetAlert(
                area = "Melbu - Torsvåg",
                consequences = "Høye bølger: Sjøen begynner å rulle. Sjørokket kan minske synsvidden.",
                eventAwarenessName = "Kuling",
                description = "Fra lørdag morgen sørvestlig stiv kuling 15 m/s, økende til sterk kuling 20 m/s lørdag formiddag.",
                severity = "Moderate",
                event = "gale",
                timeInterval = TimeInterval(
                    interval = listOf("2026-05-02T04:00:00+00:00", "2026-05-02T12:00:00+00:00")
                ),
                awarenessResponse = "Følg med",
                instruction = "Ikke dra ut i småbåt: Det er farlig å være ute i småbåt. Ved motorstopp kan man drive raskt mot land."
        ),
        MetAlert(
                area = "E3",
                consequences = "Høye bølger: Sjøen begynner å rulle. Sjørokket kan minske synsvidden.",
                eventAwarenessName = "Storm",
                description = "Nordvest liten storm 22 m/s.",
                severity = "Moderate",
                event = "gale",
                timeInterval = TimeInterval(
                    interval = listOf("2026-05-02T01:00:00+00:00", "2026-05-02T11:00:00+00:00")
                ),
                awarenessResponse = "Følg med",
                instruction = "Ikke dra ut i småbåt: Det er farlig å være ute i småbåt."
        ),
        MetAlert(
                area = "Vesterålsbankene",
                consequences = "Høye bølger: Sjøen begynner å rulle. Sjørokket kan minske synsvidden.",
                eventAwarenessName = "Storm",
                description = "Skiftende liten storm 22 m/s.",
                severity = "Moderate",
                event = "gale",
                timeInterval = TimeInterval(
                    interval = listOf("2026-05-02T05:00:00+00:00", "2026-05-02T14:00:00+00:00")
                ),
                awarenessResponse = "Følg med",
                instruction = "Ikke dra ut i småbåt: Det er farlig å være ute i småbåt."
        ),
        MetAlert(
                area = "Svinøy - Frøya",
                consequences = "Grov sjø: Hvitt skum fra bølgetopper som brekker.",
                eventAwarenessName = "Kuling",
                description = "Fra fredag ettermiddag økning til sørvest stiv kuling 15 m/s. Natt til lørdag minkende.",
                severity = "Moderate",
                event = "gale",
                timeInterval = TimeInterval(
                    interval = listOf("2026-05-01T13:00:00+00:00", "2026-05-02T03:00:00+00:00")
                ),
                awarenessResponse = "Følg med",
                instruction = "Vurder å la båten ligge: Det kan være farlig å være ute i småbåt. Ved motorstopp kan man drive raskt mot land."
        ),
        MetAlert(
                area = "Deler av Vestland",
                consequences = "Vegetasjonen kan lett antennes: Store områder kan bli berørt.",
                eventAwarenessName = "Skogbrannfare",
                description = "Lokal lyng- og gressbrannfare i snøfrie områder inntil det kommer nedbør av betydning.",
                severity = "Moderate",
                event = "forestFire",
                timeInterval = TimeInterval(
                    interval = listOf("2026-05-01T10:00:00+00:00", "2026-05-02T10:00:00+00:00")
                ),
                awarenessResponse = "Følg med",
                instruction = "Vegetasjonen kan lett antennes: Ikke bruk åpen ild. Følg instruksjoner fra lokale myndigheter."
            )
        )

    @BeforeEach
    fun setup(){
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown(){
        Dispatchers.resetMain()
    }

    private fun createViewModel() = HomeViewModel(
        deviceLocationDataSource,
        locationForecastRepository,
        alertsRepository,
        locationRepository
    )

    private fun stubHappyPath(
        location: AppLocation? = fakeLocation,
        forecast: ForecastHourDetails? = fakeForecast,
        place: String = "Oslo",
        alerts: List<MetAlert> = fakeAlerts
    ) {
        coEvery { deviceLocationDataSource.getCurrentLocation() } returns location
        coEvery { locationForecastRepository.getForecastNow(any(), any()) } returns forecast
        coEvery { locationRepository.getPlaceName(any(), any()) } returns place
        coEvery { alertsRepository.getAlertsByLocation(any(), any()) } returns alerts
    }

    // tester som burde være suksess

    @Test
    fun `initial uiState is Loading`() {
        stubHappyPath()
        viewModel = createViewModel()

        assertEquals(UiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `loadData emits Success when all repositories return data`() = runTest {
        stubHappyPath()
        viewModel = createViewModel()

        viewModel.loadData()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertInstanceOf<UiState.Success>(state)
        assertEquals(fakeForecast, state.forecast)
        assertEquals("Oslo", state.place)
        assertEquals(fakeAlerts, state.alerts)
    }

    @Test
    fun `loadData uses device location coordinates when location is available`() = runTest {
        stubHappyPath(location = fakeLocation)
        viewModel = createViewModel()

        viewModel.loadData()
        advanceUntilIdle()

        // verifiserer at faktiske koordinater blir brukt, ikke fallback
        coVerify {
            locationForecastRepository.getForecastNow(59.9432, 10.7173)
            alertsRepository.getAlertsByLocation(59.9432,10.7173)
            locationRepository.getPlaceName(59.9432, 10.7173)
        }
    }

    @Test
    fun `loadData falls back to default coordinates when location is null`() = runTest {
        stubHappyPath(location = null)
        viewModel = createViewModel()

        viewModel.loadData()
        advanceUntilIdle()

        coVerify {
            locationForecastRepository.getForecastNow(59.9432, 10.7173)
            alertsRepository.getAlertsByLocation(59.9432, 10.7173)
            locationRepository.getPlaceName(59.9432, 10.7173)
        }

        val state = viewModel.uiState.value as UiState.Success
        // The Location inside the state should carry the fallback coordinates.
        assertEquals(59.9432, state.location!!.lat, 0.0001)
        assertEquals(10.7173, state.location.lon, 0.0001)
    }

    @Test
    fun `loadData Success contains empty alerts when none are returned`() = runTest {
        stubHappyPath(alerts = emptyList())
        viewModel = createViewModel()

        viewModel.loadData()
        advanceUntilIdle()

        val state = viewModel.uiState.value as UiState.Success
        assertTrue(state.alerts.isEmpty())
    }

    // tester som burde gi errors

    @Test
    fun `loadData emits Error when forecast is null`() = runTest {
        stubHappyPath(forecast = null)
        viewModel = createViewModel()

        viewModel.loadData()
        advanceUntilIdle()

        assertEquals(UiState.Error, viewModel.uiState.value)
    }

    @Test
    fun `loadData emits Error when locationForecastRepository throws`() = runTest {
        stubHappyPath()
        coEvery {
            locationForecastRepository.getForecastNow(any(), any())
        } throws Exception("Network error")

        viewModel = createViewModel()
        viewModel.loadData()
        advanceUntilIdle()

        assertEquals(UiState.Error, viewModel.uiState.value)
    }

    @Test
    fun `loadData emits Error when alertsRepository throws`() = runTest {
        stubHappyPath()
        coEvery {
            alertsRepository.getAlertsByLocation(any(), any())
        } throws Exception("Timeout")

        viewModel = createViewModel()
        viewModel.loadData()
        advanceUntilIdle()

        assertEquals(UiState.Error, viewModel.uiState.value)
    }

    @Test
    fun `loadData emits Error when locationRepository throws`() = runTest {
        stubHappyPath()
        coEvery {
            locationRepository.getPlaceName(any(), any())
        } throws Exception("Geocoding failed")

        viewModel = createViewModel()
        viewModel.loadData()
        advanceUntilIdle()

        assertEquals(UiState.Error, viewModel.uiState.value)
    }

    @Test
    fun `loadData emits Error when deviceLocationDataSource throws`() = runTest {
        coEvery {
            deviceLocationDataSource.getCurrentLocation()
        } throws SecurityException("Permission denied")

        viewModel = createViewModel()
        viewModel.loadData()
        advanceUntilIdle()

        assertEquals(UiState.Error, viewModel.uiState.value)
    }

    // overganger mellom states


    @Test
    fun `uiState transitions from Loading to Success`() = runTest {
        stubHappyPath()
        viewModel = createViewModel()

        val emittedStates = mutableListOf<UiState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { emittedStates.add(it) }
        }

        viewModel.loadData()
        advanceUntilIdle()

        assertEquals(UiState.Loading, emittedStates.first())
        assertInstanceOf<UiState.Success>(emittedStates.last())
    }


    @Test
    fun `uiState transitions from Loading to Error on failure`() = runTest {
        stubHappyPath(forecast = null)
        viewModel = createViewModel()

        val emittedStates = mutableListOf<UiState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { emittedStates.add(it) }
        }

        viewModel.loadData()
        advanceUntilIdle()

        assertEquals(UiState.Loading, emittedStates.first())
        assertEquals(UiState.Error, emittedStates.last())
    }

    // teste samtidighet / parallelitet

    @Test
    fun `loadData calls all three repositories concurrently`() = runTest {
        coEvery { deviceLocationDataSource.getCurrentLocation() } returns fakeLocation
        coEvery { locationForecastRepository.getForecastNow(any(), any()) } coAnswers {
            delay(100); fakeForecast
        }
        coEvery { locationRepository.getPlaceName(any(), any()) } coAnswers {
            delay(100); "Oslo"
        }
        coEvery { alertsRepository.getAlertsByLocation(any(), any()) } coAnswers {
            delay(100); fakeAlerts
        }

        viewModel = createViewModel()
        viewModel.loadData()
        advanceUntilIdle()

        assertInstanceOf<UiState.Success>(viewModel.uiState.value)

        assertTrue(currentTime < 200)
    }
}