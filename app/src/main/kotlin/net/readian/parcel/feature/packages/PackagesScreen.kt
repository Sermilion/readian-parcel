package net.readian.parcel.feature.packages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.readian.parcel.R
import net.readian.parcel.core.designsystem.component.PullToRefreshContent
import net.readian.parcel.feature.packages.model.DeliveryUiModel
import net.readian.parcel.feature.packages.model.PackagesUiState
import net.readian.parcel.feature.packages.model.StatusColor
import net.readian.parcel.feature.packages.model.UiFeedbackEvent
import net.readian.parcel.feature.packages.model.isRefreshing
import net.readian.parcel.feature.packages.model.refreshAllowed

@Composable
fun PackagesScreen(
  onPackageClick: (String) -> Unit,
  onLogout: () -> Unit,
  viewModel: PackagesViewModel,
  modifier: Modifier = Modifier,
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val snackBarHostState = remember { SnackbarHostState() }
  var currentErrorType by remember { mutableStateOf<UiFeedbackEvent.Error?>(null) }

  LaunchedEffect(Unit) {
    viewModel.uiFeedbackEvents.collect { event ->
      when (event) {
        is UiFeedbackEvent.NavigateToLogin -> onLogout()
        is UiFeedbackEvent.Error -> currentErrorType = event
      }
    }
  }

  val errorMessage = currentErrorType?.let { getFeedbackMessage(it) }

  LaunchedEffect(snackBarHostState, errorMessage) {
    if (errorMessage != null) {
      snackBarHostState.showSnackbar(message = errorMessage, duration = SnackbarDuration.Short)
    }
  }

  PackagesScreen(
    state = uiState,
    onPackageClick = onPackageClick,
    onLogout = { viewModel.logout() },
    onRefresh = { viewModel.refreshPackages() },
    onShowRateLimit = { viewModel.showRateLimitMessage() },
    modifier = modifier,
  )
}

@Composable
fun PackagesScreen(
  state: PackagesUiState,
  onPackageClick: (String) -> Unit,
  onLogout: () -> Unit,
  onRefresh: () -> Unit,
  onShowRateLimit: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val snackBarHostState = remember { SnackbarHostState() }
  Scaffold(
    snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
    topBar = {
      TopAppBar(
        title = { Text(stringResource(id = R.string.packages_title)) },
        actions = {
          TextButton(onClick = onLogout) {
            Text(stringResource(id = R.string.logout))
          }
        },
      )
    },
  ) { paddingValues ->
    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(paddingValues),
    ) {
      PullToRefreshContent(
        onRefresh = {
          if (state.refreshAllowed) {
            onRefresh()
          } else {
            onShowRateLimit()
          }
        },
        refreshing = state.isRefreshing,
        modifier = Modifier.fillMaxSize(),
      ) {
        when (state) {
          is PackagesUiState.Loading -> {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
          }

          is PackagesUiState.Content -> {
            if (state.packages.isEmpty()) {
              Text(
                text = stringResource(id = R.string.no_packages_found),
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.bodyLarge,
              )
            } else {
              LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
              ) {
                items(state.packages, key = { it.trackingNumber }) { deliveryUi ->
                  PackageCard(
                    deliveryUi = deliveryUi,
                    onClick = { onPackageClick(deliveryUi.trackingNumber) },
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun PackageCard(
  deliveryUi: DeliveryUiModel,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Card(
    onClick = onClick,
    modifier = modifier.fillMaxWidth(),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = deliveryUi.description,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
          )
          Text(
            text = deliveryUi.trackingNumber,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }

        Surface(
          color = getStatusColor(deliveryUi.statusColor),
          shape = MaterialTheme.shapes.small,
          modifier = Modifier.padding(start = 8.dp),
        ) {
          Text(
            text = stringResource(id = deliveryUi.statusTextRes),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
          )
        }
      }

      if (deliveryUi.carrierCode.isNotBlank()) {
        Text(
          text = stringResource(
            id = net.readian.parcel.R.string.carrier_label,
            deliveryUi.carrierName ?: deliveryUi.carrierCode,
          ),
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(top = 8.dp),
        )
      }
    }
  }
}

@Composable
private fun getStatusColor(statusColor: StatusColor): Color {
  return when (statusColor) {
    StatusColor.SUCCESS -> MaterialTheme.colorScheme.primaryContainer
    StatusColor.INFO -> MaterialTheme.colorScheme.secondary
    StatusColor.WARNING -> MaterialTheme.colorScheme.outline
    StatusColor.ERROR -> MaterialTheme.colorScheme.errorContainer
    StatusColor.NEUTRAL -> MaterialTheme.colorScheme.surfaceVariant
  }
}

@Composable
private fun getFeedbackMessage(event: UiFeedbackEvent.Error): String {
  return when (event) {
    is UiFeedbackEvent.Error.RateLimit -> stringResource(
      id = R.string.error_rate_limit_with_time,
      event.remainingMinutes,
    )

    is UiFeedbackEvent.Error.RateLimitGeneral -> stringResource(id = R.string.error_rate_limit_general)
  }
}
