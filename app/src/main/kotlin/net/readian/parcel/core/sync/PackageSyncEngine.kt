package net.readian.parcel.core.sync

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PackageSyncEngine @Inject constructor(
  private val dataStore: DataStore<Preferences>,
  private val workManager: WorkManager,
) {

  private companion object {
    val LAST_SYNC_TIME_KEY = longPreferencesKey("last_sync_time")
    val LAST_SUCCESS_SYNC_TIME_KEY = longPreferencesKey("last_success_sync_time")
    const val SYNC_INTERVAL_MINUTES = 30L
  }

  fun startSync() {
    val constraints = Constraints.Builder()
      .setRequiredNetworkType(NetworkType.CONNECTED)
      .setRequiresBatteryNotLow(true)
      .build()

    val syncWorkRequest = PeriodicWorkRequestBuilder<PackageBackgroundSyncWorker>(
      repeatInterval = SYNC_INTERVAL_MINUTES,
      repeatIntervalTimeUnit = TimeUnit.MINUTES,
    )
      .setConstraints(constraints)
      .build()

    workManager.enqueueUniquePeriodicWork(
      PackageBackgroundSyncWorker.WORK_NAME,
      ExistingPeriodicWorkPolicy.KEEP,
      syncWorkRequest,
    )

    Timber.d("Background sync scheduled every $SYNC_INTERVAL_MINUTES minutes")
  }

  fun stopSync() {
    workManager.cancelUniqueWork(PackageBackgroundSyncWorker.WORK_NAME)
    Timber.d("Background sync cancelled")
  }

  fun syncNow() {
    val constraints = Constraints.Builder()
      .setRequiredNetworkType(NetworkType.CONNECTED)
      .build()

    val immediateSync = OneTimeWorkRequestBuilder<PackageBackgroundSyncWorker>()
      .setConstraints(constraints)
      .build()

    workManager.enqueue(immediateSync)
    Timber.d("Immediate sync requested")
  }

  fun getSyncStatus(): Flow<SyncStatus> {
    return workManager.getWorkInfosForUniqueWorkFlow(PackageBackgroundSyncWorker.WORK_NAME)
      .map { workInfos ->
        val workInfo = workInfos.firstOrNull()
        when (workInfo?.state) {
          WorkInfo.State.RUNNING -> SyncStatus.Syncing
          WorkInfo.State.ENQUEUED -> SyncStatus.Scheduled
          WorkInfo.State.SUCCEEDED,
          WorkInfo.State.FAILED,
          WorkInfo.State.CANCELLED,
          null,
          -> SyncStatus.Idle

          else -> SyncStatus.Idle
        }
      }
  }

  /**
   * Get last sync time info
   */
  fun getLastSyncInfo(): Flow<SyncInfo> {
    return dataStore.data.map { preferences ->
      val lastSyncTime = preferences[LAST_SYNC_TIME_KEY] ?: 0L
      val lastSuccessTime = preferences[LAST_SUCCESS_SYNC_TIME_KEY] ?: 0L

      SyncInfo(
        lastSyncTime = lastSyncTime,
        lastSuccessfulSyncTime = lastSuccessTime,
        minutesAgo = if (lastSuccessTime > 0) {
          ((System.currentTimeMillis() - lastSuccessTime) / 60_000).toInt()
        } else {
          -1 // Never synced
        },
      )
    }
  }

  /**
   * Update sync timestamps - called by WorkManager
   */
  suspend fun updateSyncTime(success: Boolean) {
    val currentTime = System.currentTimeMillis()
    dataStore.edit { preferences ->
      preferences[LAST_SYNC_TIME_KEY] = currentTime
      if (success) {
        preferences[LAST_SUCCESS_SYNC_TIME_KEY] = currentTime
      }
    }
  }

  /**
   * Check if sync is currently enabled
   */
  fun isSyncEnabled(): Boolean {
    val workInfos = workManager.getWorkInfosForUniqueWork(
      PackageBackgroundSyncWorker.WORK_NAME,
    ).get()
    return workInfos.any {
      it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.RUNNING
    }
  }

  sealed interface SyncStatus {
    data object Idle : SyncStatus
    data object Scheduled : SyncStatus
    data object Syncing : SyncStatus
  }

  data class SyncInfo(
    val lastSyncTime: Long,
    val lastSuccessfulSyncTime: Long,
    val minutesAgo: Int, // -1 if never synced
  ) {
    val lastSyncText: String
      get() = when {
        minutesAgo < 0 -> "Never synced"
        minutesAgo == 0 -> "Just now"
        minutesAgo == 1 -> "1 minute ago"
        minutesAgo < 60 -> "$minutesAgo minutes ago"
        else -> {
          val hours = minutesAgo / 60
          if (hours == 1) "1 hour ago" else "$hours hours ago"
        }
      }
  }
}
