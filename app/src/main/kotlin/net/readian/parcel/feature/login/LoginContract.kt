package net.readian.parcel.feature.login

object LoginContract {

    data class UiState(
        val apiKey: String = "",
        val isLoading: Boolean = false,
        val isError: Boolean = false,
        val error: LoginError? = null,
    )

    sealed interface LoginError {
        data object EmptyKey : LoginError
        data object InvalidKey : LoginError
        data object Network : LoginError
    }
}
