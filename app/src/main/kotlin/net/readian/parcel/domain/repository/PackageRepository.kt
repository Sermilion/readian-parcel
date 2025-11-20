package net.readian.parcel.domain.repository

import kotlinx.coroutines.flow.Flow
import net.readian.parcel.domain.model.ApiValidationResult
import net.readian.parcel.domain.model.Delivery
import net.readian.parcel.domain.model.RateLimitInfo
import net.readian.parcel.domain.model.RefreshResult

interface PackageRepository {

  fun getAllPackages(): Flow<List<Delivery>>

  suspend fun savePackages(deliveries: List<Delivery>)

  suspend fun getLastUpdateTime(): Long?

  suspend fun refreshPackages(): RefreshResult

  suspend fun getRateLimitInfo(): RateLimitInfo

  suspend fun validateAndSaveApiKey(apiKey: String): ApiValidationResult

  suspend fun clearSavedApiKey()

  fun getPackage(trackingNumber: String): Flow<Delivery?>
}
