package net.readian.parcel.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.readian.parcel.data.database.dao.PackageDao
import net.readian.parcel.data.database.entity.PackageEntity
import net.readian.parcel.data.model.Delivery
import net.readian.parcel.data.model.DeliveryEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PackageRepository @Inject constructor(
    private val packageDao: PackageDao,
    private val json: Json
) {

    fun getAllPackages(): Flow<List<Delivery>> {
        return packageDao.getAllPackages().map { entities ->
            entities.map { entity -> entity.toDelivery() }
        }
    }

    suspend fun savePackages(deliveries: List<Delivery>) {
        val entities = deliveries.map { delivery -> delivery.toEntity() }
        packageDao.insertPackages(entities)
    }

    suspend fun getLastUpdateTime(): Long? {
        return packageDao.getLastUpdateTime()
    }

    private fun Delivery.toEntity(): PackageEntity {
        return PackageEntity(
            trackingNumber = trackingNumber,
            carrierCode = carrierCode,
            description = description,
            statusCode = statusCode,
            lastUpdated = System.currentTimeMillis(),
            eventsJson = json.encodeToString(events),
            extraInformationJson = json.encodeToString(extraInformation)
        )
    }

    private fun PackageEntity.toDelivery(): Delivery {
        return Delivery(
            trackingNumber = trackingNumber,
            carrierCode = carrierCode,
            description = description,
            statusCode = statusCode,
            events = try { 
                json.decodeFromString<List<DeliveryEvent>>(eventsJson) 
            } catch (e: Exception) { 
                emptyList() 
            },
            extraInformation = try { 
                json.decodeFromString<Map<String, String>>(extraInformationJson) 
            } catch (e: Exception) { 
                emptyMap() 
            }
        )
    }
}