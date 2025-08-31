package net.readian.parcel.core.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import net.readian.parcel.core.navigation.LoginDestination
import net.readian.parcel.core.navigation.PackageDetailDestination
import net.readian.parcel.core.navigation.PackagesDestination
import net.readian.parcel.feature.login.LoginScreen
import net.readian.parcel.feature.packagedetail.PackageDetailScreen
import net.readian.parcel.feature.packages.PackagesScreen

@Composable
fun ParcelNavHost(
    navController: NavHostController,
    hasApiKey: Boolean,
    modifier: Modifier = Modifier,
) {
    val startDestination = if (hasApiKey) PackagesDestination else LoginDestination

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable<LoginDestination> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(PackagesDestination) {
                        popUpTo<LoginDestination> { inclusive = true }
                    }
                },
                viewModel = hiltViewModel(),
            )
        }

        composable<PackagesDestination> {
            PackagesScreen(
                onPackageClick = { packageId ->
                    navController.navigate(PackageDetailDestination(packageId))
                },
                onLogout = {
                    navController.navigate(LoginDestination) {
                        popUpTo<PackagesDestination> { inclusive = true }
                    }
                },
                viewModel = hiltViewModel(),
            )
        }

        composable<PackageDetailDestination> { backStackEntry ->
            PackageDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                viewModel = hiltViewModel(backStackEntry),
            )
        }
    }
}
