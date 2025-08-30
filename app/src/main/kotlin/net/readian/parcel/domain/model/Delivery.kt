package net.readian.parcel.domain.model

/**
 * Domain model for a delivery/package
 * Contains business logic without dependencies on data layer or presentation layer
 */
data class Delivery(
    val trackingNumber: String,
    val carrierCode: String,
    val description: String,
    val status: DeliveryStatus,
    val events: List<DeliveryEvent>,
    val extraInformation: String
)

/**
 * Domain model for delivery status
 * Pure business logic without UI or data layer dependencies
 */
enum class DeliveryStatus {
    COMPLETED,
    FROZEN,
    IN_TRANSIT,
    EXPECTING_PICKUP,
    OUT_FOR_DELIVERY,
    NOT_FOUND,
    FAILED_DELIVERY,
    EXCEPTION,
    CARRIER_INFORMED
}

/**
 * Domain model for delivery events
 */
data class DeliveryEvent(
    val timestamp: Long,
    val description: String,
    val location: String?
)