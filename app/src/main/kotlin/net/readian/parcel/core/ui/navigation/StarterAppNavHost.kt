package net.readian.parcel.core.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import net.readian.parcel.core.navigation.LoginDestination
import net.readian.parcel.core.navigation.PackageDetailDestination
import net.readian.parcel.core.navigation.PackagesDestination
import net.readian.parcel.core.navigation.PackagesTab
import net.readian.parcel.core.navigation.ProfileDestination
import net.readian.parcel.core.navigation.ProfileTab
import net.readian.parcel.feature.login.LoginScreen
import net.readian.parcel.feature.packages.PackagesScreen

@Composable
fun ParcelNavHost(
    navController: NavHostController,
    hasApiKey: Boolean,
    modifier: Modifier = Modifier,
) {
    val startDestination = if (hasApiKey) PackagesTab else LoginDestination
    
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable<LoginDestination> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(PackagesTab) {
                        popUpTo<LoginDestination> { inclusive = true }
                    }
                }
            )
        }
        
        composable<PackagesDestination> {
            PackagesScreen(
                onPackageClick = { packageId ->
                    navController.navigate(PackageDetailDestination(packageId))
                },
                onNavigateToProfile = {
                    navController.navigate(ProfileDestination)
                }
            )
        }
        
        composable<PackagesTab> {
            PackagesScreen(
                onPackageClick = { packageId ->
                    navController.navigate(PackageDetailDestination(packageId))
                },
                onNavigateToProfile = {
                    navController.navigate(ProfileTab)
                }
            )
        }
        
        composable<ProfileDestination> {
            // TODO: Implement ProfileScreen
            // ProfileScreen(
            //     onLogout = {
            //         navController.navigate(LoginDestination) {
            //             popUpTo<PackagesDestination> { inclusive = true }
            //         }
            //     },
            //     onNavigateBack = {
            //         navController.popBackStack()
            //     }
            // )
        }
        
        composable<ProfileTab> {
            // TODO: Implement ProfileScreen
            // ProfileScreen(
            //     onLogout = {
            //         navController.navigate(LoginDestination) {
            //             popUpTo<PackagesTab> { inclusive = true }
            //         }
            //     },
            //     onNavigateBack = {
            //         navController.navigate(PackagesTab)
            //     }
            // )
        }
        
        composable<PackageDetailDestination> { backStackEntry ->
            val packageDetail = backStackEntry.toRoute<PackageDetailDestination>()
            // TODO: Implement PackageDetailScreen
            // PackageDetailScreen(
            //     packageId = packageDetail.packageId,
            //     onNavigateBack = {
            //         navController.popBackStack()
            //     }
            // )
        }
    }
}
