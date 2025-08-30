package net.readian.parcel.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "packages")
data class PackageEntity(
    @PrimaryKey
    val trackingNumber: String,
    val carrierCode: String,
    val description: String,
    val statusCode: Int,
    val lastUpdated: Long,
    val eventsJson: String,
    val extraInformationJson: String
)