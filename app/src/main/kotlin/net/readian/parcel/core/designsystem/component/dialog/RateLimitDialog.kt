package net.readian.parcel.core.designsystem.component.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import net.readian.parcel.R

@Composable
fun RateLimitDialog(
  onDismiss: () -> Unit,
  remainingMinutes: Int? = null,
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(text = stringResource(id = R.string.rate_limit_dialog_title))
    },
    text = {
      Text(
        text = if (remainingMinutes != null && remainingMinutes > 0) {
          stringResource(id = R.string.rate_limit_dialog_message_with_time, remainingMinutes)
        } else {
          stringResource(id = R.string.rate_limit_dialog_message)
        },
      )
    },
    confirmButton = {
      TextButton(onClick = onDismiss) {
        Text(stringResource(id = android.R.string.ok))
      }
    },
  )
}
