package com.example.edusync.presentation.views.group.components.survey

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.edusync.R
import com.example.edusync.presentation.components.custom_text_field.generic_text_field.GenericTextField
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography

@Composable
fun CreatePollDialog(
    onDismiss: () -> Unit,
    onPollCreated: (String, List<String>) -> Unit
) {
    var question by remember { mutableStateOf("") }
    val options = remember { mutableStateListOf("", "") }
    val isValid = question.isNotBlank() && options.size >= 2 && options.all { it.isNotBlank() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { onDismiss() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(
                        color = AppColors.Background,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(
                        BorderStroke(1.dp, AppColors.Primary),
                        RoundedCornerShape(16.dp)
                    )
                    .align(Alignment.Center)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Создать опрос",
                        fontWeight = FontWeight.Medium,
                        color = AppColors.Secondary,
                        style = AppTypography.title.copy(fontSize = 24.sp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Поле для вопроса
                    GenericTextField(
                        value = question,
                        onValueChange = { question = it },
                        label = "Вопрос",
                        isError = question.isBlank() && options.any { it.isNotBlank() },
                        errorMessage = "Введите вопрос",
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Поля для вариантов ответов
                    options.forEachIndexed { index, text ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            GenericTextField(
                                value = text,
                                onValueChange = { options[index] = it },
                                label = "Вариант ${index + 1}",
                                isError = text.isBlank() && question.isNotBlank(),
                                errorMessage = "Введите вариант ответа",
                            )

                            IconButton(
                                onClick = { options.removeAt(index) },
                                enabled = options.size > 2
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_close),
                                    contentDescription = "Удалить вариант"
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Button(
                        onClick = { options.add("") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = options.size < 5,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.Primary.copy(alpha = 0.1f),
                            contentColor = AppColors.Primary
                        )
                    ) {
                        Text("Добавить вариант")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (isValid) {
                                onPollCreated(question, options.filter { it.isNotBlank() })
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.Primary
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isValid
                    ) {
                        Text(
                            text = "Создать опрос",
                            style = AppTypography.body1.copy(fontSize = 14.sp)
                        )
                    }
                }
            }
        }
    }
}