package net.readian.parcel.core.common.ratelimit

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.readian.parcel.core.common.di.qualifiers.ApplicationScope
import net.readian.parcel.domain.model.RefreshResult
import net.readian.parcel.domain.repository.PackageRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("MagicNumber")
@Singleton
class RefreshManager @Inject constructor(
  private val packageRepository: PackageRepository,
  @param:ApplicationScope val scope: CoroutineScope,
) {
  private val refreshCooldownMs = 3 * 60 * 1000L
  private var cooldownJob: Job? = null

  private val _refreshState = MutableStateFlow(RefreshState())
  val refreshState = _refreshState.asStateFlow()

  suspend fun performRefresh() {
    try {
      _refreshState.update { it.copy(refreshing = true, lastRefreshError = null) }
      when (val result = packageRepository.refreshPackages()) {
        is RefreshResult.Success -> {
          startCooldownTimer(refreshCooldownMs)
          _refreshState.update { it.copy(hasOfflineData = false) }
        }

        is RefreshResult.Error -> {
          val errorMessage = result.error.message ?: "Unknown error occurred"
          Timber.w(result.error, "Refresh failed: $errorMessage")
          _refreshState.update {
            it.copy(
              lastRefreshError = errorMessage,
              hasOfflineData = result.cachedData.isNotEmpty(),
            )
          }
          startCooldownTimer(refreshCooldownMs)
        }

        is RefreshResult.RateLimit -> {
          Timber.w(result.exception, "Rate limit exceeded.")
          _refreshState.update {
            it.copy(hasOfflineData = result.cachedData.isNotEmpty())
          }
          startCooldownTimer(result.exception.timeUntilNextRequestMillis)
        }
      }
    } finally {
      _refreshState.update { it.copy(refreshing = false) }
    }
  }

  private fun startCooldownTimer(initialMillis: Long) {
    cooldownJob?.cancel()
    cooldownJob = scope.launch {
      var remaining = (initialMillis / 1000).toInt()
      val remainingMinutes = remaining.toMinutes()
      _refreshState.update {
        it.copy(canRefresh = false, cooldownMinutes = remainingMinutes)
      }
      while (remaining > 0) {
        delay(1000)
        remaining -= 1
        val newMinutes = remaining.toMinutes()
        if (newMinutes != _refreshState.value.cooldownMinutes) {
          _refreshState.update { it.copy(cooldownMinutes = newMinutes) }
        }
      }

      _refreshState.update {
        it.copy(canRefresh = true, cooldownMinutes = null, lastRefreshError = null)
      }
    }
  }

  suspend fun syncCooldownFromRepo() {
    val info = packageRepository.getRateLimitInfo()
    if (info.timeUntilNextRequestMs > 0) {
      startCooldownTimer(info.timeUntilNextRequestMs)
      _refreshState.update { it.copy(refreshing = false) }
    }
  }

  fun resetErrorState() {
    _refreshState.update { it.copy(lastRefreshError = null) }
  }

  fun cleanup() {
    cooldownJob?.cancel()
    cooldownJob = null

    _refreshState.value = RefreshState()
  }

  private fun Int?.toMinutes(): Int? = this?.let { seconds -> (seconds + 59) / 60 }
}
