package net.readian.parcel.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import net.readian.parcel.data.database.entity.DeliveryEventDataModel
import net.readian.parcel.data.database.entity.PackageDataModel
import net.readian.parcel.data.database.model.PackageWithEvents

@Dao
interface PackageDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPackages(packages: List<PackageDataModel>)

  @Query("DELETE FROM packages")
  suspend fun clearAllPackages()

  @Query("SELECT lastUpdated FROM packages ORDER BY lastUpdated DESC LIMIT 1")
  suspend fun getLastUpdateTime(): Long?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertEvents(events: List<DeliveryEventDataModel>)

  @Query("DELETE FROM package_events")
  suspend fun clearAllEvents()

  @Transaction
  @Query("SELECT * FROM packages ORDER BY lastUpdated DESC")
  fun observeAllPackagesWithEvents(): Flow<List<PackageWithEvents>>

  @Transaction
  @Query("SELECT * FROM packages ORDER BY lastUpdated DESC")
  suspend fun getAllPackagesWithEvents(): List<PackageWithEvents>

  @Transaction
  @Query("SELECT * FROM packages WHERE trackingNumber = :trackingNumber")
  fun observePackageWithEvents(trackingNumber: String): Flow<PackageWithEvents?>

  @Transaction
  suspend fun replaceAllPackages(packages: List<PackageDataModel>, events: List<DeliveryEventDataModel>) {
    clearAllPackages()
    clearAllEvents()
    if (packages.isNotEmpty()) {
      insertPackages(packages)
    }
    if (events.isNotEmpty()) {
      insertEvents(events)
    }
  }

  @Transaction
  suspend fun savePackagesWithEvents(packages: List<PackageDataModel>, events: List<DeliveryEventDataModel>) {
    if (packages.isNotEmpty()) {
      insertPackages(packages)
    }
    if (events.isNotEmpty()) {
      insertEvents(events)
    }
  }
}
