package net.readian.parcel.data.network

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import java.io.File

class RateLimiterTest :
  FunSpec({
    val tempDir = tempdir()

    fun newDataStore(): DataStore<Preferences> = PreferenceDataStoreFactory.create(
      produceFile = { File(tempDir, "rate_limiter_${System.nanoTime()}.preferences_pb") },
    )

    test("initial state allows requests") {
      runTest {
        val dataStore = newDataStore()
        val limiter = RateLimiter(dataStore)

        limiter.canMakeRequest() shouldBe true
        limiter.getRemainingRequests() shouldBe 20
      }
    }

    test("records and blocks after limit") {
      runTest {
        val dataStore = newDataStore()
        val limiter = RateLimiter(dataStore)

        repeat(20) { limiter.recordRequest() }

        limiter.getRemainingRequests() shouldBe 0
        (limiter.getTimeUntilNextRequest() > 0) shouldBe true
        limiter.canMakeRequest() shouldBe false
      }
    }

    test("persists state across instances") {
      runTest {
        val dataStore = newDataStore()
        val limiter1 = RateLimiter(dataStore)
        repeat(5) { limiter1.recordRequest() }

        val limiter2 = RateLimiter(dataStore)
        limiter2.getRemainingRequests() shouldBe 15
      }
    }
  })
