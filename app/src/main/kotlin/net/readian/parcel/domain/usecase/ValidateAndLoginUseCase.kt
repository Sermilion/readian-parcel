package net.readian.parcel.domain.usecase

import net.readian.parcel.domain.datastore.UserDataStore
import net.readian.parcel.domain.model.ApiValidationResult
import net.readian.parcel.domain.repository.PackageRepository
import javax.inject.Inject

class ValidateAndLoginUseCase @Inject constructor(
  private val packageRepository: PackageRepository,
  private val userDataStore: UserDataStore,
) {
  suspend operator fun invoke(apiKey: String): ApiValidationResult {
    val result = packageRepository.validateAndSaveApiKey(apiKey)
    return when (result) {
      is ApiValidationResult.Success -> {
        userDataStore.setLoggedIn(true)
        result
      }
      is ApiValidationResult.InvalidKey -> {
        userDataStore.logout()
        result
      }
      is ApiValidationResult.RateLimited -> result
      is ApiValidationResult.NetworkError -> {
        userDataStore.logout()
        result
      }
    }
  }
}
