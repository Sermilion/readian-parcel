package net.readian.parcel.domain.model

data class RateLimitInfo(
  val remainingRequests: Int,
  val timeUntilNextRequestMs: Long,
)
