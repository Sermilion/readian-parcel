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
  val deliveries: List<DeliveryResponse> = emptyList(),
)

@Serializable
data class DeliveryResponse(
  @SerialName("carrier_code")
  val carrierCode: String,
  @SerialName("description")
  val description: String,
  @SerialName("status_code")
  val statusCode: Int,
  @SerialName("tracking_number")
  val trackingNumber: String,
  @SerialName("events")
  val events: List<DeliveryEventResponse> = emptyList(),
  @SerialName("extra_information")
  val extraInformation: String = "",
  @SerialName("timestamp_expected")
  val timestampExpected: Long? = null,
  @SerialName("timestamp_expected_end")
  val timestampExpectedEnd: Long? = null,
  @SerialName("date_expected")
  val dateExpected: String? = null,
  @SerialName("date_expected_end")
  val dateExpectedEnd: String? = null,
)

@Serializable
data class DeliveryEventResponse(
  @SerialName("event")
  val event: String,
  @SerialName("date")
  val date: String,
  @SerialName("location")
  val location: String? = null,
  @SerialName("additional")
  val additional: String? = null,
)

@Suppress("MagicNumber")
enum class DeliveryStatusResponse(val code: Int) {
  COMPLETED(0),
  FROZEN(1),
  IN_TRANSIT(2),
  EXPECTING_PICKUP(3),
  OUT_FOR_DELIVERY(4),
  NOT_FOUND(5),
  FAILED_DELIVERY(6),
  EXCEPTION(7),
  CARRIER_INFORMED(8),
  ;

  companion object Companion {
    fun fromCode(code: Int): DeliveryStatusResponse =
      DeliveryStatusResponse.entries.find { it.code == code } ?: EXCEPTION
  }
}
