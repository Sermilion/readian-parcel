package net.readian.parcel.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.readian.parcel.data.database.entity.PackageDataModel

@Dao
interface PackageDao {
    
    @Query("SELECT * FROM packages ORDER BY lastUpdated DESC")
    fun getAllPackages(): Flow<List<PackageDataModel>>
    
    @Query("SELECT * FROM packages WHERE trackingNumber = :trackingNumber")
    suspend fun getPackageByTrackingNumber(trackingNumber: String): PackageDataModel?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPackages(packages: List<PackageDataModel>)
    
    @Query("DELETE FROM packages")
    suspend fun clearAllPackages()
    
    @Query("SELECT lastUpdated FROM packages ORDER BY lastUpdated DESC LIMIT 1")
    suspend fun getLastUpdateTime(): Long?
}