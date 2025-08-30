package net.readian.parcel.data.network

/**
 * Exception thrown when API rate limit would be exceeded
 */
class RateLimitException(
    val timeUntilNextRequestMillis: Long,
    val remainingRequests: Int = 0
) : Exception("API rate limit exceeded. Try again in ${timeUntilNextRequestMillis / 1000} seconds. Remaining requests: $remainingRequests")