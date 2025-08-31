package net.readian.parcel.core.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import net.readian.parcel.core.notifications.NotificationService
import net.readian.parcel.core.notifications.model.NotificationData
import net.readian.parcel.data.mapper.StatusMapper
import net.readian.parcel.data.repository.ReadianCarrierRepository
import net.readian.parcel.data.repository.ReadianPackageRepository
import timber.log.Timber

@HiltWorker
class PackageBackgroundSyncWorker @AssistedInject constructor(
  @Assisted context: Context,
  @Assisted workerParams: WorkerParameters,
  private val packageRepository: ReadianPackageRepository,
  private val carrierRepository: ReadianCarrierRepository,
  private val notificationService: NotificationService,
  private val statusMapper: StatusMapper,
  private val syncEngine: PackageSyncEngine,
) : CoroutineWorker(context, workerParams) {

  override suspend fun doWork(): Result {
    return try {
      Timber.d("Sync: Starting background package sync")

      val activePackages = packageRepository.getActivePackages()

      if (activePackages.isEmpty()) {
        Timber.d("Sync: No active packages to sync")
        return Result.success()
      }

      Timber.d("Sync: Syncing ${activePackages.size} active packages")

      packageRepository.refreshPackages()

      val updatedPackages = packageRepository.getActivePackages()
      val statusUpdates = mutableListOf<NotificationData>()

      for (updated in updatedPackages) {
        val previous = activePackages.find { it.trackingNumber == updated.trackingNumber }
        if (previous != null && previous.statusCode != updated.statusCode) {
          val carrier = carrierRepository.getCarrier(updated.carrierCode)
          statusUpdates.add(
            NotificationData(
              trackingNumber = updated.trackingNumber,
              description = updated.description,
              carrierName = carrier?.name,
              carrierCode = updated.carrierCode,
              oldStatus = statusMapper.mapToDisplayText(previous.statusCode),
              newStatus = statusMapper.mapToDisplayText(updated.statusCode),
            ),
          )

          packageRepository.updateLastNotifiedStatus(
            updated.trackingNumber,
            updated.statusCode,
          )
        }
      }

      statusUpdates.forEach { update ->
        notificationService.showPackageStatusUpdate(update)
      }

      Timber.d("Sync: Background sync completed. ${statusUpdates.size} status updates found")
      syncEngine.updateSyncTime(success = true)
      Result.success()
    } catch (e: Exception) {
      Timber.e(e, "Background sync failed")
      syncEngine.updateSyncTime(success = false)
      Result.retry()
    }
  }

  companion object {
    const val WORK_NAME = "package_background_sync"
  }
}
