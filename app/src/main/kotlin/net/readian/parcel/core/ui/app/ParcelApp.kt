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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.map
import net.readian.parcel.core.designsystem.component.ReadianBackground
import net.readian.parcel.core.designsystem.theme.StarterAppTheme
import net.readian.parcel.core.ui.navigation.ParcelNavHost
import net.readian.parcel.domain.datastore.UserDataStore

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ParcelApp(
  userDataStore: UserDataStore,
  modifier: Modifier = Modifier,
  navController: NavHostController = rememberNavController(),
) {
  val isLoggedIn by userDataStore.userData.map {
    it.isLoggedIn
  }.collectAsStateWithLifecycle(initialValue = false)

  StarterAppTheme {
    ReadianBackground {
      Scaffold(
        modifier = Modifier
          .systemBarsPadding()
          .then(modifier),
      ) { innerPadding ->
        ParcelNavHost(
          navController = navController,
          hasApiKey = isLoggedIn,
          modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .consumeWindowInsets(innerPadding)
            .windowInsetsPadding(
              WindowInsets.safeDrawing.only(
                WindowInsetsSides.Vertical,
              ),
            ),
        )
      }
    }
  }
}
