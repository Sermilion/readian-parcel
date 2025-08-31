package net.readian.parcel.feature.packages.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

@Immutable
data class DeliveryUiModel(
  val trackingNumber: String,
  val carrierCode: String,
  val carrierName: String?,
  val description: String,
  @StringRes val statusTextRes: Int,
  val statusColor: StatusColor,
  val events: List<DeliveryEventUiModel>,
  val extraInformation: String,
  val expectedAt: Long? = null,
  val expectedEndAt: Long? = null,
  val expectedDateRaw: String? = null,
  val expectedEndDateRaw: String? = null,
)

@Immutable
data class DeliveryEventUiModel(
  val timestamp: Long?,
  val description: String,
  val location: String?,
  val rawDate: String? = null,
)

enum class StatusColor {
  SUCCESS, // Green - Delivered
  INFO, // Blue - In transit, Out for delivery
  WARNING, // Orange - Expecting pickup
  ERROR, // Red - Failed delivery, Exception
  NEUTRAL, // Gray - Other statuses
}
