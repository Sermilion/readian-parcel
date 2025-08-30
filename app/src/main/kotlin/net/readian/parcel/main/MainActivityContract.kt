package net.readian.parcel.main

object MainActivityContract {

    sealed interface UiState {

        data class Content(val logged: Boolean) : UiState

        data object Loading : UiState
    }
}
