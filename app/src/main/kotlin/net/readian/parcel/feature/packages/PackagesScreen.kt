package net.readian.parcel.feature.packages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import net.readian.parcel.data.model.Delivery
import net.readian.parcel.data.model.DeliveryStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackagesScreen(
    onPackageClick: (String) -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PackagesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("My Packages") },
            actions = {
                IconButton(
                    onClick = { viewModel.refreshPackages() },
                    enabled = uiState.canRefresh && !uiState.isLoading
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }
                TextButton(onClick = onNavigateToProfile) {
                    Text("Profile")
                }
            }
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading && uiState.packages.isEmpty() -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.packages.isEmpty() -> {
                    Text(
                        text = "No packages found",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.packages) { delivery ->
                            PackageCard(
                                delivery = delivery,
                                onClick = { onPackageClick(delivery.trackingNumber) }
                            )
                        }
                    }
                }
            }

            if (uiState.errorMessage != null) {
                Snackbar(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    action = {
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(uiState.errorMessage ?: "")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PackageCard(
    delivery: Delivery,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = delivery.description,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = delivery.trackingNumber,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Surface(
                    color = getStatusColor(delivery.statusCode),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = DeliveryStatus.fromCode(delivery.statusCode).displayName,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            if (delivery.carrierCode.isNotBlank()) {
                Text(
                    text = "Carrier: ${delivery.carrierCode}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun getStatusColor(statusCode: Int): Color {
    return when (DeliveryStatus.fromCode(statusCode)) {
        DeliveryStatus.COMPLETED -> MaterialTheme.colorScheme.primaryContainer
        DeliveryStatus.OUT_FOR_DELIVERY -> MaterialTheme.colorScheme.tertiary
        DeliveryStatus.IN_TRANSIT -> MaterialTheme.colorScheme.secondary
        DeliveryStatus.EXPECTING_PICKUP -> MaterialTheme.colorScheme.outline
        DeliveryStatus.FAILED_DELIVERY, DeliveryStatus.EXCEPTION -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
}