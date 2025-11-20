@file:Suppress("TooGenericExceptionCaught")

package net.readian.parcel.data.network

import kotlinx.serialization.json.Json
import net.readian.parcel.data.model.ApiErrorResponse
import net.readian.parcel.domain.model.RateLimitException
import retrofit2.Response
import timber.log.Timber

object NetworkErrorUtils {
  private const val ERROR_RATE_LIMIT = 429
  fun extractApiErrorMessage(response: Response<*>, json: Json): String? {
    val errorText = try {
      response.errorBody()?.string()
    } catch (e: Exception) {
      Timber.d(e, "Failed to read error body")
      null
    }
    val apiError = try {
      if (!errorText.isNullOrBlank()) {
        json.decodeFromString(
          ApiErrorResponse.serializer(),
          errorText,
        )
      } else {
        null
      }
    } catch (e: Exception) {
      Timber.d(e, "Failed to parse API error response")
      null
    }
    return apiError?.errorMessage ?: response.message()
  }

  suspend fun throwRateLimitIfApplicable(
    response: Response<*>,
    rateLimiter: RateLimiter,
    json: Json,
  ) {
    val message = extractApiErrorMessage(response, json)
    val isRateLimited = response.code() == ERROR_RATE_LIMIT ||
      (message?.contains("rate limit", ignoreCase = true) == true)
    if (isRateLimited) {
      val timeUntilNext = rateLimiter.getTimeUntilNextRequest()
      val remaining = rateLimiter.getRemainingRequests()
      throw RateLimitException(timeUntilNext, remaining)
    }
  }
}
