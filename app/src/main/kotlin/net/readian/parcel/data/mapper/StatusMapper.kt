package net.readian.parcel.data.mapper

import net.readian.parcel.data.database.entity.DeliveryStatusDataModel
import net.readian.parcel.domain.model.DeliveryStatus

/**
 * Maps delivery status between data and domain layers
 * Handles conversion between database entity status and domain status
 */
object StatusMapper {
    
    /**
     * Maps database entity status to domain status
     */
    fun toDomain(dataStatus: DeliveryStatusDataModel): DeliveryStatus {
        return when (dataStatus) {
            DeliveryStatusDataModel.COMPLETED -> DeliveryStatus.COMPLETED
            DeliveryStatusDataModel.FROZEN -> DeliveryStatus.FROZEN
            DeliveryStatusDataModel.IN_TRANSIT -> DeliveryStatus.IN_TRANSIT
            DeliveryStatusDataModel.EXPECTING_PICKUP -> DeliveryStatus.EXPECTING_PICKUP
            DeliveryStatusDataModel.OUT_FOR_DELIVERY -> DeliveryStatus.OUT_FOR_DELIVERY
            DeliveryStatusDataModel.NOT_FOUND -> DeliveryStatus.NOT_FOUND
            DeliveryStatusDataModel.FAILED_DELIVERY -> DeliveryStatus.FAILED_DELIVERY
            DeliveryStatusDataModel.EXCEPTION -> DeliveryStatus.EXCEPTION
            DeliveryStatusDataModel.CARRIER_INFORMED -> DeliveryStatus.CARRIER_INFORMED
        }
    }
    
    /**
     * Maps domain status to database entity status
     */
    fun toData(domainStatus: DeliveryStatus): DeliveryStatusDataModel {
        return when (domainStatus) {
            DeliveryStatus.COMPLETED -> DeliveryStatusDataModel.COMPLETED
            DeliveryStatus.FROZEN -> DeliveryStatusDataModel.FROZEN
            DeliveryStatus.IN_TRANSIT -> DeliveryStatusDataModel.IN_TRANSIT
            DeliveryStatus.EXPECTING_PICKUP -> DeliveryStatusDataModel.EXPECTING_PICKUP
            DeliveryStatus.OUT_FOR_DELIVERY -> DeliveryStatusDataModel.OUT_FOR_DELIVERY
            DeliveryStatus.NOT_FOUND -> DeliveryStatusDataModel.NOT_FOUND
            DeliveryStatus.FAILED_DELIVERY -> DeliveryStatusDataModel.FAILED_DELIVERY
            DeliveryStatus.EXCEPTION -> DeliveryStatusDataModel.EXCEPTION
            DeliveryStatus.CARRIER_INFORMED -> DeliveryStatusDataModel.CARRIER_INFORMED
        }
    }
}