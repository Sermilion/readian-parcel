package net.readian.parcel.feature.packagedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import net.readian.parcel.core.navigation.PackageDetailDestination
import net.readian.parcel.domain.repository.PackageRepository
import net.readian.parcel.feature.packages.mapper.DeliveryUiMapper
import net.readian.parcel.feature.packages.model.DeliveryUiModel
import javax.inject.Inject

@HiltViewModel
class PackageDetailViewModel @Inject constructor(
  repository: PackageRepository,
  savedStateHandle: SavedStateHandle,
) : ViewModel() {

  private val args = PackageDetailDestination(
    packageId = requireNotNull(savedStateHandle.get<String>("packageId")),
  )

  val uiState: StateFlow<UiState> = repository.getPackage(args.packageId)
    .map { delivery ->
      delivery?.let { UiState.Data(DeliveryUiMapper.toUiModel(it)) }
        ?: UiState.NotFound
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = UiState.Loading,
    )
}

sealed interface UiState {
  data object Loading : UiState
  data object NotFound : UiState
  data class Data(val delivery: DeliveryUiModel) : UiState
}
