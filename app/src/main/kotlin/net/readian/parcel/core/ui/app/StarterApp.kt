package net.readian.parcel.core.ui.app

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import net.readian.parcel.core.designsystem.component.ReadianBackground
import net.readian.parcel.core.designsystem.theme.StarterAppTheme
import net.readian.parcel.core.designsystem.icon.asComposable
import net.readian.parcel.core.navigation.ParcelDestination
import net.readian.parcel.core.ui.navigation.ReadianNavigationBar
import net.readian.parcel.core.ui.navigation.ReadianNavigationBarItem
import net.readian.parcel.core.ui.navigation.ParcelNavHost
import net.readian.parcel.core.ui.navigation.TopLevelDestination
import net.readian.parcel.core.update.UpdateChecker
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ParcelApp(
    hasApiKey: Boolean,
    modifier: Modifier = Modifier,
    appState: StarterAppState = rememberStarterAppState(),
) {
    StarterAppTheme {
        ReadianBackground {
            UpdateChecker {
                val systemUiController = rememberSystemUiController()
                systemUiController.setSystemBarsColor(MaterialTheme.colorScheme.background)

                Scaffold(
                    modifier = Modifier
                        .systemBarsPadding()
                        .then(modifier),
                    bottomBar = {
                        if (appState.showBottomNavigation) {
                            StarterAppBottomBar(
                                destinations = appState.topLevelDestinations,
                                onNavigateToDestination = appState::navigate,
                                currentDestination = appState.currentDestination,
                            )
                        }
                    },
                ) { innerPadding ->
                    ParcelNavHost(
                        navController = appState.navController,
                        hasApiKey = hasApiKey,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .consumeWindowInsets(innerPadding)
                            .windowInsetsPadding(
                                WindowInsets.safeDrawing.only(
                                    WindowInsetsSides.Vertical,
                                ),
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun StarterAppBottomBar(
    destinations: ImmutableList<TopLevelDestination>,
    onNavigateToDestination: (ParcelDestination) -> Unit,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier,
) {
    ReadianNavigationBar(modifier = modifier) {
        destinations.forEach { destination ->
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)

            ReadianNavigationBarItem(
                selected = selected,
                onClick = { onNavigateToDestination(destination.destination) },
                icon = { 
                    when (val icon = destination.selectedIcon) {
                        is net.readian.parcel.core.designsystem.icon.Icon.ImageVectorIcon -> {
                            androidx.compose.material3.Icon(
                                imageVector = icon.imageVector,
                                contentDescription = stringResource(destination.iconTextResource)
                            )
                        }
                        is net.readian.parcel.core.designsystem.icon.Icon.DrawableResourceIcon -> {
                            androidx.compose.material3.Icon(
                                painter = androidx.compose.ui.res.painterResource(id = icon.id),
                                contentDescription = stringResource(destination.iconTextResource)
                            )
                        }
                    }
                },
                label = { Text(stringResource(destination.iconTextResource)) },
            )
        }
    }
}

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.destination::class.simpleName ?: "") == true
    } ?: false
