package net.readian.parcel.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.readian.parcel.data.network.RateLimiter
import net.readian.parcel.data.network.RateLimitException
import net.readian.parcel.data.api.ParcelApiService
import net.readian.parcel.data.database.dao.PackageDao
import net.readian.parcel.data.database.entity.PackageDataModel
import net.readian.parcel.data.model.DeliveryEventResponse
import net.readian.parcel.data.model.RateLimitInfoDataModel
import net.readian.parcel.data.mapper.DeliveryMapper
import net.readian.parcel.data.mapper.StatusMapper
import net.readian.parcel.domain.model.Delivery
import net.readian.parcel.domain.model.FilterMode
import net.readian.parcel.domain.model.RateLimitInfo
import net.readian.parcel.domain.repository.PackageRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReadianPackageRepository @Inject constructor(
    private val packageDao: PackageDao,
    private val parcelApiService: ParcelApiService,
    private val rateLimiter: RateLimiter,
    private val json: Json
) : PackageRepository {

    override fun getAllPackages(): Flow<List<Delivery>> {
        return packageDao.getAllPackages().map { entities ->
            entities.map { entity -> entity.toDomain() }
        }
    }

    override suspend fun savePackages(deliveries: List<Delivery>) {
        val entities = deliveries.map { delivery -> delivery.toEntity() }
        packageDao.insertPackages(entities)
    }

    override suspend fun getLastUpdateTime(): Long? {
        return packageDao.getLastUpdateTime()
    }

    /**
     * Refresh packages from API with rate limiting
     * @param apiKey The user's API key
     * @param filterMode The filter mode for deliveries
     * @throws RateLimitException if rate limit would be exceeded
     */
    override suspend fun refreshPackages(
        apiKey: String,
        filterMode: FilterMode
    ): Result<List<Delivery>> {
        return try {
            // Check rate limit before making request
            if (!rateLimiter.canMakeRequest()) {
                val timeUntilNext = rateLimiter.getTimeUntilNextRequest()
                val remaining = rateLimiter.getRemainingRequests()
                throw RateLimitException(timeUntilNext, remaining)
            }

            // Make API call
            val response = parcelApiService.getDeliveries(apiKey, DeliveryMapper.toData(filterMode))
            
            if (response.isSuccessful) {
                // Record successful request for rate limiting
                rateLimiter.recordRequest()
                
                val parcelResponse = response.body()
                if (parcelResponse?.success == true) {
                    val dataDeliveries = parcelResponse.deliveries
                    val domainDeliveries = dataDeliveries.map { DeliveryMapper.toDomain(it) }
                    
                    // Save to local database
                    savePackages(domainDeliveries)
                    
                    Result.success(domainDeliveries)
                } else {
                    Result.failure(Exception(parcelResponse?.errorMessage ?: "Unknown API error"))
                }
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: RateLimitException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get rate limiting information
     */
    override suspend fun getRateLimitInfo(): RateLimitInfo {
        val dataModel = RateLimitInfoDataModel(
            remainingRequests = rateLimiter.getRemainingRequests(),
            timeUntilNextRequest = rateLimiter.getTimeUntilNextRequest()
        )
        return DeliveryMapper.toDomain(dataModel)
    }

    private fun Delivery.toEntity(): PackageDataModel {
        return PackageDataModel(
            trackingNumber = trackingNumber,
            carrierCode = carrierCode,
            description = description,
            statusCode = StatusMapper.toData(status),
            lastUpdated = System.currentTimeMillis(),
            eventsJson = json.encodeToString(events.map { event ->
                DeliveryEventResponse(
                    event = event.description,
                    date = event.timestamp.toString(),
                    location = event.location,
                    additional = null
                )
            }),
            extraInformationJson = extraInformation
        )
    }

    private fun PackageDataModel.toDomain(): Delivery {
        return Delivery(
            trackingNumber = trackingNumber,
            carrierCode = carrierCode,
            description = description,
            status = StatusMapper.toDomain(statusCode),
            events = try { 
                json.decodeFromString<List<DeliveryEventResponse>>(eventsJson).map { eventResponse ->
                    net.readian.parcel.domain.model.DeliveryEvent(
                        timestamp = DeliveryMapper.parseDateToTimestamp(eventResponse.date),
                        description = eventResponse.event,
                        location = eventResponse.location
                    )
                }
            } catch (e: Exception) { 
                emptyList() 
            },
            extraInformation = extraInformationJson
        )
    }
}