package net.readian.parcel.data.network

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Rate limiter for Parcel API calls (20 requests per hour limit),
 * persisted with DataStore so state survives process death.
 */
@Singleton
class RateLimiter @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    private val mutex = Mutex()

    private val keyTimestamps = stringSetPreferencesKey("rate_limiter_request_timestamps")

    companion object {
        private const val MAX_REQUESTS_PER_HOUR = 20
        private const val HOUR_IN_MILLIS = 60 * 60 * 1000L
    }

    private suspend fun loadPrunedTimestamps(now: Long): MutableList<Long> {
        val prefs = dataStore.data.first()
        val raw = prefs[keyTimestamps].orEmpty()
        val list = raw.mapNotNull { it.toLongOrNull() }.toMutableList()
        list.removeAll { it < now - HOUR_IN_MILLIS }
        return list
    }

    private suspend fun saveTimestamps(list: List<Long>) {
        val set = list.map { it.toString() }.toSet()
        dataStore.edit { prefs ->
            prefs[keyTimestamps] = set
        }
    }

    suspend fun canMakeRequest(): Boolean = mutex.withLock {
        val now = System.currentTimeMillis()
        val list = loadPrunedTimestamps(now)
        list.size < MAX_REQUESTS_PER_HOUR
    }

    suspend fun recordRequest() = mutex.withLock {
        val now = System.currentTimeMillis()
        val list = loadPrunedTimestamps(now)
        var unique = now
        // Ensure uniqueness to avoid Set dedup when multiple calls happen within the
        // same millisecond
        val existing = list.toMutableSet()
        while (existing.contains(unique)) unique += 1
        list.add(unique)
        saveTimestamps(list)
    }

    suspend fun getRemainingRequests(): Int = mutex.withLock {
        val now = System.currentTimeMillis()
        val list = loadPrunedTimestamps(now)
        MAX_REQUESTS_PER_HOUR - list.size
    }

    suspend fun getTimeUntilNextRequest(): Long = mutex.withLock {
        val now = System.currentTimeMillis()
        val list = loadPrunedTimestamps(now)
        if (list.size < MAX_REQUESTS_PER_HOUR) return 0L
        val oldest = list.minOrNull() ?: return 0L
        val expiration = oldest + HOUR_IN_MILLIS
        maxOf(0L, expiration - now)
    }
}
