package com.example.edusync.presentation.components.custom_text_field.password_text_field

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.edusync.R
import com.example.edusync.presentation.components.custom_text_field.base_text_field.BaseTextField
import com.example.edusync.presentation.theme.ui.AppColors

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Пароль",
    isError: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier,
    fieldHeight: Dp = 56.dp
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    BaseTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        isError = isError,
        errorMessage = errorMessage,
        modifier = modifier,
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        trailingIcon = {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentWidth(Alignment.End)
                    .padding(end = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(
                        id = if (isPasswordVisible) R.drawable.ic_visibility_off
                        else R.drawable.ic_visibility_on
                    ),
                    contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                    colorFilter = ColorFilter.tint(color = AppColors.Secondary),
                    modifier = Modifier
                        .size(24.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = {
                                isPasswordVisible = !isPasswordVisible
                            })
                        }
                )
            }
        },
        maxLines = 1,
        fieldHeight = fieldHeight
    )
}