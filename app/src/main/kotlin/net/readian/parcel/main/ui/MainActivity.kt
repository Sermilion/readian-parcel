package net.readian.parcel.main.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import net.readian.parcel.core.sync.PackageSyncEngine
import net.readian.parcel.core.ui.app.ParcelApp
import net.readian.parcel.data.datastore.ReadianUserDataStore
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  @Inject
  lateinit var userDataStore: ReadianUserDataStore

  @Inject
  lateinit var syncEngine: PackageSyncEngine

  override fun onCreate(savedInstanceState: Bundle?) {
    installSplashScreen()
    super.onCreate(savedInstanceState)

    syncEngine.startSync()

    setContent {
      ParcelApp(userDataStore = userDataStore)
    }
  }
}
