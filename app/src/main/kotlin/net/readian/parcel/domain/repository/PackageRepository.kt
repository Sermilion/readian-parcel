package net.readian.parcel.domain.repository

import kotlinx.coroutines.flow.Flow
import net.readian.parcel.domain.model.Delivery
import net.readian.parcel.domain.model.FilterMode
import net.readian.parcel.domain.model.RateLimitInfo

/**
 * Domain repository interface for package operations
 * This interface defines the contract for package data access without
 * exposing implementation details from the data layer
 */
interface PackageRepository {
    
    fun getAllPackages(): Flow<List<Delivery>>
    
    suspend fun savePackages(deliveries: List<Delivery>)
    
    suspend fun getLastUpdateTime(): Long?
    
    suspend fun refreshPackages(
        apiKey: String,
        filterMode: FilterMode = FilterMode.RECENT
    ): Result<List<Delivery>>
    
    suspend fun getRateLimitInfo(): RateLimitInfo
}