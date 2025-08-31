package net.readian.parcel.data.database.converters

import androidx.room.TypeConverter
import net.readian.parcel.data.database.entity.DeliveryStatusDataModel

class Converters {
  @TypeConverter
  fun fromDeliveryStatus(status: DeliveryStatusDataModel): Int {
    return status.ordinal
  }

  @TypeConverter
  fun toDeliveryStatus(statusCode: Int): DeliveryStatusDataModel {
    return DeliveryStatusDataModel.entries[statusCode]
  }
}
