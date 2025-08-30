package net.readian.parcel.feature.packages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.readian.parcel.data.api.ParcelApiService
import net.readian.parcel.data.model.Delivery
import net.readian.parcel.data.repository.ApiKeyRepository
import net.readian.parcel.data.repository.PackageRepository
import javax.inject.Inject

@HiltViewModel
class PackagesViewModel @Inject constructor(
    private val apiKeyRepository: ApiKeyRepository,
    private val parcelApiService: ParcelApiService,
    private val packageRepository: PackageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PackagesUiState())
    val uiState: StateFlow<PackagesUiState> = _uiState.asStateFlow()

    private var lastRefreshTime = 0L
    private val refreshCooldownMs = 3 * 60 * 1000L // 3 minutes

    init {
        loadPackages()
    }

    private fun loadPackages() {
        viewModelScope.launch {
            packageRepository.getAllPackages().collect { packages ->
                _uiState.value = _uiState.value.copy(packages = packages)
            }
        }
    }

    fun refreshPackages() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastRefreshTime < refreshCooldownMs) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Please wait ${((refreshCooldownMs - (currentTime - lastRefreshTime)) / 1000)} seconds before refreshing again"
            )
            return
        }

        val apiKey = apiKeyRepository.getApiKey()
        if (apiKey.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "API key not found"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val response = parcelApiService.getDeliveries(apiKey)
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val deliveries = response.body()?.deliveries ?: emptyList()
                    packageRepository.savePackages(deliveries)
                    lastRefreshTime = currentTime
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        canRefresh = false
                    )
                    
                    // Re-enable refresh after cooldown
                    kotlinx.coroutines.delay(refreshCooldownMs)
                    _uiState.value = _uiState.value.copy(canRefresh = true)
                } else {
                    val errorMessage = response.body()?.errorMessage 
                        ?: "Failed to fetch packages"
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Network error: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

data class PackagesUiState(
    val packages: List<Delivery> = emptyList(),
    val isLoading: Boolean = false,
    val canRefresh: Boolean = true,
    val errorMessage: String? = null
)