package net.readian.parcel.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "carriers",
  indices = [Index(value = ["name"])],
)
data class CarrierDataModel(
  @PrimaryKey val code: String,
  val name: String,
  val updatedAt: Long,
)
