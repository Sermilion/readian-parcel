package net.readian.parcel.feature.packagedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.readian.parcel.core.common.ratelimit.RefreshManager
import net.readian.parcel.core.navigation.PackageDetailDestination
import net.readian.parcel.core.ui.delivery.mapper.DeliveryUiMapper
import net.readian.parcel.domain.repository.PackageRepository
import net.readian.parcel.feature.packagedetail.PackageDetailContract.UiState
import javax.inject.Inject

@Suppress("MagicNumber")
@HiltViewModel
class PackageDetailViewModel @Inject constructor(
  repository: PackageRepository,
  private val refreshManager: RefreshManager,
  savedStateHandle: SavedStateHandle,
) : ViewModel() {

  private val args = PackageDetailDestination(
    packageId = requireNotNull(savedStateHandle.get<String>("packageId")),
  )

  val uiState: StateFlow<UiState> = combine(
    repository.getPackage(args.packageId),
    refreshManager.refreshState,
  ) { delivery, refreshState ->
    when {
      delivery != null -> UiState.Data(
        delivery = DeliveryUiMapper.toUiModel(delivery),
        refreshing = refreshState.refreshing,
        canRefresh = refreshState.canRefresh,
        cooldownMinutes = refreshState.cooldownMinutes,
        lastRefreshError = refreshState.lastRefreshError,
        hasOfflineData = refreshState.hasOfflineData,
      )
      else -> UiState.NotFound
    }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = UiState.Loading,
  )

  init {
    viewModelScope.launch {
      refreshManager.syncCooldownFromRepo()
    }
  }

  override fun onCleared() {
    super.onCleared()
    refreshManager.cleanup()
  }

  fun refreshPackage() {
    viewModelScope.launch {
      refreshManager.performRefresh()
    }
  }

  fun onDismissError() {
    refreshManager.resetErrorState()
  }
}
