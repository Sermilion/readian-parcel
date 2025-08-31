package net.readian.parcel.data.model

data class RateLimitInfoDataModel(
  val remainingRequests: Int,
  val timeUntilNextRequestMs: Long,
)
