package net.readian.parcel.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "packages",
  indices = [
    Index(value = ["lastUpdated"]),
    Index(value = ["carrierCode"]),
  ],
  foreignKeys = [
    ForeignKey(
      entity = CarrierDataModel::class,
      parentColumns = ["code"],
      childColumns = ["carrierCode"],
      onDelete = ForeignKey.NO_ACTION,
      onUpdate = ForeignKey.NO_ACTION,
    ),
  ],
)
data class PackageDataModel(
  @PrimaryKey
  val trackingNumber: String,
  val carrierCode: String,
  val description: String,
  val statusCode: DeliveryStatusDataModel,
  val lastUpdated: Long,
  val extraInformation: String,
  val expectedAt: Long?,
  val expectedEndAt: Long?,
  val expectedDateRaw: String?,
  val expectedEndDateRaw: String?,
  val lastNotifiedStatus: DeliveryStatusDataModel? = null,
)

enum class DeliveryStatusDataModel {
  COMPLETED,
  FROZEN,
  IN_TRANSIT,
  EXPECTING_PICKUP,
  OUT_FOR_DELIVERY,
  NOT_FOUND,
  FAILED_DELIVERY,
  EXCEPTION,
  CARRIER_INFORMED,
}
