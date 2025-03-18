package com.example.edusync.presentation.components.custom_text_field.read_only_text_field

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.edusync.presentation.components.custom_text_field.base_text_field.BaseTextField

@Composable
fun ReadOnlyTextField(
    value: String,
    label: String = "Label",
    isError: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier,
    fieldHeight: Dp = 56.dp,
    trailingIcon: @Composable (() -> Unit)? = {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            modifier = Modifier.padding(end = 16.dp)
        )
    }
) {
    BaseTextField(
        value = value,
        onValueChange = {},
        label = label,
        isError = isError,
        errorMessage = errorMessage,
        modifier = modifier,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.None),
        fieldHeight = fieldHeight,
        enabled = false,
        trailingIcon = trailingIcon
    )
}