package net.readian.parcel.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.readian.parcel.data.database.entity.CarrierDataModel

@Dao
interface CarrierDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(carriers: List<CarrierDataModel>)

    @Query("SELECT * FROM carriers")
    fun observeAll(): Flow<List<CarrierDataModel>>

    @Query("DELETE FROM carriers")
    suspend fun clearAll()
}
