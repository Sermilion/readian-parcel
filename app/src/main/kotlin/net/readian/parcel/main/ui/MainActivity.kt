package net.readian.parcel.main.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import net.readian.parcel.core.ui.app.ParcelApp
import net.readian.parcel.data.repository.ApiKeyRepository
import javax.inject.Inject
import net.readian.parcel.main.MainActivityContract.UiState
import net.readian.parcel.main.MainActivityViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()
    
    @Inject
    lateinit var apiKeyRepository: ApiKeyRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { viewModel.isLoading.value }
        setContent {
            val uiState by viewModel.state.collectAsStateWithLifecycle()
            when (val state = uiState) {
                is UiState.Content -> {
                    ParcelApp(hasApiKey = apiKeyRepository.hasApiKey())
                }
                UiState.Loading -> {
                    // showing splash screen
                }
            }
        }
    }
}
