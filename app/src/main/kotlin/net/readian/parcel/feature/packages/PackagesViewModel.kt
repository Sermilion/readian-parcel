package net.readian.parcel.feature.packages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.readian.parcel.core.common.ratelimit.RefreshManager
import net.readian.parcel.core.ui.delivery.mapper.DeliveryUiMapper
import net.readian.parcel.domain.datastore.UserDataStore
import net.readian.parcel.domain.repository.CarrierRepository
import net.readian.parcel.domain.repository.PackageRepository
import net.readian.parcel.feature.packages.PackagesContract.PackagesUiState
import net.readian.parcel.feature.packages.PackagesContract.UiFeedbackEvent
import javax.inject.Inject

@Suppress("MagicNumber")
@HiltViewModel
class PackagesViewModel @Inject constructor(
  private val userDataStore: UserDataStore,
  private val packageRepository: PackageRepository,
  private val carrierRepository: CarrierRepository,
  private val refreshManager: RefreshManager,
) : ViewModel() {

  private val _uiFeedbackEvents = Channel<UiFeedbackEvent>()
  val uiFeedbackEvents = _uiFeedbackEvents.receiveAsFlow()

  private val initialLoadCompleted = MutableStateFlow(false)

  val uiState: StateFlow<PackagesUiState> = combine(
    packageRepository.getAllPackages(),
    carrierRepository.carriers,
    refreshManager.refreshData,
    initialLoadCompleted,
  ) { deliveries, carriers, refreshData, loaded ->
    if (!loaded) {
      PackagesUiState.Loading
    } else {
      val packages = deliveries.map { delivery ->
        val name = carriers[delivery.carrierCode]
        DeliveryUiMapper.toUiModel(delivery, name)
      }
      PackagesUiState.Content(
        packages = packages,
        refreshing = refreshData.refreshing,
        canRefresh = refreshData.canRefresh,
        cooldownMinutes = refreshData.cooldownMinutes,
        lastRefreshError = refreshData.lastRefreshError,
        hasOfflineData = refreshData.hasOfflineData,
      )
    }
  }.distinctUntilChanged().stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = PackagesUiState.Loading,
  )

  init {
    viewModelScope.launch {
      carrierRepository.refresh()
      refreshManager.syncCooldownFromRepo()
      refreshManager.performRefresh()
      initialLoadCompleted.update { true }
    }
  }

  override fun onCleared() {
    super.onCleared()
    refreshManager.cleanup()
  }

  fun logout() {
    viewModelScope.launch {
      packageRepository.clearSavedApiKey()
      userDataStore.logout()
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
