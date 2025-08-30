package net.readian.parcel.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import net.readian.parcel.data.database.converters.Converters
import net.readian.parcel.data.database.dao.PackageDao
import net.readian.parcel.data.database.entity.PackageDataModel

@Database(
    entities = [PackageDataModel::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class ParcelDatabase : RoomDatabase() {
    abstract fun packageDao(): PackageDao

    companion object {
        const val DATABASE_NAME = "parcel_database"
    }
}