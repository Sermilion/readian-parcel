package net.readian.parcel.feature.packagedetail

import androidx.lifecycle.SavedStateHandle
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import net.readian.parcel.core.common.ratelimit.RefreshManager
import net.readian.parcel.core.common.ratelimit.RefreshState
import net.readian.parcel.domain.repository.PackageRepository
import net.readian.parcel.feature.packagedetail.PackageDetailContract.UiState

@OptIn(ExperimentalCoroutinesApi::class)
class PackageDetailViewModelTest : FunSpec() {
  private val testDispatcher = UnconfinedTestDispatcher()

  @MockK
  private lateinit var packageRepository: PackageRepository

  @MockK
  private lateinit var refreshManager: RefreshManager

  @MockK
  private lateinit var savedStateHandle: SavedStateHandle

  private lateinit var viewModel: PackageDetailViewModel

  init {
    beforeSpec {
      Dispatchers.setMain(testDispatcher)
    }

    afterSpec {
      Dispatchers.resetMain()
    }

    beforeTest {
      MockKAnnotations.init(this@PackageDetailViewModelTest)
      coEvery { refreshManager.syncCooldownFromRepo() } returns Unit
      every { refreshManager.refreshState } returns MutableStateFlow(RefreshState())
    }

    test("initial state should be loading") {
      every { savedStateHandle.get<String>("packageId") } returns "123"
      every { packageRepository.getPackage("123") } returns flowOf(null)

      viewModel = PackageDetailViewModel(
        packageRepository,
        refreshManager,
        savedStateHandle,
      )

      viewModel.uiState.value.shouldBeTypeOf<UiState.Loading>()
    }

    test("refresh should call refresh manager") {
      every { savedStateHandle.get<String>("packageId") } returns "123"
      every { packageRepository.getPackage("123") } returns flowOf(null)
      coEvery { refreshManager.performRefresh() } returns Unit

      viewModel = PackageDetailViewModel(
        packageRepository,
        refreshManager,
        savedStateHandle,
      )

      viewModel.refreshPackage()
      testDispatcher.scheduler.advanceUntilIdle()

      coVerify { refreshManager.performRefresh() }
    }

    test("dismiss error should call refresh manager") {
      every { savedStateHandle.get<String>("packageId") } returns "123"
      every { packageRepository.getPackage("123") } returns flowOf(null)
      every { refreshManager.resetErrorState() } returns Unit

      viewModel = PackageDetailViewModel(
        packageRepository,
        refreshManager,
        savedStateHandle,
      )

      viewModel.onDismissError()

      coVerify { refreshManager.resetErrorState() }
    }
  }
}
