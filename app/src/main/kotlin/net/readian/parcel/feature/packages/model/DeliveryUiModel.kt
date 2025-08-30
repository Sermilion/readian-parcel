package net.readian.parcel.feature.packages.model

/**
 * UI model for delivery presentation
 * Contains UI-specific properties and formatting
 */
data class DeliveryUiModel(
    val trackingNumber: String,
    val carrierCode: String,
    val description: String,
    val statusText: String,
    val statusColor: StatusColor,
    val events: List<DeliveryEventUiModel>,
    val extraInformation: String
)

/**
 * UI model for delivery events
 */
data class DeliveryEventUiModel(
    val timestamp: Long,
    val description: String,
    val location: String?
)

/**
 * UI representation of status colors
 */
enum class StatusColor {
    SUCCESS,      // Green - Delivered
    INFO,         // Blue - In transit, Out for delivery  
    WARNING,      // Orange - Expecting pickup
    ERROR,        // Red - Failed delivery, Exception
    NEUTRAL       // Gray - Other statuses
}