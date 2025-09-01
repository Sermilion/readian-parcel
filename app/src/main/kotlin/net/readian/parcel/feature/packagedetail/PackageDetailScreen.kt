package net.readian.parcel.feature.packagedetail

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.readian.parcel.R
import net.readian.parcel.core.common.TimeUtils
import net.readian.parcel.core.designsystem.component.DeliveryStatusChip
import net.readian.parcel.core.designsystem.component.ErrorBanner
import net.readian.parcel.core.designsystem.component.button.RefreshButton
import net.readian.parcel.core.designsystem.component.dialog.RateLimitDialog
import net.readian.parcel.core.ui.delivery.model.DeliveryEventUiModel
import net.readian.parcel.core.ui.delivery.model.DeliveryUiModel
import net.readian.parcel.feature.packagedetail.PackageDetailContract.UiState

@Composable
fun PackageDetailScreen(
  onNavigateBack: () -> Unit,
  viewModel: PackageDetailViewModel,
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  val clipboard = LocalClipboardManager.current
  val context = LocalContext.current
  val snackBarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()

  PackageDetailContent(
    state = state,
    actions = object : PackageDetailActions {
      override fun onNavigateBack() = onNavigateBack()

      override fun onRefresh() = viewModel.refreshPackage()

      override fun onDismissError() = viewModel.onDismissError()

      override fun onCopyTracking(tracking: String) {
        clipboard.setText(AnnotatedString(tracking))
        scope.launch { snackBarHostState.showSnackbar(context.getString(R.string.copied_to_clipboard)) }
      }

      override fun onShare(model: DeliveryUiModel) {
        handleSharing(
          delivery = model,
          context = context,
          scope = scope,
          snackBarHostState = snackBarHostState,
        )
      }
    },
    snackBarHostState = snackBarHostState,
  )
}

@Composable
private fun PackageDetailContent(
  state: UiState,
  actions: PackageDetailActions,
  modifier: Modifier = Modifier,
  snackBarHostState: SnackbarHostState? = null,
) {
  val host = snackBarHostState ?: remember { SnackbarHostState() }
  var showRateLimitDialog by remember { mutableStateOf(false) }

  Scaffold(
    snackbarHost = { SnackbarHost(hostState = host) },
    modifier = modifier,
    topBar = {
      CenterAlignedTopAppBar(
        title = { Text(stringResource(id = R.string.packages_title)) },
        navigationIcon = {
          IconButton(onClick = actions::onNavigateBack) {
            Icon(
              Icons.AutoMirrored.Default.ArrowBack,
              contentDescription = stringResource(id = android.R.string.cancel),
            )
          }
        },
        actions = {
          if (state is UiState.Data) {
            RefreshButton(
              onRefresh = actions::onRefresh,
              canRefresh = state.canRefresh,
              isRefreshing = state.refreshing,
              onShowRateLimit = { showRateLimitDialog = true },
            )
            val delivery = state.delivery
            IconButton(onClick = { actions.onCopyTracking(delivery.trackingNumber) }) {
              Icon(
                Icons.Default.ContentCopy,
                contentDescription = stringResource(id = R.string.copy),
              )
            }
            IconButton(onClick = { actions.onShare(delivery) }) {
              Icon(Icons.Default.Share, contentDescription = stringResource(id = R.string.share))
            }
          }
        },
      )
    },
  ) { innerPadding ->
    Box(modifier = Modifier.padding(innerPadding)) {
      when (state) {
        is UiState.Loading -> Box(modifier = Modifier.fillMaxSize()) {
          CircularProgressIndicator(modifier = Modifier.padding(24.dp))
        }

        is UiState.NotFound -> NotFoundContent()

        is UiState.Data -> {
          Column {
            if (state.lastRefreshError != null) {
              ErrorBanner(
                message = state.lastRefreshError,
                hasOfflineData = state.hasOfflineData,
                onDismiss = actions::onDismissError,
              )
            }

            PackageDetailBody(
              delivery = state.delivery,
              modifier = Modifier.fillMaxSize(),
            )
          }
        }
      }
    }

    if (showRateLimitDialog) {
      RateLimitDialog(
        onDismiss = { showRateLimitDialog = false },
        remainingMinutes = (state as? UiState.Data)?.cooldownMinutes,
      )
    }
  }
}

@Composable
private fun NotFoundContent() {
  Box(modifier = Modifier.fillMaxSize()) {
    Text(
      text = stringResource(id = R.string.no_packages_found),
      modifier = Modifier.padding(24.dp),
    )
  }
}

