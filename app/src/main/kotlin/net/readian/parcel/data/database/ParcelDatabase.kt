package net.readian.parcel.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import net.readian.parcel.data.database.dao.PackageDao
import net.readian.parcel.data.database.entity.PackageEntity

@Database(
    entities = [PackageEntity::class],
    version = 1,
    exportSchema = true
)
abstract class ParcelDatabase : RoomDatabase() {
    abstract fun packageDao(): PackageDao

    companion object {
        const val DATABASE_NAME = "parcel_database"
    }
}