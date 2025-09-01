package net.readian.parcel.core.designsystem.component.button

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import net.readian.parcel.R

@Composable
fun RefreshButton(
  onRefresh: () -> Unit,
  canRefresh: Boolean,
  isRefreshing: Boolean,
  onShowRateLimit: () -> Unit,
  modifier: Modifier = Modifier,
) {
  IconButton(
    onClick = { if (canRefresh) onRefresh() else onShowRateLimit() },
    enabled = !isRefreshing,
    modifier = modifier,
  ) {
    if (isRefreshing) {
      CircularProgressIndicator(
        modifier = Modifier.padding(4.dp),
        strokeWidth = 2.dp,
      )
    } else {
      Icon(
        Icons.Outlined.Refresh,
        contentDescription = stringResource(id = R.string.refresh),
        tint = if (canRefresh) {
          MaterialTheme.colorScheme.onSurface
        } else {
          MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        },
      )
    }
  }
}
