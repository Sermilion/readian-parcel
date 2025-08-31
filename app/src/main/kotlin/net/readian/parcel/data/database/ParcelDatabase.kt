package net.readian.parcel.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import net.readian.parcel.data.database.converters.Converters
import net.readian.parcel.data.database.dao.CarrierDao
import net.readian.parcel.data.database.dao.PackageDao
import net.readian.parcel.data.database.entity.CarrierDataModel
import net.readian.parcel.data.database.entity.DeliveryEventDataModel
import net.readian.parcel.data.database.entity.PackageDataModel

@Database(
    entities = [PackageDataModel::class, DeliveryEventDataModel::class, CarrierDataModel::class],
    version = ParcelDatabase.VERSION,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class ParcelDatabase : RoomDatabase() {
    abstract fun packageDao(): PackageDao
    abstract fun carrierDao(): CarrierDao

    companion object {
        const val DATABASE_NAME = "parcel_database"
        const val VERSION = 1
    }
}
