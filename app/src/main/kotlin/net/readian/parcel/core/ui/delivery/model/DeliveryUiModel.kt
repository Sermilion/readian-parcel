package net.readian.parcel.core.ui.delivery.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

@Immutable
data class DeliveryUiModel(
  val trackingNumber: String,
  val carrierCode: String,
  val carrierName: String?,
  val description: String,
  @StringRes val statusTextRes: Int,
  val statusColor: StatusColorUiModel,
  val events: List<DeliveryEventUiModel>,
  val extraInformation: String,
  val expectedAt: Long? = null,
  val expectedEndAt: Long? = null,
  val expectedDateRaw: String? = null,
  val expectedEndDateRaw: String? = null,
)
