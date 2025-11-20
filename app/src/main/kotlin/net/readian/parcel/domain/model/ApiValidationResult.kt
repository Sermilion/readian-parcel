package net.readian.parcel.domain.model

sealed class ApiValidationResult {
  data object Success : ApiValidationResult()
  data object InvalidKey : ApiValidationResult()
  data class RateLimited(val exception: RateLimitException) : ApiValidationResult()
  data object NetworkError : ApiValidationResult()
}
