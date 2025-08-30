package net.readian.parcel.domain.repository

/**
 * Domain repository interface for API key operations
 * This interface defines the contract for API key storage without
 * exposing implementation details from the data layer
 */
interface ApiKeyRepository {
    
    fun getApiKey(): String?
    
    fun setApiKey(apiKey: String)
    
    fun clearApiKey()
    
    fun hasApiKey(): Boolean
}