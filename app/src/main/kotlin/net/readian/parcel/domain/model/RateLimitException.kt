package net.readian.parcel.domain.model

/**
 * Domain-level exception indicating API rate limiting.
 * Data layer can throw this to signal callers in upper layers without
 * leaking data-layer types into presentation.
 */
class RateLimitException(
  val timeUntilNextRequestMillis: Long,
  val remainingRequests: Int = 0,
) : Exception(
  "API rate limit exceeded. Try again in ${timeUntilNextRequestMillis / MILLIS_IN_SECOND} seconds. " +
    "Remaining requests: $remainingRequests",
)

private const val MILLIS_IN_SECOND = 1000
