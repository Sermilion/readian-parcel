package net.readian.parcel.main.ui

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import dagger.hilt.android.AndroidEntryPoint
import net.readian.parcel.core.designsystem.theme.ReadianTheme
import net.readian.parcel.core.ui.app.ParcelApp
import net.readian.parcel.domain.datastore.UserDataStore
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  @Inject
  lateinit var userDataStore: UserDataStore

  override fun onCreate(savedInstanceState: Bundle?) {
    val splashScreen = installSplashScreen()
    splashScreen.setOnExitAnimationListener(::animateSplashScreenExit)

    super.onCreate(savedInstanceState)

    setContent {
      ReadianTheme {
        ParcelApp(userDataStore = userDataStore)
      }
    }
  }

  private fun animateSplashScreenExit(splashScreenView: SplashScreenViewProvider) {
    val slideUp = ObjectAnimator.ofFloat(
      splashScreenView.iconView,
      "translationY",
      0f,
      -splashScreenView.iconView.height.toFloat(),
    )
    slideUp.interpolator = OvershootInterpolator()
    slideUp.duration = ANIMATION_DURATION
    slideUp.doOnEnd { splashScreenView.remove() }
    slideUp.start()
  }

  private companion object {
    private const val ANIMATION_DURATION = 500L
  }
}
