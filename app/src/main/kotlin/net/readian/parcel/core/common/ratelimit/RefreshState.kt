package net.readian.parcel.core.common.ratelimit

data class RefreshState(
  val refreshing: Boolean = false,
  val canRefresh: Boolean = true,
  val cooldownMinutes: Int? = null,
  val lastRefreshError: String? = null,
  val hasOfflineData: Boolean = false,
)
