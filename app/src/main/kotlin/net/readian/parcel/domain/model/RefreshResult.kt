package net.readian.parcel.domain.model

sealed class RefreshResult {
  data class Success(val deliveries: List<Delivery>) : RefreshResult()

  data class Error(
    val cachedData: List<Delivery>,
    val error: Throwable,
  ) : RefreshResult()

  data class RateLimit(
    val cachedData: List<Delivery>,
    val exception: RateLimitException,
  ) : RefreshResult()
}
