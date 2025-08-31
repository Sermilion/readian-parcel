package net.readian.parcel.data.database.model

import androidx.room.Embedded
import androidx.room.Relation
import net.readian.parcel.data.database.entity.DeliveryEventDataModel
import net.readian.parcel.data.database.entity.PackageDataModel

data class PackageWithEvents(
    @Embedded val pkg: PackageDataModel,
    @Relation(
        parentColumn = "trackingNumber",
        entityColumn = "trackingNumber",
    )
    val events: List<DeliveryEventDataModel>,
)
