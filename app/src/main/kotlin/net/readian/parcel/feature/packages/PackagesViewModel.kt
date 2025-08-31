package net.readian.parcel.feature.packages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.readian.parcel.domain.datastore.UserDataStore
import net.readian.parcel.domain.model.RateLimitException
import net.readian.parcel.domain.repository.CarrierRepository
import net.readian.parcel.domain.repository.PackageRepository
import net.readian.parcel.feature.packages.mapper.DeliveryUiMapper
import net.readian.parcel.feature.packages.model.PackagesUiState
import net.readian.parcel.feature.packages.model.RefreshState
import net.readian.parcel.feature.packages.model.UiFeedbackEvent
import javax.inject.Inject

@Suppress("MagicNumber")
@HiltViewModel
class PackagesViewModel @Inject constructor(
    private val userDataStore: UserDataStore,
    private val packageRepository: PackageRepository,
    private val carrierRepository: CarrierRepository,
) : ViewModel() {

    private val _uiFeedbackEvents = Channel<UiFeedbackEvent>()
    val uiFeedbackEvents = _uiFeedbackEvents.receiveAsFlow()

    private val refreshCooldownMs = 3 * 60 * 1000L
    private var cooldownJob: Job? = null

    private val refreshState = MutableStateFlow(RefreshState())
    private val initialLoadCompleted = MutableStateFlow(false)

    private val refreshUi = refreshState
        .map { it.refreshing to it.canRefresh }
        .distinctUntilChanged()

    val uiState: StateFlow<PackagesUiState> = combine(
        packageRepository.getAllPackages(),
        carrierRepository.carriers,
        refreshUi,
        initialLoadCompleted,
    ) { deliveries, carriers, (refreshing, canRefresh), loaded ->
        if (!loaded) {
            PackagesUiState.Loading
        } else {
            val packages = deliveries.map { delivery ->
                val name = carriers[delivery.carrierCode]
                DeliveryUiMapper.toUiModel(delivery, name)
            }
            PackagesUiState.Content(
                packages = packages,
                refreshing = refreshing,
                canRefresh = canRefresh,
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
            syncCooldownFromRepo()
            performRefresh()
            initialLoadCompleted.update { true }
        }
    }

    fun logout() {
        viewModelScope.launch {
            packageRepository.clearSavedApiKey()
            userDataStore.logout()
            _uiFeedbackEvents.trySend(UiFeedbackEvent.NavigateToLogin)
        }
    }

    fun refreshPackages() {
        viewModelScope.launch { performRefresh() }
    }

    private suspend fun performRefresh() {
        try {
            refreshState.update { it.copy(refreshing = true) }
            packageRepository.refreshPackages()
            startCooldownTimer(refreshCooldownMs)
        } catch (e: RateLimitException) {
            startCooldownTimer(e.timeUntilNextRequestMillis)
        } finally {
            refreshState.update { it.copy(refreshing = false) }
        }
    }

    private fun startCooldownTimer(initialMillis: Long) {
        cooldownJob?.cancel()
        cooldownJob = viewModelScope.launch {
            var remaining = (initialMillis / 1000).toInt()
            refreshState.update { it.copy(canRefresh = false, cooldownSeconds = remaining) }
            while (remaining > 0) {
                delay(1000)
                remaining -= 1
                refreshState.update { it.copy(cooldownSeconds = remaining) }
            }
            refreshState.update { it.copy(canRefresh = true, cooldownSeconds = null) }
        }
    }

    suspend fun syncCooldownFromRepo() {
        val info = packageRepository.getRateLimitInfo()
        if (info.timeUntilNextRequestMs > 0) {
            startCooldownTimer(info.timeUntilNextRequestMs)
            refreshState.update { it.copy(refreshing = false) }
        }
    }

    fun showRateLimitMessage() {
        val remainingSeconds = refreshState.value.cooldownSeconds ?: 0
        val event = if (remainingSeconds > 0) {
            val minutes = (remainingSeconds + 59) / 60
            UiFeedbackEvent.Error.RateLimit(minutes)
        } else {
            UiFeedbackEvent.Error.RateLimitGeneral
        }
        refreshState.update { it.copy(refreshing = false) }
        _uiFeedbackEvents.trySend(event)
    }
}
