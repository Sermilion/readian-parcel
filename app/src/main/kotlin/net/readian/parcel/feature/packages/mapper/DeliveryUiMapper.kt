package net.readian.parcel.feature.packages.mapper

import net.readian.parcel.R
import net.readian.parcel.domain.model.Delivery
import net.readian.parcel.domain.model.DeliveryEvent
import net.readian.parcel.domain.model.DeliveryStatus
import net.readian.parcel.feature.packages.model.DeliveryEventUiModel
import net.readian.parcel.feature.packages.model.DeliveryUiModel
import net.readian.parcel.feature.packages.model.StatusColor

/**
 * Maps domain models to presentation/UI models
 * This is where UI-specific logic and formatting happens
 */
object DeliveryUiMapper {

  fun toUiModel(domain: Delivery, carrierName: String? = null): DeliveryUiModel {
    return DeliveryUiModel(
      trackingNumber = domain.trackingNumber,
      carrierCode = domain.carrierCode,
      carrierName = carrierName,
      description = domain.description,
      statusTextRes = getStatusTextRes(domain.status),
      statusColor = getStatusColor(domain.status),
      events = domain.events.map { toUiModel(it) },
      extraInformation = domain.extraInformation,
      expectedAt = domain.expectedAt,
      expectedEndAt = domain.expectedEndAt,
      expectedDateRaw = domain.expectedDateRaw,
      expectedEndDateRaw = domain.expectedEndDateRaw,
    )
  }

  private fun toUiModel(domain: DeliveryEvent): DeliveryEventUiModel {
    return DeliveryEventUiModel(
      timestamp = domain.timestamp,
      description = domain.description,
      location = domain.location,
      rawDate = domain.rawDate,
    )
  }

  private fun getStatusTextRes(status: DeliveryStatus): Int {
    return when (status) {
      DeliveryStatus.COMPLETED -> R.string.delivery_status_completed
      DeliveryStatus.FROZEN -> R.string.delivery_status_frozen
      DeliveryStatus.IN_TRANSIT -> R.string.delivery_status_in_transit
      DeliveryStatus.EXPECTING_PICKUP -> R.string.delivery_status_expecting_pickup
      DeliveryStatus.OUT_FOR_DELIVERY -> R.string.delivery_status_out_for_delivery
      DeliveryStatus.NOT_FOUND -> R.string.delivery_status_not_found
      DeliveryStatus.FAILED_DELIVERY -> R.string.delivery_status_failed_delivery
      DeliveryStatus.EXCEPTION -> R.string.delivery_status_exception
      DeliveryStatus.CARRIER_INFORMED -> R.string.delivery_status_carrier_informed
    }
  }

  private fun getStatusColor(status: DeliveryStatus): StatusColor {
    return when (status) {
      DeliveryStatus.COMPLETED -> StatusColor.SUCCESS
      DeliveryStatus.OUT_FOR_DELIVERY -> StatusColor.INFO
      DeliveryStatus.IN_TRANSIT -> StatusColor.INFO
      DeliveryStatus.EXPECTING_PICKUP -> StatusColor.WARNING
      DeliveryStatus.FAILED_DELIVERY, DeliveryStatus.EXCEPTION -> StatusColor.ERROR
      else -> StatusColor.NEUTRAL
    }
  }
}
