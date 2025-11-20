package net.readian.parcel

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import net.readian.parcel.BuildConfig
import timber.log.Timber

@HiltAndroidApp
class ParcelApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }
  }
}
