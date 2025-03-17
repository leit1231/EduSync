package com.example.edusync.presentation.views.reminderScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import com.example.edusync.presentation.viewModels.mainScreen.Lesson

@Composable
fun ReminderDialog(
    lesson: Lesson,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var reminderText by remember { mutableStateOf(TextFieldValue("")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Создать напоминание") },
        text = {
            Column {
                OutlinedTextField(
                    value = reminderText,
                    onValueChange = { reminderText = it },
                    label = { Text("Напоминание") }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSave(reminderText.text) }) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}