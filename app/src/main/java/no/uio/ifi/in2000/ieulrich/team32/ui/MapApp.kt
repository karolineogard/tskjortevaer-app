package no.uio.ifi.in2000.ieulrich.team32.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import no.uio.ifi.in2000.ieulrich.team32.ui.screens.AdjustmentScreen
import no.uio.ifi.in2000.ieulrich.team32.ui.screens.ClothesScreen
import no.uio.ifi.in2000.ieulrich.team32.ui.screens.HomeScreen
import no.uio.ifi.in2000.ieulrich.team32.ui.screens.LocationForecastScreen
import no.uio.ifi.in2000.ieulrich.team32.ui.screens.MapScreen
import no.uio.ifi.in2000.ieulrich.team32.ui.screens.SettingsScreen

@Composable
fun MapApp(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
){

    val startDestination = Destination.HOME
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }
    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar{
                Destination.entries.forEachIndexed{ index, destination ->
                    NavigationBarItem(
                        selected = selectedDestination == index,
                        onClick = {
                            navController.navigate(route = destination.route)
                            selectedDestination = index
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = destination.icon),
                                contentDescription = destination.contentDescription
                            )
                        },
                        label = {Text(destination.label)}
                    )
                }
            }
        }
    )
    { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                fadeIn(animationSpec = tween(400))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(400))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(400))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(400))
            }
        ) {
            composable(route = Routes.MAP,
                enterTransition = {
                   EnterTransition.None
                },
                    exitTransition = {
                ExitTransition.None
                },
                popEnterTransition = {
                   EnterTransition.None
                },
                popExitTransition = {
                    ExitTransition.None
                })
            {
                MapScreen(navController = navController)
            }

            composable(route = "forecast?lat={lat}&lon={lon}") { backStackEntry ->
                val lat = backStackEntry.arguments?.getString("lat")?.toDoubleOrNull()
                val lon = backStackEntry.arguments?.getString("lon")?.toDoubleOrNull()
                LocationForecastScreen(lat = lat, lon = lon, navController = navController)
            }

            composable(route = Routes.HOME){
                HomeScreen(navController = navController)
            }


            composable(route = Routes.SETTINGS){
                SettingsScreen(navController=navController)
            }

            composable(route = Routes.CLOTHES){
                ClothesScreen(navController=navController)
            }

            composable(route = Routes.ADJUSTMENT) {
                AdjustmentScreen(navController = navController)
            }
        }
    }
}


@Preview
@Composable
fun NavbarPreview() {
    MapApp()
}