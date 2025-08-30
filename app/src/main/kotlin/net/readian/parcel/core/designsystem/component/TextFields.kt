package net.readian.parcel.core.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun StarterAppTextField(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        imeAction = ImeAction.Done,
    ),
    onValueChanged: (String) -> Unit,
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .then(modifier),
        value = value,
        enabled = enabled,
        isError = isError,
        maxLines = 1,
        singleLine = true,
        onValueChange = onValueChanged,
        keyboardOptions = keyboardOptions,
        label = { Text(text = label) },
        visualTransformation = visualTransformation,
    )
}
