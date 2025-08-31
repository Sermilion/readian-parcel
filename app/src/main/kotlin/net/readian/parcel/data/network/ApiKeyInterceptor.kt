package net.readian.parcel.data.network

import net.readian.parcel.data.repository.ApiKeyRepository
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiKeyInterceptor @Inject constructor(
  private val apiKeyRepository: ApiKeyRepository,
) : Interceptor {

  @Suppress("MagicNumber")
  override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request()

    val apiKeyRaw = apiKeyRepository.getApiKey()
    val apiKey = apiKeyRaw
      ?.filter { it.code in 32..126 }
      ?.trim()

    return if (!apiKey.isNullOrBlank()) {
      val authenticatedRequest = request.newBuilder()
        .addHeader("api-key", apiKey)
        .build()
      chain.proceed(authenticatedRequest)
    } else {
      chain.proceed(request)
    }
  }
}
