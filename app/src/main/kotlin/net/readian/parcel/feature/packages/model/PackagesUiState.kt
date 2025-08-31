package net.readian.parcel.feature.packages.model

sealed class PackagesUiState {
  data object Loading : PackagesUiState()

  data class Content(
    val packages: List<DeliveryUiModel> = emptyList(),
    val refreshing: Boolean = false,
    val canRefresh: Boolean = true,
  ) : PackagesUiState()
}

internal data class RefreshState(
  val refreshing: Boolean = false,
  val canRefresh: Boolean = true,
  val cooldownSeconds: Int? = null,
)

val PackagesUiState.refreshAllowed: Boolean get() {
  return when (this) {
    is PackagesUiState.Loading -> false
    is PackagesUiState.Content -> this.canRefresh && !this.refreshing
  }
}

val PackagesUiState.isRefreshing: Boolean get() {
  return when (this) {
    is PackagesUiState.Loading -> false
    is PackagesUiState.Content -> this.refreshing
  }
}
