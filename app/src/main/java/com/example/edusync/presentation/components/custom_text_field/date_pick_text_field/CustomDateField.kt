package com.example.edusync.presentation.components.custom_text_field.date_pick_text_field

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.edusync.presentation.components.custom_text_field.base_text_field.BaseTextField

@Composable
fun CustomDateField(
    value: String,
    onClick: () -> Unit,
    label: String = "Выберите дату",
    isError: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier,
    fieldHeight: Dp = 56.dp
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        BaseTextField(
            value = value,
            onValueChange = {},
            label = label,
            isError = isError,
            errorMessage = errorMessage,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Text,
                imeAction = ImeAction.None
            ),
            fieldHeight = fieldHeight,
            maxLines = 1,
            isScrollable = false,
            enabled = false // отключаем ввод руками
        )
    }
}