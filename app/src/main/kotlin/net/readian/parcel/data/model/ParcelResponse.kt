package net.readian.parcel.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ParcelResponse(
    @SerialName("success")
    val success: Boolean,
    @SerialName("error_message")
    val errorMessage: String? = null,
    @SerialName("deliveries")
    val deliveries: List<Delivery> = emptyList()
)

@Serializable
data class Delivery(
    @SerialName("carrier_code")
    val carrierCode: String,
    @SerialName("description")
    val description: String,
    @SerialName("status_code")
    val statusCode: Int,
    @SerialName("tracking_number")
    val trackingNumber: String,
    @SerialName("events")
    val events: List<DeliveryEvent> = emptyList(),
    @SerialName("extra_information")
    val extraInformation: Map<String, String> = emptyMap()
)

@Serializable
data class DeliveryEvent(
    @SerialName("timestamp")
    val timestamp: Long,
    @SerialName("description")
    val description: String,
    @SerialName("location")
    val location: String? = null
)

enum class DeliveryStatus(val code: Int, val displayName: String) {
    COMPLETED(0, "Completed"),
    FROZEN(1, "Frozen"),
    IN_TRANSIT(2, "In Transit"),
    EXPECTING_PICKUP(3, "Expecting Pickup"),
    OUT_FOR_DELIVERY(4, "Out for Delivery"),
    NOT_FOUND(5, "Not Found"),
    FAILED_DELIVERY(6, "Failed Delivery"),
    EXCEPTION(7, "Exception"),
    CARRIER_INFORMED(8, "Carrier Informed");

    companion object {
        fun fromCode(code: Int): DeliveryStatus =
            values().find { it.code == code } ?: EXCEPTION
    }
}