package net.readian.parcel.core.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import net.readian.parcel.core.ui.delivery.model.StatusColorUiModel

@Composable
fun DeliveryStatusChip(
  @StringRes textRes: Int,
  statusColor: StatusColorUiModel,
  modifier: Modifier = Modifier,
) {
  AssistChip(
    onClick = {},
    label = { Text(text = stringResource(id = textRes)) },
    leadingIcon = {
      val icon = statusIconFor(statusColor)
      Icon(imageVector = icon, contentDescription = stringResource(id = textRes))
    },
    colors = AssistChipDefaults.assistChipColors(
      containerColor = MaterialTheme.colorScheme.surface,
      labelColor = MaterialTheme.colorScheme.onSurface,
      leadingIconContentColor = MaterialTheme.colorScheme.primary,
    ),
    modifier = modifier,
  )
}

fun statusIconFor(statusColor: StatusColorUiModel): ImageVector = when (statusColor) {
  StatusColorUiModel.SUCCESS -> Icons.Outlined.CheckCircle
  StatusColorUiModel.INFO -> Icons.Outlined.LocalShipping
  StatusColorUiModel.WARNING -> Icons.Outlined.ErrorOutline
  StatusColorUiModel.ERROR -> Icons.Outlined.ErrorOutline
  StatusColorUiModel.NEUTRAL -> Icons.Outlined.LocalShipping
}
