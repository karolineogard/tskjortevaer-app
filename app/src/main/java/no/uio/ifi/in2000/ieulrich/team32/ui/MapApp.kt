package no.uio.ifi.in2000.ieulrich.team32.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import no.uio.ifi.in2000.ieulrich.team32.data.client.NetworkMonitor
import no.uio.ifi.in2000.ieulrich.team32.ui.screens.AdjustmentScreen
import no.uio.ifi.in2000.ieulrich.team32.ui.screens.ClothesScreen
import no.uio.ifi.in2000.ieulrich.team32.ui.screens.HomeScreen
import no.uio.ifi.in2000.ieulrich.team32.ui.screens.LocationForecastScreen
import no.uio.ifi.in2000.ieulrich.team32.ui.screens.MapScreen
import no.uio.ifi.in2000.ieulrich.team32.ui.screens.SettingsScreen
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.Grey
import no.uio.ifi.in2000.ieulrich.team32.ui.theme.MediumBlue
import no.uio.ifi.in2000.ieulrich.team32.viewmodel.ClothesViewModel
import no.uio.ifi.in2000.ieulrich.team32.viewmodel.HomeViewModel
import no.uio.ifi.in2000.ieulrich.team32.viewmodel.LocationForecastViewmodel
import no.uio.ifi.in2000.ieulrich.team32.viewmodel.SettingsViewModel

@Composable
fun MapApp(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier,
) {
    val locationForecastViewmodel: LocationForecastViewmodel = hiltViewModel()
    val homeViewModel: HomeViewModel = hiltViewModel()
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val clothesViewModel: ClothesViewModel = hiltViewModel()
    val context = LocalContext.current

    // Nettverksovervåking
    val networkMonitor = remember { NetworkMonitor(context) }
    DisposableEffect(Unit) {
        onDispose { networkMonitor.unregister() }
    }
    val isOnline by networkMonitor.isOnline.collectAsState()

    // Sync temperatureOffset fra innstillinger til klesanbefalingen
    val temperatureOffset by settingsViewModel.temperatureOffset.collectAsState()
    LaunchedEffect(temperatureOffset) {
        clothesViewModel.updateTemperatureOffset(temperatureOffset)
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                Destination.entries.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = currentRoute == destination.route,
                        onClick = {
                            navController.navigate(destination.route) {
                                launchSingleTop = true
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = destination.icon),
                                contentDescription = destination.contentDescription
                            )
                        },
                        label = { Text(destination.label) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MediumBlue,
                            selectedIconColor = Grey,
                            selectedTextColor = Grey,
                            unselectedIconColor = Grey,
                            unselectedTextColor = Grey
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = Routes.HOME,
                modifier = Modifier.padding(innerPadding),
                enterTransition = { fadeIn(animationSpec = tween(400)) },
                exitTransition = { fadeOut(animationSpec = tween(400)) },
                popEnterTransition = { fadeIn(animationSpec = tween(400)) },
                popExitTransition = { fadeOut(animationSpec = tween(400)) }
            ) {
                composable(
                    route = Routes.MAP,
                    enterTransition = { EnterTransition.None },
                    exitTransition = { ExitTransition.None },
                    popEnterTransition = { EnterTransition.None },
                    popExitTransition = { ExitTransition.None }
                ) {
                    MapScreen(navController = navController, isOnline = isOnline)
                }

                composable(route = "forecast?lat={lat}&lon={lon}&city={city}") { backStackEntry ->
                    val lat = backStackEntry.arguments?.getString("lat")?.toDoubleOrNull()
                    val lon = backStackEntry.arguments?.getString("lon")?.toDoubleOrNull()
                    val city = backStackEntry.arguments?.getString("city") ?: "Værvarsel"
                    LocationForecastScreen(
                        viewmodel = locationForecastViewmodel,
                        lat = lat,
                        lon = lon,
                        city = city,
                        navController = navController
                    )
                }

                composable(route = Routes.HOME) {
                    HomeScreen(
                        navController = navController,
                        viewmodel = locationForecastViewmodel,
                        clothesViewModel = clothesViewModel,
                        homeViewModel = homeViewModel,
                        isOnline = isOnline
                    )
                }

                composable(route = Routes.SETTINGS) {
                    SettingsScreen(navController = navController, settingsViewModel = settingsViewModel)
                }

                composable(route = Routes.CLOTHES) {
                    ClothesScreen(
                        navController = navController,
                        clothesViewModel = clothesViewModel,
                        settingsViewModel = settingsViewModel,
                        isOnline = isOnline
                    )
                }

                composable(route = Routes.ADJUSTMENT) {
                    AdjustmentScreen(navController = navController)
                }
            }

            // Ingen-nett-banner — vises øverst på alle skjermer
            AnimatedVisibility(
                visible = !isOnline,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = innerPadding.calculateTopPadding())
            ) {
                Text(
                    text = "Ingen internettforbindelse",
                    color = MaterialTheme.colorScheme.onError,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.error)
                        .padding(vertical = 6.dp)
                )
            }
        }
    }
}