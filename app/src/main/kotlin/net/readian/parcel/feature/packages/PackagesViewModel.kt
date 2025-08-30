package net.readian.parcel.feature.packages

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.readian.parcel.data.network.RateLimitException
import net.readian.parcel.domain.model.Delivery
import net.readian.parcel.domain.model.FilterMode
import net.readian.parcel.domain.repository.ApiKeyRepository
import net.readian.parcel.domain.repository.PackageRepository
import net.readian.parcel.feature.packages.mapper.DeliveryUiMapper
import net.readian.parcel.feature.packages.model.DeliveryUiModel
import javax.inject.Inject

@HiltViewModel
class PackagesViewModel @Inject constructor(
    private val apiKeyRepository: ApiKeyRepository,
    private val packageRepository: PackageRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(PackagesUiState())
    val uiState: StateFlow<PackagesUiState> = _uiState.asStateFlow()

    private val refreshCooldownMs = 3 * 60 * 1000L // 3 minutes for UI feedback

    init {
        loadPackages()
    }

    private fun loadPackages() {
        viewModelScope.launch {
            packageRepository.getAllPackages()
                .map { deliveries -> 
                    deliveries.map { delivery -> 
                        DeliveryUiMapper.toUiModel(delivery, context) 
                    }
                }
                .collect { uiModels ->
                    _uiState.value = _uiState.value.copy(packages = uiModels)
                }
        }
    }

    fun refreshPackages(filterMode: FilterMode = FilterMode.RECENT) {
        val apiKey = apiKeyRepository.getApiKey()
        if (apiKey.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "API key not found"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            // Use the new repository method with rate limiting
            val result = packageRepository.refreshPackages(apiKey, filterMode)
            
            result.fold(
                onSuccess = { _ ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        canRefresh = false
                    )
                    
                    // Re-enable refresh after cooldown
                    kotlinx.coroutines.delay(refreshCooldownMs)
                    _uiState.value = _uiState.value.copy(canRefresh = true)
                },
                onFailure = { exception ->
                    val errorMessage = when (exception) {
                        is RateLimitException -> {
                            "Rate limit exceeded. Try again in ${exception.timeUntilNextRequestMillis / 1000} seconds"
                        }
                        else -> exception.message ?: "Failed to refresh packages"
                    }
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

data class PackagesUiState(
    val packages: List<DeliveryUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val canRefresh: Boolean = true,
    val errorMessage: String? = null
)