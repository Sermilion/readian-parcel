package net.readian.parcel.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "package_events",
    foreignKeys = [
        ForeignKey(
            entity = PackageDataModel::class,
            parentColumns = ["trackingNumber"],
            childColumns = ["trackingNumber"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION,
            deferred = false,
        ),
    ],
    indices = [
        Index(value = ["trackingNumber"]),
        Index(value = ["timestamp"]),
    ],
)
data class DeliveryEventDataModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val trackingNumber: String,
    val description: String,
    val rawDate: String?,
    val location: String?,
    val additional: String?,
    val timestamp: Long?,
)
