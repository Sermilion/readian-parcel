package net.readian.parcel.data.repository

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import net.readian.parcel.core.common.DispatcherProvider
import net.readian.parcel.data.api.ParcelApiService
import net.readian.parcel.data.database.dao.PackageDao
import net.readian.parcel.data.database.entity.DeliveryEventDataModel
import net.readian.parcel.data.database.entity.DeliveryStatusDataModel
import net.readian.parcel.data.database.entity.PackageDataModel
import net.readian.parcel.data.database.model.PackageWithEvents
import net.readian.parcel.data.model.DeliveryEventResponse
import net.readian.parcel.data.model.DeliveryResponse
import net.readian.parcel.data.model.ParcelResponse
import net.readian.parcel.data.network.RateLimiter
import net.readian.parcel.domain.model.ApiValidationResult
import net.readian.parcel.domain.model.Delivery
import net.readian.parcel.domain.model.DeliveryStatus
import net.readian.parcel.domain.model.RefreshResult
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class ReadianPackageRepositoryTest : FunSpec() {
  private val testDispatcher = UnconfinedTestDispatcher()

  @MockK
  private lateinit var dispatcherProvider: DispatcherProvider

  @MockK
  private lateinit var packageDao: PackageDao

  @MockK
  private lateinit var parcelApiService: ParcelApiService

  @MockK
  private lateinit var rateLimiter: RateLimiter

  @MockK
  private lateinit var apiKeyRepository: ApiKeyRepository

  private val json: Json = Json

  private lateinit var repository: ReadianPackageRepository

  init {
    beforeTest {
      MockKAnnotations.init(this@ReadianPackageRepositoryTest)
      every { dispatcherProvider.io() } returns testDispatcher
      repository = ReadianPackageRepository(
        packageDao,
        parcelApiService,
        rateLimiter,
        json,
        apiKeyRepository,
        dispatcherProvider,
      )
    }

    test("getAllPackages should return packages from database") {
      runTest {
        val mockPackageWithEvents = PackageWithEvents(
          pkg = PackageDataModel(
            trackingNumber = "123",
            carrierCode = "ups",
            description = "Test Package",
            statusCode = DeliveryStatusDataModel.IN_TRANSIT,
            lastUpdated = System.currentTimeMillis(),
            extraInformation = "",
            expectedAt = null,
            expectedEndAt = null,
            expectedDateRaw = null,
            expectedEndDateRaw = null,
          ),
          events = emptyList(),
        )

        every { packageDao.observeAllPackagesWithEvents() } returns flowOf(listOf(mockPackageWithEvents))

        val result = repository.getAllPackages().first()

        result.size shouldBe 1
        result[0].trackingNumber shouldBe "123"
        result[0].carrierCode shouldBe "ups"
      }
    }

    test("savePackages should save to database") {
      runTest {
        val deliveries = listOf(
          Delivery(
            trackingNumber = "123",
            carrierCode = "ups",
            description = "Test Package",
            status = DeliveryStatus.IN_TRANSIT,
            events = emptyList(),
            extraInformation = "",
          ),
        )

        val packagesSlot = slot<List<PackageDataModel>>()
        val eventsSlot = slot<List<DeliveryEventDataModel>>()

        coEvery {
          packageDao.savePackagesWithEvents(
            capture(packagesSlot),
            capture(eventsSlot),
          )
        } returns Unit

        repository.savePackages(deliveries)

        coVerify {
          packageDao.savePackagesWithEvents(any(), any())
        }

        packagesSlot.captured.size shouldBe 1
        packagesSlot.captured[0].trackingNumber shouldBe "123"
      }
    }

    test("refreshPackages with rate limit should return RateLimit result") {
      runTest {
        val cachedData = listOf(
          PackageWithEvents(
            pkg = PackageDataModel(
              trackingNumber = "cached",
              carrierCode = "ups",
              description = "Cached Package",
              statusCode = DeliveryStatusDataModel.IN_TRANSIT,
              lastUpdated = System.currentTimeMillis(),
              extraInformation = "",
              expectedAt = null,
              expectedEndAt = null,
              expectedDateRaw = null,
              expectedEndDateRaw = null,
            ),
            events = emptyList(),
          ),
        )

        coEvery { rateLimiter.canMakeRequest() } returns false
        coEvery { rateLimiter.getTimeUntilNextRequest() } returns 60000L
        coEvery { rateLimiter.getRemainingRequests() } returns 0
        coEvery { packageDao.getAllPackagesWithEvents() } returns cachedData

        val result = repository.refreshPackages()

        result.shouldBeTypeOf<RefreshResult.RateLimit>()
        result.cachedData.size shouldBe 1
        result.exception.timeUntilNextRequestMillis shouldBe 60000L
      }
    }

    test("refreshPackages with successful response should return Success") {
      runTest {
        val deliveryResponse = DeliveryResponse(
          trackingNumber = "123",
          carrierCode = "ups",
          description = "Test Package",
          statusCode = 2,
          events = listOf(
            DeliveryEventResponse(
              event = "Package in transit",
              date = "2024-01-01",
              location = "New York",
            ),
          ),
          extraInformation = "",
        )

        val parcelResponse = ParcelResponse(
          success = true,
          deliveries = listOf(deliveryResponse),
        )

        coEvery { rateLimiter.canMakeRequest() } returns true
        coEvery { parcelApiService.getDeliveries() } returns Response.success(parcelResponse)
        coEvery { rateLimiter.recordRequest() } returns Unit
        coEvery { packageDao.getAllPackagesWithEvents() } returns emptyList()
        coEvery { packageDao.replaceAllPackages(any(), any()) } returns Unit

        val result = repository.refreshPackages()

        result.shouldBeTypeOf<RefreshResult.Success>()
        result.deliveries.size shouldBe 1
        result.deliveries[0].trackingNumber shouldBe "123"
      }
    }

    test("refreshPackages with network error should return Error result") {
      runTest {
        val cachedData = listOf(
          PackageWithEvents(
            pkg = PackageDataModel(
              trackingNumber = "cached",
              carrierCode = "ups",
              description = "Cached Package",
              statusCode = DeliveryStatusDataModel.IN_TRANSIT,
              lastUpdated = System.currentTimeMillis(),
              extraInformation = "",
              expectedAt = null,
              expectedEndAt = null,
              expectedDateRaw = null,
              expectedEndDateRaw = null,
            ),
            events = emptyList(),
          ),
        )

        coEvery { rateLimiter.canMakeRequest() } returns true
        coEvery { parcelApiService.getDeliveries() } returns Response.error(500, "Server error".toResponseBody())
        coEvery { rateLimiter.recordRequest() } returns Unit
        coEvery { packageDao.getAllPackagesWithEvents() } returns cachedData

        val result = repository.refreshPackages()

        result.shouldBeTypeOf<RefreshResult.Error>()
        result.cachedData.size shouldBe 1
      }
    }

    test("validateAndSaveApiKey with valid key should return Success") {
      runTest {
        val parcelResponse = ParcelResponse(
          success = true,
          deliveries = emptyList(),
        )

        coEvery { rateLimiter.canMakeRequest() } returns true
        coEvery { apiKeyRepository.setApiKey("valid-key") } returns Unit
        coEvery { parcelApiService.getDeliveries() } returns Response.success(parcelResponse)
        coEvery { rateLimiter.recordRequest() } returns Unit

        val result = repository.validateAndSaveApiKey("valid-key")

        result shouldBe ApiValidationResult.Success
      }
    }

    test("validateAndSaveApiKey with invalid key should return InvalidKey") {
      runTest {
        val parcelResponse = ParcelResponse(
          success = false,
          deliveries = emptyList(),
          errorMessage = "Invalid API key",
        )

        coEvery { rateLimiter.canMakeRequest() } returns true
        coEvery { apiKeyRepository.setApiKey("invalid-key") } returns Unit
        coEvery { parcelApiService.getDeliveries() } returns Response.success(parcelResponse)
        coEvery { rateLimiter.recordRequest() } returns Unit
        coEvery { apiKeyRepository.clearApiKey() } returns Unit

        val result = repository.validateAndSaveApiKey("invalid-key")

        result shouldBe ApiValidationResult.InvalidKey
      }
    }

    test("validateAndSaveApiKey with rate limit should return RateLimited") {
      runTest {
        coEvery { rateLimiter.canMakeRequest() } returns false
        coEvery { rateLimiter.getTimeUntilNextRequest() } returns 60000L
        coEvery { rateLimiter.getRemainingRequests() } returns 0

        val result = repository.validateAndSaveApiKey("key")

        result.shouldBeTypeOf<ApiValidationResult.RateLimited>()
        result.exception.timeUntilNextRequestMillis shouldBe 60000L
      }
    }

    test("getPackage should return package from database") {
      runTest {
        val mockPackageWithEvents = PackageWithEvents(
          pkg = PackageDataModel(
            trackingNumber = "123",
            carrierCode = "ups",
            description = "Test Package",
            statusCode = DeliveryStatusDataModel.IN_TRANSIT,
            lastUpdated = System.currentTimeMillis(),
            extraInformation = "",
            expectedAt = null,
            expectedEndAt = null,
            expectedDateRaw = null,
            expectedEndDateRaw = null,
          ),
          events = emptyList(),
        )

        every { packageDao.observePackageWithEvents("123") } returns flowOf(mockPackageWithEvents)

        val result = repository.getPackage("123").first()

        result?.trackingNumber shouldBe "123"
        result?.carrierCode shouldBe "ups"
      }
    }

    test("getPackage with non-existent package should return null") {
      runTest {
        every { packageDao.observePackageWithEvents("999") } returns flowOf(null)

        val result = repository.getPackage("999").first()

        result shouldBe null
      }
    }
  }
}
