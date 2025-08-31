package net.readian.parcel.feature.packages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.readian.parcel.R
import net.readian.parcel.core.designsystem.component.DeliveryStatusChip
import net.readian.parcel.core.designsystem.component.PullToRefreshContent
import net.readian.parcel.feature.packages.model.DeliveryUiModel
import net.readian.parcel.feature.packages.model.PackagesUiState
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

@Suppress("LongParameterList")
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
  var query by remember { mutableStateOf("") }
  var filter by remember { mutableStateOf(StatusFilter.ALL) }
  Scaffold(
    modifier = Modifier,
    snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
    topBar = {
      PackagesTopBar(
        onLogout = onLogout,
      )
    },
  ) { paddingValues ->
    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(paddingValues),
    ) {
      ModernSearchBar(
        query = query,
        onQueryChange = { query = it },
      )

      FiltersRow(
        filter = filter,
        onFilterChange = { filter = it },
      )

      PullToRefreshContent(
        onRefresh = { if (state.refreshAllowed) onRefresh() else onShowRateLimit() },
        refreshing = state.isRefreshing,
        modifier = Modifier.fillMaxSize(),
      ) {
        PackagesListSection(
          state = state,
          query = query,
          filter = filter,
          onPackageClick = onPackageClick,
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PackagesTopBar(onLogout: () -> Unit) {
  TopAppBar(
    title = { Text(stringResource(id = R.string.packages_title)) },
    actions = {
      IconButton(onClick = onLogout) {
        Icon(
          Icons.AutoMirrored.Outlined.Logout,
          contentDescription = stringResource(id = R.string.logout),
        )
      }
    },
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = MaterialTheme.colorScheme.surface,
      scrolledContainerColor = MaterialTheme.colorScheme.surface,
    ),
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernSearchBar(
  query: String,
  onQueryChange: (String) -> Unit,
) {
  val expanded = false
  SearchBar(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 8.dp),
    expanded = expanded,
    onExpandedChange = { /* locked closed */ },
    colors = SearchBarDefaults.colors(
      containerColor = MaterialTheme.colorScheme.surface,
      dividerColor = MaterialTheme.colorScheme.surfaceVariant,
    ),
    inputField = {
      SearchBarDefaults.InputField(
        query = query,
        onQueryChange = onQueryChange,
        onSearch = { /* no-op */ },
        expanded = expanded,
        onExpandedChange = { /* locked closed */ },
        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
        trailingIcon = {
          if (query.isNotEmpty()) {
            IconButton(onClick = { onQueryChange("") }) {
              Icon(Icons.Outlined.Close, contentDescription = null)
            }
          }
        },
        placeholder = { Text(text = stringResource(id = R.string.search_hint, "packages")) },
      )
    },
  ) { }
}

@Composable
private fun FiltersRow(
  filter: StatusFilter,
  onFilterChange: (StatusFilter) -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    FilterChipItem(
      selected = filter == StatusFilter.ALL,
      onClick = { onFilterChange(StatusFilter.ALL) },
      label = stringResource(id = android.R.string.untitled),
      leadingIcon = Icons.Outlined.FilterList,
      textOverride = stringResource(id = R.string.filter_all),
    )
    FilterChipItem(
      selected = filter == StatusFilter.ACTIVE,
      onClick = { onFilterChange(StatusFilter.ACTIVE) },
      label = stringResource(id = R.string.delivery_status_in_transit),
      leadingIcon = null,
    )
    FilterChipItem(
      selected = filter == StatusFilter.DELIVERED,
      onClick = { onFilterChange(StatusFilter.DELIVERED) },
      label = stringResource(id = R.string.delivery_status_completed),
      leadingIcon = null,
    )
  }
}

@Composable
private fun BoxScope.PackagesListSection(
  state: PackagesUiState,
  query: String,
  filter: StatusFilter,
  onPackageClick: (String) -> Unit,
) {
  when (state) {
    is PackagesUiState.Loading -> {
      CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }

    is PackagesUiState.Content -> {
      val filtered = state.packages
        .filter { ui ->
          val matchesQuery = query.isBlank() ||
            ui.description.contains(query, ignoreCase = true) ||
            ui.trackingNumber.contains(query, ignoreCase = true) ||
            (ui.carrierName ?: ui.carrierCode).contains(query, ignoreCase = true)
          val isDelivered = ui.statusTextRes == R.string.delivery_status_completed
          val matchesFilter = when (filter) {
            StatusFilter.ALL -> true
            StatusFilter.ACTIVE -> !isDelivered
            StatusFilter.DELIVERED -> isDelivered
          }
          matchesQuery && matchesFilter
        }

      if (filtered.isEmpty()) {
        Column(
          modifier = Modifier.align(Alignment.Center),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Icon(
            imageVector = Icons.Outlined.LocalShipping,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = stringResource(id = R.string.no_packages_found),
            style = MaterialTheme.typography.bodyLarge,
          )
        }
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(16.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          items(filtered, key = { it.trackingNumber }) { deliveryUi ->
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

@Composable
private fun FilterChipItem(
  selected: Boolean,
  onClick: () -> Unit,
  label: String,
  leadingIcon: ImageVector?,
  textOverride: String? = null,
) {
  ElevatedFilterChip(
    selected = selected,
    onClick = onClick,
    label = { Text(textOverride ?: label) },
    leadingIcon = if (leadingIcon != null) {
      { Icon(leadingIcon, contentDescription = null) }
    } else {
      null
    },
    colors = FilterChipDefaults.elevatedFilterChipColors(
      containerColor = MaterialTheme.colorScheme.surface,
      selectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    ),
  )
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
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
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
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = deliveryUi.trackingNumber,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
              text = " • ",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
              text = deliveryUi.carrierName ?: deliveryUi.carrierCode,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
          }
        }

        Icon(
          imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      DeliveryStatusChip(
        textRes = deliveryUi.statusTextRes,
        statusColor = deliveryUi.statusColor,
        modifier = Modifier.padding(top = 12.dp),
      )
    }
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

private enum class StatusFilter { ALL, ACTIVE, DELIVERED }
