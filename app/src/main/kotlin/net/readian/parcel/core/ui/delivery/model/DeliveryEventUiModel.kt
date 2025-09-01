package net.readian.parcel.core.ui.delivery.model

import androidx.compose.runtime.Immutable

@Immutable
data class DeliveryEventUiModel(
  val timestamp: Long?,
  val description: String,
  val location: String?,
  val rawDate: String? = null,
)
