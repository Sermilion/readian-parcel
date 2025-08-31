package net.readian.parcel.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
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
import net.readian.parcel.domain.model.Delivery
import net.readian.parcel.domain.model.RateLimitException
import net.readian.parcel.domain.model.RateLimitInfo
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
) : PackageRepository {

    override fun getAllPackages(): Flow<List<Delivery>> {
        return packageDao.observeAllPackagesWithEvents().distinctUntilChanged().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun savePackages(deliveries: List<Delivery>) {
        val entities = deliveries.map { delivery -> delivery.toEntity() }
        packageDao.insertPackages(entities)
        // Insert normalized events; cascading FK cleans up previous ones on REPLACE
        val eventsToInsert = mutableListOf<DeliveryEventDataModel>()
        deliveries.forEach { d ->
            d.events.forEach { e ->
                eventsToInsert += DeliveryEventDataModel(
                    trackingNumber = d.trackingNumber,
                    description = e.description,
                    rawDate = e.rawDate,
                    location = e.location,
                    additional = null,
                    timestamp = e.timestamp,
                )
            }
        }
        if (eventsToInsert.isNotEmpty()) packageDao.insertEvents(eventsToInsert)
    }

    override suspend fun getLastUpdateTime(): Long? {
        return packageDao.getLastUpdateTime()
    }

    override suspend fun refreshPackages(): List<Delivery> {
        return try {
            if (!rateLimiter.canMakeRequest()) {
                val timeUntilNext = rateLimiter.getTimeUntilNextRequest()
                val remaining = rateLimiter.getRemainingRequests()
                throw RateLimitException(timeUntilNext, remaining)
            }

            val response = parcelApiService.getDeliveries()
            rateLimiter.recordRequest()

            val result = if (response.isSuccessful) {
                val parcelResponse = response.body()
                if (parcelResponse?.success == true) {
                    val dataDeliveries = parcelResponse.deliveries
                    val domainDeliveries = dataDeliveries.map { DeliveryMapper.toDomain(it) }
                    packageDao.clearAllPackages()
                    savePackages(domainDeliveries)
                    domainDeliveries
                } else {
                    Timber.e("API error: ${parcelResponse?.errorMessage}")
                    emptyList()
                }
            } else {
                NetworkErrorUtils.throwRateLimitIfApplicable(
                    response = response,
                    rateLimiter = rateLimiter,
                    json = json,
                )
                val message = NetworkErrorUtils.extractApiErrorMessage(response, json)
                Timber.e("API error: $message")
                emptyList()
            }
            result
        } catch (e: IOException) {
            Timber.e(e, "Network error while refreshing packages.")
            emptyList()
        } catch (e: HttpException) {
            Timber.e(e, "HTTP error while refreshing packages.")
            emptyList()
        }
    }

    /**
     * Get rate limiting information
     */
    override suspend fun getRateLimitInfo(): RateLimitInfo {
        val dataModel = RateLimitInfoDataModel(
            remainingRequests = rateLimiter.getRemainingRequests(),
            timeUntilNextRequestMs = rateLimiter.getTimeUntilNextRequest(),
        )
        return DeliveryMapper.toDomain(dataModel)
    }

    override fun getPackage(trackingNumber: String): Flow<Delivery?> {
        return packageDao.observePackageWithEvents(trackingNumber).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun validateAndSaveApiKey(apiKey: String): Boolean {
        return try {
            if (!rateLimiter.canMakeRequest()) {
                val timeUntilNext = rateLimiter.getTimeUntilNextRequest()
                val remaining = rateLimiter.getRemainingRequests()
                throw RateLimitException(timeUntilNext, remaining)
            }

            apiKeyRepository.setApiKey(apiKey)

            val response = parcelApiService.getDeliveries()
            rateLimiter.recordRequest()
            val ok = response.isSuccessful && response.body()?.success == true
            if (!ok) {
                if (!response.isSuccessful) {
                    NetworkErrorUtils.throwRateLimitIfApplicable(
                        response = response,
                        rateLimiter = rateLimiter,
                        json = json,
                    )
                }
                apiKeyRepository.clearApiKey()
            }
            ok
        } catch (e: RateLimitException) {
            throw e
        } catch (e: IOException) {
            Timber.e(e, "Network error validating API key")
            apiKeyRepository.clearApiKey()
            false
        } catch (e: HttpException) {
            Timber.e(e, "HTTP error validating API key")
            apiKeyRepository.clearApiKey()
            false
        }
    }

    override suspend fun clearSavedApiKey() {
        apiKeyRepository.clearApiKey()
    }

    private fun Delivery.toEntity(): PackageDataModel {
        return PackageDataModel(
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
        )
    }

    private fun PackageWithEvents.toDomain(): Delivery {
        return Delivery(
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
}
