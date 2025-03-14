package com.example.edusync.presentation.components.custom_text_field.base_text_field

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edusync.R
import com.example.edusync.presentation.theme.ui.AppColors

@Composable
fun BaseTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Start),
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: androidx.compose.foundation.text.KeyboardActions = androidx.compose.foundation.text.KeyboardActions.Default,
    trailingIcon: (@Composable (() -> Unit))? = null,
    maxLines: Int = 1,
    isScrollable: Boolean = true,
    fieldHeight: Dp = 56.dp,
    backgroundColor: Color = AppColors.Background,
    borderColor: Color = AppColors.Secondary,
    shape: Shape = RoundedCornerShape(5.dp)
) {
    val scrollState = rememberScrollState()
    val isMultiLine = maxLines > 1
    val placeholderTopPadding = if (isMultiLine) 8.dp else 17.dp
    val textVerticalPadding = if (isMultiLine) 8.dp else 14.dp
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = FocusRequester()

    val animatedBorderColor = animateColorAsState(
        targetValue = when {
            isError -> Color.Red
            isFocused -> AppColors.Primary
            else -> borderColor
        },
        label = "borderColorAnimation"
    )

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(fieldHeight)
                .border(
                    width = 1.dp,
                    color = animatedBorderColor.value,
                    shape = shape
                )
                .clip(shape)
                .background(backgroundColor)
        ) {
            if (value.isEmpty() && !isFocused) {
                Text(
                    text = label,
                    color = AppColors.SecondaryTransparent,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.ubuntu_regular)),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 16.dp, top = placeholderTopPadding),
                    style = textStyle
                )
            }

            BasicTextField(
                value = value,
                onValueChange = { newValue ->
                    if (newValue.length <= 100) {
                        onValueChange(newValue)
                    }
                },
                cursorBrush = SolidColor(AppColors.Secondary),
                textStyle = textStyle.copy(color = AppColors.Secondary),
                visualTransformation = visualTransformation,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(if (isMultiLine) Alignment.TopStart else Alignment.CenterStart)
                    .padding(
                        start = 16.dp,
                        end = if (trailingIcon != null) 40.dp else 16.dp,
                        top = textVerticalPadding,
                        bottom = textVerticalPadding
                    )
                    .let { if (isScrollable) it.horizontalScroll(scrollState) else it }
                    .focusRequester(focusRequester)
                    .onFocusChanged { isFocused = it.isFocused },
                maxLines = maxLines,
            )

            trailingIcon?.let {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = 8.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    it()
                }
            }
        }

        if (isScrollable) {
            LaunchedEffect(value) {
                scrollState.animateScrollTo(scrollState.maxValue)
            }
        }

        if (isError && !errorMessage.isNullOrEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}