package com.example.edusync.presentation.components.custom_text_field.generic_text_field

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.edusync.presentation.components.custom_text_field.base_text_field.BaseTextField

@Composable
fun GenericTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Name",
    isError: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier,
    fieldHeight: Dp = 56.dp,
    maxLines: Int = 1,
    isScrollable: Boolean = true
) {
    BaseTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        isError = isError,
        errorMessage = errorMessage,
        modifier = modifier,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        fieldHeight = fieldHeight,
        maxLines = maxLines,
        isScrollable = isScrollable
    )
}