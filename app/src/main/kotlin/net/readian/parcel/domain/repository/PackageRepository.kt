package net.readian.parcel.domain.repository

import kotlinx.coroutines.flow.Flow
import net.readian.parcel.domain.model.Delivery
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

    suspend fun refreshPackages(): List<Delivery>

    suspend fun getRateLimitInfo(): RateLimitInfo

    /**
     * Validates the provided API key by performing a lightweight request.
     * If valid, persists the key securely for future requests; otherwise clears it.
     */
    suspend fun validateAndSaveApiKey(apiKey: String): Boolean

    /**
     * Clears any persisted API key from secure storage.
     */
    suspend fun clearSavedApiKey()

    /**
     * Observe a single package by tracking number.
     */
    fun getPackage(trackingNumber: String): kotlinx.coroutines.flow.Flow<Delivery?>
}
