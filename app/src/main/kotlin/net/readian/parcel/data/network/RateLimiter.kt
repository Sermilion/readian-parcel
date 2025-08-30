package net.readian.parcel.data.network

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Rate limiter for Parcel API calls (20 requests per hour limit)
 */
@Singleton
class RateLimiter @Inject constructor() {
    
    private val mutex = Mutex()
    private val requestHistory = mutableListOf<Long>()
    
    companion object {
        private const val MAX_REQUESTS_PER_HOUR = 20
        private const val HOUR_IN_MILLIS = 60 * 60 * 1000L
    }
    
    /**
     * Check if we can make a request without exceeding rate limits
     * @return true if request is allowed, false if rate limit would be exceeded
     */
    suspend fun canMakeRequest(): Boolean = mutex.withLock {
        val now = System.currentTimeMillis()
        
        // Remove requests older than 1 hour
        requestHistory.removeAll { it < now - HOUR_IN_MILLIS }
        
        return requestHistory.size < MAX_REQUESTS_PER_HOUR
    }
    
    /**
     * Record a successful API request
     */
    suspend fun recordRequest() = mutex.withLock {
        val now = System.currentTimeMillis()
        requestHistory.add(now)
        
        // Clean up old requests
        requestHistory.removeAll { it < now - HOUR_IN_MILLIS }
    }
    
    /**
     * Get remaining requests in current hour
     */
    suspend fun getRemainingRequests(): Int = mutex.withLock {
        val now = System.currentTimeMillis()
        requestHistory.removeAll { it < now - HOUR_IN_MILLIS }
        return MAX_REQUESTS_PER_HOUR - requestHistory.size
    }
    
    /**
     * Get time until next request is allowed (in milliseconds)
     * Returns 0 if requests are currently allowed
     */
    suspend fun getTimeUntilNextRequest(): Long = mutex.withLock {
        val now = System.currentTimeMillis()
        requestHistory.removeAll { it < now - HOUR_IN_MILLIS }
        
        if (requestHistory.size < MAX_REQUESTS_PER_HOUR) {
            return 0L
        }
        
        // Find the oldest request and calculate when it expires
        val oldestRequest = requestHistory.minOrNull() ?: return 0L
        val expirationTime = oldestRequest + HOUR_IN_MILLIS
        return maxOf(0L, expirationTime - now)
    }
}