package com.example.edusync.presentation.components.custom_text_field.dropdownMenu

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdownMenu(
    label: String,
    selectedOption: String,
    options: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    isChanged: Boolean = false,
    borderColor: Color = AppColors.Secondary,
    focusedBorderColor: Color = AppColors.Primary,
    errorBorderColor: Color = Color.Red,
    backgroundColor: Color = AppColors.Background,
    shape: Shape = RoundedCornerShape(5.dp),
    isError: Boolean = false
) {
    var searchText by remember { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    var isFirstClick by remember { mutableStateOf(true) }

    val filteredOptions = if (isChanged && searchText.isNotEmpty()) {
        options.filter { it.contains(searchText, ignoreCase = true) }
    } else {
        options
    }

    val animatedBorderColor = animateColorAsState(
        targetValue = when {
            isError -> errorBorderColor
            isFocused || expanded -> focusedBorderColor
            else -> borderColor
        },
        label = "borderColorAnimation"
    )

    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "rotationAnimation"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        val boxModifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(
                width = 1.dp,
                color = animatedBorderColor.value,
                shape = shape
            )
            .clip(shape)
            .background(backgroundColor)
            .clickable {
                if (!expanded) {
                    onExpandedChange(true)
                }
                if (expanded && !isFirstClick) {
                    focusRequester.requestFocus()
                }
                isFirstClick = false
                isFocused = expanded
            }

        Box(
            modifier = boxModifier,
            contentAlignment = Alignment.CenterStart
        ) {
            if (isChanged && expanded) {
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = {
                        Text(
                            text = "Начните вводить",
                            color = AppColors.Secondary,
                            style = AppTypography.body1.copy(fontSize = 14.sp),
                        )
                    },
                    textStyle = TextStyle(
                        color = AppColors.Secondary
                    ),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        cursorColor = AppColors.Secondary,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .onFocusChanged { isFocused = it.isFocused }
                        .padding(end = 48.dp)
                )
            } else {
                Text(
                    text = selectedOption.ifEmpty { label },
                    color = if (selectedOption.isEmpty()) AppColors.SecondaryTransparent else AppColors.Secondary,
                    style = AppTypography.body1.copy(fontSize = 16.sp),
                    fontWeight = FontWeight.ExtraLight,
                    modifier = Modifier
                        .padding(start = 16.dp, end = 48.dp)
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Expand dropdown",
                tint = animatedBorderColor.value,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
                    .rotate(rotationAngle)
                    .size(24.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        onExpandedChange(!expanded)
                        isFocused = !expanded
                    }
            )
        }

        Spacer(modifier = Modifier.height(5.dp))

        if (expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .shadow(elevation = 8.dp, shape = shape)
                    .border(
                        width = 2.dp,
                        color = animatedBorderColor.value,
                        shape = shape
                    )
                    .clip(shape)
                    .background(backgroundColor)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 157.dp)
                ) {
                    items(filteredOptions) { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = option,
                                    color = AppColors.Secondary,
                                    style = AppTypography.body1.copy(fontSize = 14.sp),
                                    fontWeight = FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                                )
                            },
                            onClick = {
                                onOptionSelected(option)
                                onExpandedChange(false)
                                searchText = ""
                                isFocused = false
                                isFirstClick = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(backgroundColor)
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(expanded) {
        if (expanded && !isFirstClick) {
            focusRequester.requestFocus()
        }
    }
}