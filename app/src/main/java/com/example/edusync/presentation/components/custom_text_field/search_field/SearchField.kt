package com.example.edusync.presentation.components.custom_text_field.search_field

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.edusync.R
import com.example.edusync.presentation.components.custom_text_field.base_text_field.BaseTextField
import com.example.edusync.presentation.theme.ui.AppColors

@Composable
fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit,
    imeAction: ImeAction,
    modifier: Modifier = Modifier,
    label: String = "Поиск"
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    BaseTextField(
        value = value,
        onValueChange = { newValue ->
            if (newValue.length <= 100) {
                onValueChange(newValue)
            }
        },
        label = label,
        modifier = modifier,
        textStyle = LocalTextStyle.current.copy(),
        trailingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "Search",
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(24.dp)
                    .clickable {
                        keyboardController?.hide()
                        onSearch()
                    },
                tint = AppColors.Secondary
            )
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                keyboardController?.hide()
                onSearch()
            }
        ),
        backgroundColor = AppColors.Background,
        borderColor = AppColors.Primary,
        shape = RoundedCornerShape(10.dp),
        fieldHeight = 60.dp
    )
}