@Composable
private fun PackageDetailBody(
  delivery: DeliveryUiModel,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier.fillMaxSize()) {
    HeaderCard(delivery)
    EventsSection(delivery)
  }
}

@Composable
private fun HeaderCard(delivery: DeliveryUiModel) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = delivery.description,
        style = MaterialTheme.typography.titleLarge,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
      )
      Text(
        text = stringResource(
          id = R.string.carrier_label,
          delivery.carrierName ?: delivery.carrierCode,
        ),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 8.dp),
      )
      Text(
        text = delivery.trackingNumber,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 4.dp),
      )
      TimelineAndStatus(delivery)
    }
  }
}

@Composable
private fun TimelineAndStatus(delivery: DeliveryUiModel) {
  val timelineLabel = computeTimelineLabel(delivery)
  if (timelineLabel != null) {
    Text(
      text = timelineLabel,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurface,
      modifier = Modifier.padding(top = 8.dp),
    )
  }
  DeliveryStatusChip(
    textRes = delivery.statusTextRes,
    statusColor = delivery.statusColor,
    modifier = Modifier.padding(top = 12.dp),
  )
}

@Suppress("CyclomaticComplexMethod")
@Composable
private fun computeTimelineLabel(delivery: DeliveryUiModel): String? {
  val isDelivered = delivery.statusTextRes == R.string.delivery_status_completed
  val deliveredTime = delivery.events.maxByOrNull { it.timestamp ?: Long.MIN_VALUE }
  val deliveredText = deliveredTime?.timestamp?.let { TimeUtils.formattedAbsoluteAndRelative(it) }
    ?: deliveredTime?.rawDate
  val expectedStart =
    delivery.expectedAt?.let { TimeUtils.formatTimestamp(it) } ?: delivery.expectedDateRaw
  val expectedEnd =
    delivery.expectedEndAt?.let { TimeUtils.formatTimestamp(it) } ?: delivery.expectedEndDateRaw
  return when {
    isDelivered && !deliveredText.isNullOrBlank() ->
      stringResource(id = R.string.delivered_label, deliveredText)

    !isDelivered && expectedStart != null && expectedEnd != null ->
      stringResource(id = R.string.expected_window_label, expectedStart, expectedEnd)

    !isDelivered && expectedStart != null ->
      stringResource(id = R.string.expected_label, expectedStart)

    else -> null
  }
}

@Composable
private fun EventsSection(delivery: DeliveryUiModel) {
  Column(modifier = Modifier.fillMaxSize()) {
    Text(
      text = stringResource(id = R.string.events),
      style = MaterialTheme.typography.titleMedium,
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    )
    val sorted = androidx.compose.runtime.remember(delivery.events) {
      delivery.events.sortedByDescending { it.timestamp ?: Long.MIN_VALUE }
    }
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      itemsIndexed(
        items = sorted,
        key = { index, event ->
          "${event.timestamp ?: -1}" +
            "_${event.description}" +
            "_${event.location ?: ""}" +
            "_${event.rawDate ?: ""}_$index"
        },
      ) { _, event ->
        EventRow(event)
      }
    }
  }
}

@Composable
private fun EventRow(event: DeliveryEventUiModel) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Text(
        text = event.description,
        style = MaterialTheme.typography.bodyLarge,
      )
      val timeText = event.timestamp?.let { TimeUtils.formattedAbsoluteAndRelative(it) }
        ?: (event.rawDate ?: "")
      Text(
        text = timeText,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 4.dp),
      )
      if (!event.location.isNullOrBlank()) {
        Text(
          text = event.location,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.padding(top = 4.dp),
        )
      }
    }
  }
}

private fun handleSharing(
  delivery: DeliveryUiModel,
  context: Context,
  scope: CoroutineScope,
  snackBarHostState: SnackbarHostState,
) {
  val pair = context.getString(R.string.share_carrier_and_tracking, delivery.carrierCode, delivery.trackingNumber)
  val shareText = "${delivery.description}\n$pair"
  val intent = Intent(Intent.ACTION_SEND).apply {
    type = "text/plain"
    putExtra(Intent.EXTRA_TEXT, shareText)
  }
  runCatching {
    context.startActivity(
      Intent.createChooser(intent, context.getString(R.string.share_tracking_title)),
    )
  }.onFailure {
    scope.launch { snackBarHostState.showSnackbar(context.getString(R.string.share_failed)) }
  }
}

private interface PackageDetailActions {
  fun onNavigateBack()
  fun onRefresh()
  fun onDismissError()
  fun onCopyTracking(tracking: String)
  fun onShare(model: DeliveryUiModel)
}
