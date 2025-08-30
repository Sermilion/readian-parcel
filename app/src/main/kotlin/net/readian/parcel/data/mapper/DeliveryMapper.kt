package net.readian.parcel.data.mapper

import net.readian.parcel.data.model.DeliveryResponse
import net.readian.parcel.data.model.DeliveryEventResponse
import net.readian.parcel.data.model.DeliveryStatusResponse
import net.readian.parcel.data.model.FilterModeDataModel
import net.readian.parcel.data.model.RateLimitInfoDataModel
import net.readian.parcel.domain.model.Delivery
import net.readian.parcel.domain.model.DeliveryEvent
import net.readian.parcel.domain.model.DeliveryStatus
import net.readian.parcel.domain.model.FilterMode
import net.readian.parcel.domain.model.RateLimitInfo

/**
 * Maps data layer models to domain layer models
 * This enforces the dependency rule: data layer depends on domain, not vice versa
 */
object DeliveryMapper {
    
    fun toDomain(dataModel: DeliveryResponse): Delivery {
        return Delivery(
            trackingNumber = dataModel.trackingNumber,
            carrierCode = dataModel.carrierCode,
            description = dataModel.description,
            status = dataModel.statusCode.toDeliveryStatus(),
            events = dataModel.events.map { toDomain(it) },
            extraInformation = dataModel.extraInformation
        )
    }
    
    fun toDomain(dataModel: DeliveryEventResponse): DeliveryEvent {
        return DeliveryEvent(
            timestamp = parseDateToTimestamp(dataModel.date),
            description = dataModel.event,
            location = dataModel.location
        )
    }
    
    fun parseDateToTimestamp(dateString: String): Long {
        return try {
            // For now, return current time - we can implement proper date parsing later
            // The API date format needs to be determined from actual responses
            System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }
    
    fun toDomain(dataModel: FilterModeDataModel): FilterMode {
        return when (dataModel) {
            FilterModeDataModel.ACTIVE -> FilterMode.ACTIVE
            FilterModeDataModel.RECENT -> FilterMode.RECENT
        }
    }
    
    fun toDomain(dataModel: RateLimitInfoDataModel): RateLimitInfo {
        return RateLimitInfo(
            remainingRequests = dataModel.remainingRequests,
            timeUntilNextRequest = dataModel.timeUntilNextRequest
        )
    }
    
    fun toData(domain: FilterMode): FilterModeDataModel {
        return when (domain) {
            FilterMode.ACTIVE -> FilterModeDataModel.ACTIVE
            FilterMode.RECENT -> FilterModeDataModel.RECENT
        }
    }
    
    private fun Int.toDeliveryStatus(): DeliveryStatus {
        val dataStatus = DeliveryStatusResponse.fromCode(this)
        return when (dataStatus) {
            DeliveryStatusResponse.COMPLETED -> DeliveryStatus.COMPLETED
            DeliveryStatusResponse.FROZEN -> DeliveryStatus.FROZEN
            DeliveryStatusResponse.IN_TRANSIT -> DeliveryStatus.IN_TRANSIT
            DeliveryStatusResponse.EXPECTING_PICKUP -> DeliveryStatus.EXPECTING_PICKUP
            DeliveryStatusResponse.OUT_FOR_DELIVERY -> DeliveryStatus.OUT_FOR_DELIVERY
            DeliveryStatusResponse.NOT_FOUND -> DeliveryStatus.NOT_FOUND
            DeliveryStatusResponse.FAILED_DELIVERY -> DeliveryStatus.FAILED_DELIVERY
            DeliveryStatusResponse.EXCEPTION -> DeliveryStatus.EXCEPTION
            DeliveryStatusResponse.CARRIER_INFORMED -> DeliveryStatus.CARRIER_INFORMED
        }
    }
    
}