package net.readian.parcel.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorResponse(
  @SerialName("success") val success: Boolean = false,
  @SerialName("error_message") val errorMessage: String? = null,
)
