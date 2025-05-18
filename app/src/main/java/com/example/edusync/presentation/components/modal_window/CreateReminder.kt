package com.example.edusync.presentation.components.modal_window

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edusync.presentation.components.custom_text_field.generic_text_field.GenericTextField
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography
import com.example.edusync.domain.model.schedule.PairItem

@Composable
fun CreateReminderOverlay(
    pair: PairItem,
    initialText: String = "",
    onRequestClose: () -> Unit,
    onSave: (PairItem, String) -> Unit
) {
    var reminderText by remember { mutableStateOf(initialText) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var didFocus by remember { mutableStateOf(false) }

    fun saveAndClose() {
        keyboardController?.hide()
        focusManager.clearFocus(force = true)
        onSave(pair, reminderText)
        onRequestClose()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable {
                keyboardController?.hide()
                focusManager.clearFocus(force = true)
                onRequestClose()
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(AppColors.Background, RoundedCornerShape(16.dp))
                .border(BorderStroke(1.dp, AppColors.Primary), RoundedCornerShape(16.dp))
                .clickable(enabled = false) {}
                .padding(16.dp)
        ) {
            Text(
                text = "Создать напоминание",
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = AppColors.Secondary,
                style = AppTypography.title.copy(fontSize = 24.sp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            GenericTextField(
                value = reminderText,
                onValueChange = { reminderText = it },
                label = "Напоминание",
                isError = false,
                errorMessage = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onGloballyPositioned {
                        if (!didFocus) {
                            focusRequester.requestFocus()
                            keyboardController?.show()
                            didFocus = true
                        }
                    },
                keyboardActions = KeyboardActions(
                    onDone = {
                        saveAndClose()
                    }
                ),
                imeAction = ImeAction.Done
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { saveAndClose() },
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сохранить", style = AppTypography.body1.copy(fontSize = 14.sp))
            }
        }
    }
}