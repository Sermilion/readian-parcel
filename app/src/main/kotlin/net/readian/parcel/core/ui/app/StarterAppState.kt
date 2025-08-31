package net.readian.parcel.core.ui.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import net.readian.parcel.core.navigation.ParcelDestination

@Composable
fun rememberStarterAppState(
  navController: NavHostController = rememberNavController(),
): StarterAppState {
  return remember(navController) {
    StarterAppState(navController = navController)
  }
}

@Stable
class StarterAppState(val navController: NavHostController) {

  val currentDestination: NavDestination?
    @Composable get() = navController.currentBackStackEntryAsState().value?.destination

  fun navigate(destination: ParcelDestination) {
    navController.navigate(destination)
  }

  fun onBackClick() {
    navController.popBackStack()
  }
}
