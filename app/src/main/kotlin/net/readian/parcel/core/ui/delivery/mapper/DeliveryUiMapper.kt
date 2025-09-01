package net.readian.parcel.core.ui.delivery.mapper

import net.readian.parcel.R
import net.readian.parcel.core.ui.delivery.model.DeliveryEventUiModel
import net.readian.parcel.core.ui.delivery.model.DeliveryUiModel
import net.readian.parcel.core.ui.delivery.model.StatusColorUiModel
import net.readian.parcel.domain.model.Delivery
import net.readian.parcel.domain.model.DeliveryEvent
import net.readian.parcel.domain.model.DeliveryStatus

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

  private fun getStatusColor(status: DeliveryStatus): StatusColorUiModel {
    return when (status) {
      DeliveryStatus.COMPLETED -> StatusColorUiModel.SUCCESS
      DeliveryStatus.OUT_FOR_DELIVERY -> StatusColorUiModel.INFO
      DeliveryStatus.IN_TRANSIT -> StatusColorUiModel.INFO
      DeliveryStatus.EXPECTING_PICKUP -> StatusColorUiModel.WARNING
      DeliveryStatus.FAILED_DELIVERY, DeliveryStatus.EXCEPTION -> StatusColorUiModel.ERROR
      else -> StatusColorUiModel.NEUTRAL
    }
  }
}
