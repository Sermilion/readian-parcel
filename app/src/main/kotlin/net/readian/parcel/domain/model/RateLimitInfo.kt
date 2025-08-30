package net.readian.parcel.domain.model

/**
 * Domain model for rate limiting information
 */
data class RateLimitInfo(
    val remainingRequests: Int,
    val timeUntilNextRequest: Long // in milliseconds
) {
    val canMakeRequest: Boolean
        get() = remainingRequests > 0 && timeUntilNextRequest == 0L
        
    val timeUntilNextRequestSeconds: Long
        get() = timeUntilNextRequest / 1000
}