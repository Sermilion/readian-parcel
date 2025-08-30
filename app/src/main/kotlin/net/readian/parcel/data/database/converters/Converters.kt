package net.readian.parcel.data.database.converters

import androidx.room.TypeConverter
import net.readian.parcel.data.database.entity.DeliveryStatusDataModel

/**
 * Room type converters for custom data types
 * These converters allow Room to store and retrieve enum types
 */
class Converters {
    
    /**
     * Converts DeliveryStatusDataModel enum to integer for database storage
     */
    @TypeConverter
    fun fromDeliveryStatus(status: DeliveryStatusDataModel): Int {
        return status.ordinal
    }
    
    /**
     * Converts integer from database to DeliveryStatusDataModel enum
     */
    @TypeConverter
    fun toDeliveryStatus(statusCode: Int): DeliveryStatusDataModel {
        return DeliveryStatusDataModel.entries[statusCode]
    }
}