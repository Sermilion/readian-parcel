package net.readian.parcel.feature.packagedetail

import net.readian.parcel.core.ui.delivery.model.DeliveryUiModel

object PackageDetailContract {

  sealed interface UiState {
    data object Loading : UiState
    data object NotFound : UiState
    data class Data(
      val delivery: DeliveryUiModel,
      val refreshing: Boolean = false,
      val canRefresh: Boolean = true,
      val cooldownMinutes: Int? = null,
      val lastRefreshError: String? = null,
      val hasOfflineData: Boolean = false,
    ) : UiState
  }
}
