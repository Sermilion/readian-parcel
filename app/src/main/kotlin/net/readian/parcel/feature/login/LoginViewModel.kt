package net.readian.parcel.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.readian.parcel.data.api.ParcelApiService
import net.readian.parcel.data.repository.ApiKeyRepository
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val apiKeyRepository: ApiKeyRepository,
    private val parcelApiService: ParcelApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onApiKeyChanged(apiKey: String) {
        _uiState.value = _uiState.value.copy(
            apiKey = apiKey,
            isError = false,
            errorMessage = null
        )
    }

    fun validateAndSaveApiKey(onSuccess: () -> Unit) {
        if (_uiState.value.apiKey.isBlank()) {
            _uiState.value = _uiState.value.copy(
                isError = true,
                errorMessage = "API key cannot be empty"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val response = parcelApiService.getDeliveries(_uiState.value.apiKey)
                
                if (response.isSuccessful && response.body()?.success == true) {
                    apiKeyRepository.saveApiKey(_uiState.value.apiKey)
                    onSuccess()
                } else {
                    val errorMessage = response.body()?.errorMessage 
                        ?: "Invalid API key or network error"
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isError = true,
                        errorMessage = errorMessage
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isError = true,
                    errorMessage = "Network error: ${e.message}"
                )
            }
        }
    }
}

data class LoginUiState(
    val apiKey: String = "",
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null
)