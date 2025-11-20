package net.readian.parcel.feature.packages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.readian.parcel.core.common.ratelimit.RefreshManager
import net.readian.parcel.core.ui.delivery.mapper.DeliveryUiMapper
import net.readian.parcel.domain.repository.CarrierRepository
import net.readian.parcel.domain.repository.PackageRepository
import net.readian.parcel.domain.usecase.LogoutUseCase
import net.readian.parcel.feature.packages.PackagesContract.PackagesUiState
import net.readian.parcel.feature.packages.PackagesContract.UiFeedbackEvent
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@Suppress("MagicNumber")
@HiltViewModel
class PackagesViewModel @Inject constructor(
  packageRepository: PackageRepository,
  private val carrierRepository: CarrierRepository,
  private val refreshManager: RefreshManager,
  private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

  private val _uiFeedbackEvents = Channel<UiFeedbackEvent>()
  val uiFeedbackEvents = _uiFeedbackEvents.receiveAsFlow()

  val uiState = combine(
    packageRepository.getAllPackages(),
    carrierRepository.carriers,
    refreshManager.refreshState,
  ) { deliveries, carriers, refreshState ->
    when {
      deliveries.isEmpty() && refreshState.refreshing -> PackagesUiState.Loading
      deliveries.isEmpty() -> PackagesUiState.Empty(
        canRefresh = refreshState.canRefresh,
        cooldownMinutes = refreshState.cooldownMinutes,
        lastRefreshError = refreshState.lastRefreshError,
      )
      else -> {
        val packages = deliveries.map { delivery ->
          val name = carriers[delivery.carrierCode]
          DeliveryUiMapper.toUiModel(delivery, name)
        }
        PackagesUiState.Content(
          packages = packages,
          refreshing = refreshState.refreshing,
          canRefresh = refreshState.canRefresh,
          cooldownMinutes = refreshState.cooldownMinutes,
          lastRefreshError = refreshState.lastRefreshError,
          hasOfflineData = refreshState.hasOfflineData,
        )
      }
    }
  }.distinctUntilChanged().stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5.seconds),
    initialValue = PackagesUiState.Loading,
  )

  init {
    viewModelScope.launch {
      carrierRepository.refresh()
      refreshManager.syncCooldownFromRepo()
      refreshManager.performRefresh()
    }
  }

  override fun onCleared() {
    super.onCleared()
    refreshManager.cleanup()
  }

  fun logout() {
    viewModelScope.launch {
      logoutUseCase()
      _uiFeedbackEvents.trySend(UiFeedbackEvent.NavigateToLogin)
    }
  }

  fun refreshPackages() {
    viewModelScope.launch {
      refreshManager.performRefresh()
    }
  }

  fun onDismissError() {
    refreshManager.resetErrorState()
  }
}
