package com.example.edusync.presentation.components.custom_text_field.email_text_field

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.edusync.presentation.components.custom_text_field.base_text_field.BaseTextField

@Composable
fun EmailTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Электронная почта",
    isError: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier,
    fieldHeight: Dp = 56.dp
) {
    BaseTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        isError = isError,
        errorMessage = errorMessage,
        modifier = modifier,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        maxLines = 1,
        fieldHeight = fieldHeight
    )
}