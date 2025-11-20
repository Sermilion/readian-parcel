package net.readian.parcel.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import net.readian.parcel.core.common.DispatcherProvider
import net.readian.parcel.data.api.ParcelApiService
import net.readian.parcel.data.database.dao.PackageDao
import net.readian.parcel.data.database.entity.DeliveryEventDataModel
import net.readian.parcel.data.database.entity.PackageDataModel
import net.readian.parcel.data.database.model.PackageWithEvents
import net.readian.parcel.data.mapper.DeliveryMapper
import net.readian.parcel.data.mapper.StatusMapper
import net.readian.parcel.data.model.RateLimitInfoDataModel
import net.readian.parcel.data.network.NetworkErrorUtils
import net.readian.parcel.data.network.RateLimiter
import net.readian.parcel.domain.model.ApiValidationResult
import net.readian.parcel.domain.model.Delivery
import net.readian.parcel.domain.model.RateLimitException
import net.readian.parcel.domain.model.RateLimitInfo
import net.readian.parcel.domain.model.RefreshResult
import net.readian.parcel.domain.repository.PackageRepository
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReadianPackageRepository @Inject constructor(
  private val packageDao: PackageDao,
  private val parcelApiService: ParcelApiService,
  private val rateLimiter: RateLimiter,
  private val json: Json,
  private val apiKeyRepository: ApiKeyRepository,
  private val dispatcherProvider: DispatcherProvider,
) : PackageRepository {

  override fun getAllPackages(): Flow<List<Delivery>> = packageDao.observeAllPackagesWithEvents()
    .distinctUntilChanged()
    .map { list -> list.map { it.toDomain() } }

  override suspend fun savePackages(deliveries: List<Delivery>) = withContext(dispatcherProvider.io()) {
    val (entities, events) = convertDeliveriesToEntities(deliveries)
    packageDao.savePackagesWithEvents(entities, events)
  }

  override suspend fun getLastUpdateTime(): Long? = withContext(dispatcherProvider.io()) {
    packageDao.getLastUpdateTime()
  }

  @Suppress("ReturnCount")
  override suspend fun refreshPackages(): RefreshResult = withContext(dispatcherProvider.io()) {
    val cachedData = packageDao.getAllPackagesWithEvents().map { it.toDomain() }
    return@withContext try {
      if (!rateLimiter.canMakeRequest()) {
        val timeUntilNext = rateLimiter.getTimeUntilNextRequest()
        val remaining = rateLimiter.getRemainingRequests()
        val exception = RateLimitException(timeUntilNext, remaining)
        return@withContext RefreshResult.RateLimit(cachedData, exception)
      }

      val response = parcelApiService.getDeliveries()
      rateLimiter.recordRequest()

      if (response.isSuccessful) {
        val parcelResponse = response.body()
        if (parcelResponse?.success == true) {
          val dataDeliveries = parcelResponse.deliveries
          val domainDeliveries = dataDeliveries.map { DeliveryMapper.toDomain(it) }
          val (entities, events) = convertDeliveriesToEntities(domainDeliveries)

          packageDao.replaceAllPackages(entities, events)
          RefreshResult.Success(domainDeliveries)
        } else {
          val error = Exception("API error: ${parcelResponse?.errorMessage}")
          RefreshResult.Error(cachedData, error)
        }
      } else {
        try {
          NetworkErrorUtils.throwRateLimitIfApplicable(
            response = response,
            rateLimiter = rateLimiter,
            json = json,
          )
        } catch (e: RateLimitException) {
          return@withContext RefreshResult.RateLimit(cachedData, e)
        }
        val message = NetworkErrorUtils.extractApiErrorMessage(response, json)
        val error = Exception("API error: $message")
        RefreshResult.Error(cachedData, error)
      }
    } catch (e: RateLimitException) {
      RefreshResult.RateLimit(cachedData, e)
    } catch (e: IOException) {
      Timber.e(e, "Network error while refreshing packages.")
      RefreshResult.Error(cachedData, e)
    } catch (e: HttpException) {
      Timber.e(e, "HTTP error while refreshing packages.")
      RefreshResult.Error(cachedData, e)
    }
  }

  /**
   * Get rate limiting information
   */
  override suspend fun getRateLimitInfo(): RateLimitInfo = withContext(dispatcherProvider.io()) {
    val dataModel = RateLimitInfoDataModel(
      remainingRequests = rateLimiter.getRemainingRequests(),
      timeUntilNextRequestMs = rateLimiter.getTimeUntilNextRequest(),
    )
    DeliveryMapper.toDomain(dataModel)
  }

  override fun getPackage(trackingNumber: String): Flow<Delivery?> = packageDao.observePackageWithEvents(trackingNumber)
    .map { entity -> entity?.toDomain() }

  override suspend fun validateAndSaveApiKey(apiKey: String): ApiValidationResult = withContext(dispatcherProvider.io()) {
    return@withContext try {
      if (!rateLimiter.canMakeRequest()) {
        val timeUntilNext = rateLimiter.getTimeUntilNextRequest()
        val remaining = rateLimiter.getRemainingRequests()
        val exception = RateLimitException(timeUntilNext, remaining)
        return@withContext ApiValidationResult.RateLimited(exception)
      }

      apiKeyRepository.setApiKey(apiKey)

      val response = parcelApiService.getDeliveries()
      rateLimiter.recordRequest()
      val ok = response.isSuccessful && response.body()?.success == true
      if (ok) {
        ApiValidationResult.Success
      } else {
        if (!response.isSuccessful) {
          try {
            NetworkErrorUtils.throwRateLimitIfApplicable(
              response = response,
              rateLimiter = rateLimiter,
              json = json,
            )
          } catch (e: RateLimitException) {
            return@withContext ApiValidationResult.RateLimited(e)
          }
        }
        apiKeyRepository.clearApiKey()
        ApiValidationResult.InvalidKey
      }
    } catch (e: RateLimitException) {
      ApiValidationResult.RateLimited(e)
    } catch (e: IOException) {
      Timber.e(e, "Network error validating API key")
      apiKeyRepository.clearApiKey()
      ApiValidationResult.NetworkError
    } catch (e: HttpException) {
      Timber.e(e, "HTTP error validating API key")
      apiKeyRepository.clearApiKey()
      ApiValidationResult.NetworkError
    }
  }

  override suspend fun clearSavedApiKey() {
    apiKeyRepository.clearApiKey()
  }

  private fun Delivery.toEntity(): PackageDataModel = PackageDataModel(
    trackingNumber = trackingNumber,
    carrierCode = carrierCode,
    description = description,
    statusCode = StatusMapper.toData(status),
    lastUpdated = System.currentTimeMillis(),
    extraInformation = extraInformation,
    expectedAt = expectedAt,
    expectedEndAt = expectedEndAt,
    expectedDateRaw = expectedDateRaw,
    expectedEndDateRaw = expectedEndDateRaw,
    lastNotifiedStatus = null, // Will be set when notification is sent
  )

  private fun convertDeliveriesToEntities(
    deliveries: List<Delivery>,
  ): Pair<List<PackageDataModel>, List<DeliveryEventDataModel>> {
    val entities = deliveries.map { it.toEntity() }
    val events = mutableListOf<DeliveryEventDataModel>()
    deliveries.forEach { delivery ->
      delivery.events.forEach { event ->
        events += DeliveryEventDataModel(
          trackingNumber = delivery.trackingNumber,
          description = event.description,
          rawDate = event.rawDate,
          location = event.location,
          additional = null,
          timestamp = event.timestamp,
        )
      }
    }
    return Pair(entities, events)
  }

  private fun PackageWithEvents.toDomain(): Delivery = Delivery(
    trackingNumber = pkg.trackingNumber,
    carrierCode = pkg.carrierCode,
    description = pkg.description,
    status = StatusMapper.toDomain(pkg.statusCode),
    events = events.map { event ->
      net.readian.parcel.domain.model.DeliveryEvent(
        timestamp = event.timestamp,
        description = event.description,
        location = event.location,
        rawDate = event.rawDate,
      )
    },
    extraInformation = pkg.extraInformation,
    expectedAt = pkg.expectedAt,
    expectedEndAt = pkg.expectedEndAt,
    expectedDateRaw = pkg.expectedDateRaw,
    expectedEndDateRaw = pkg.expectedEndDateRaw,
  )
}
