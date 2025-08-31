package net.readian.parcel.data.network

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@OptIn(ExperimentalCoroutinesApi::class)
class RateLimiterTest {

  @get:Rule
  val tmp = TemporaryFolder()

  private fun newDataStore(): DataStore<Preferences> =
    PreferenceDataStoreFactory.create(
      produceFile = { tmp.newFile("rate_limiter.preferences_pb") },
    )

  @Test
  fun initial_state_allows_requests() = runTest {
    val dataStore = newDataStore()
    val limiter = RateLimiter(dataStore)

    assertTrue(limiter.canMakeRequest())
    assertEquals(20, limiter.getRemainingRequests())
  }

  @Test
  fun records_and_blocks_after_limit() = runTest {
    val dataStore = newDataStore()
    val limiter = RateLimiter(dataStore)

    repeat(20) { limiter.recordRequest() }

    assertEquals(0, limiter.getRemainingRequests())
    assertTrue(limiter.getTimeUntilNextRequest() > 0)
    assertEquals(false, limiter.canMakeRequest())
  }

  @Test
  fun persists_state_across_instances() = runTest {
    val dataStore = newDataStore()
    val limiter1 = RateLimiter(dataStore)
    repeat(5) { limiter1.recordRequest() }

    val limiter2 = RateLimiter(dataStore)
    assertEquals(15, limiter2.getRemainingRequests())
  }
}
