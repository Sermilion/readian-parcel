package net.readian.parcel.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "packages")
data class PackageDataModel(
    @PrimaryKey
    val trackingNumber: String,
    val carrierCode: String,
    val description: String,
    val statusCode: DeliveryStatusDataModel,
    val lastUpdated: Long,
    val eventsJson: String,
    val extraInformationJson: String
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
    CARRIER_INFORMED
}