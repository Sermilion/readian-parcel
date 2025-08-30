package net.readian.parcel.feature.packages.util

import androidx.annotation.StringRes
import net.readian.parcel.R
import net.readian.parcel.data.model.DeliveryStatusResponse

/**
 * Maps delivery status data models to UI string resources
 * This belongs in the presentation layer to maintain clean architecture separation
 */
object DeliveryStatusMapper {
    
    @StringRes
    fun getDisplayNameRes(status: DeliveryStatusResponse): Int {
        return when (status) {
            DeliveryStatusResponse.COMPLETED -> R.string.delivery_status_completed
            DeliveryStatusResponse.FROZEN -> R.string.delivery_status_frozen
            DeliveryStatusResponse.IN_TRANSIT -> R.string.delivery_status_in_transit
            DeliveryStatusResponse.EXPECTING_PICKUP -> R.string.delivery_status_expecting_pickup
            DeliveryStatusResponse.OUT_FOR_DELIVERY -> R.string.delivery_status_out_for_delivery
            DeliveryStatusResponse.NOT_FOUND -> R.string.delivery_status_not_found
            DeliveryStatusResponse.FAILED_DELIVERY -> R.string.delivery_status_failed_delivery
            DeliveryStatusResponse.EXCEPTION -> R.string.delivery_status_exception
            DeliveryStatusResponse.CARRIER_INFORMED -> R.string.delivery_status_carrier_informed
        }
    }
}