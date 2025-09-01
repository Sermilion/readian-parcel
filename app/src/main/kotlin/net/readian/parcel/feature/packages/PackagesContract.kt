package net.readian.parcel.feature.packages

import net.readian.parcel.core.ui.delivery.model.DeliveryUiModel
import net.readian.parcel.feature.packages.PackagesContract.PackagesUiState

object PackagesContract {

  sealed class PackagesUiState {
    data object Loading : PackagesUiState()

    data class Content(
      val packages: List<DeliveryUiModel> = emptyList(),
      val refreshing: Boolean = false,
      val canRefresh: Boolean = true,
      val cooldownMinutes: Int? = null,
      val lastRefreshError: String? = null,
      val hasOfflineData: Boolean = false,
    ) : PackagesUiState()
  }

  sealed interface UiFeedbackEvent {
    object NavigateToLogin : UiFeedbackEvent
  }
}

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
