package net.readian.parcel.core.ui.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import net.readian.parcel.core.navigation.PackagesTab
import net.readian.parcel.core.navigation.ProfileTab
import net.readian.parcel.core.navigation.LoginDestination
import net.readian.parcel.core.navigation.PackageDetailDestination
import net.readian.parcel.core.navigation.PackagesDestination
import net.readian.parcel.core.navigation.ProfileDestination
import net.readian.parcel.core.navigation.ParcelDestination
import net.readian.parcel.core.designsystem.icon.ReadianIcons
import net.readian.parcel.core.ui.navigation.TopLevelDestination
import net.readian.parcel.R
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

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

    val showBottomNavigation: Boolean
        @Composable get() {
            return when (currentDestination?.route) {
                LoginDestination::class.qualifiedName -> false
                PackageDetailDestination::class.qualifiedName -> false
                PackagesDestination::class.qualifiedName -> false
                ProfileDestination::class.qualifiedName -> false
                else -> true
            }
        }

    val topLevelDestinations: ImmutableList<TopLevelDestination> = listOfNotNull(
        TopLevelDestination(
            destination = PackagesTab,
            selectedIcon = ReadianIcons.Packages,
            iconTextResource = R.string.packages,
        ),
        TopLevelDestination(
            destination = ProfileTab,
            selectedIcon = ReadianIcons.Profile,
            iconTextResource = R.string.profile,
        ),
    ).toImmutableList()

    fun navigate(destination: ParcelDestination) {
        when (destination) {
            is PackagesTab, is ProfileTab -> {
                navController.navigate(destination) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
            else -> {
                navController.navigate(destination)
            }
        }
    }

    fun onBackClick() {
        navController.popBackStack()
    }
}
