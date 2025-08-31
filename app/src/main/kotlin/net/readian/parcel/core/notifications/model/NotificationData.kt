package net.readian.parcel.core.notifications.model

data class NotificationData(
  val trackingNumber: String,
  val description: String,
  val carrierName: String?,
  val carrierCode: String,
  val oldStatus: String,
  val newStatus: String,
)
