package net.readian.parcel.data.model

/**
 * Information about current API rate limiting status
 */
data class RateLimitInfoDataModel(
    val remainingRequests: Int,
    val timeUntilNextRequest: Long // in milliseconds
) {
    val canMakeRequest: Boolean
        get() = remainingRequests > 0 && timeUntilNextRequest == 0L
        
    val timeUntilNextRequestSeconds: Long
        get() = timeUntilNextRequest / 1000
}