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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.edusync.presentation.components.custom_text_field.generic_text_field.GenericTextField
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography
import com.example.edusync.presentation.views.main.shedule.PairInfo
import com.example.edusync.presentation.views.main.shedule.PairItem

@Composable
fun CreateEditPairDialog(
    currentDate: String,
    pair: PairItem? = null,
    onSave: (PairItem) -> Unit,
    onDismiss: () -> Unit
) {
    var doctrine by remember { mutableStateOf(pair?.pairInfo?.firstOrNull()?.doctrine ?: "") }
    var teacher by remember { mutableStateOf(pair?.pairInfo?.firstOrNull()?.teacher ?: "") }
    var auditoria by remember { mutableStateOf(pair?.pairInfo?.firstOrNull()?.auditoria ?: "") }
    var timeStart by remember { mutableStateOf(pair?.pairInfo?.firstOrNull()?.start ?: "") }
    var timeEnd by remember { mutableStateOf(pair?.pairInfo?.firstOrNull()?.end ?: "") }

    Dialog(onDismissRequest = onDismiss,
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
                        text = if (pair == null) "Добавить пару" else "Изменить пару",
                        fontWeight = FontWeight.Medium,
                        color = AppColors.Secondary,
                        style = AppTypography.title.copy(fontSize = 24.sp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    GenericTextField(
                        value = doctrine,
                        onValueChange = { doctrine = it },
                        label = "Название дисциплины",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    GenericTextField(
                        value = teacher,
                        onValueChange = { teacher = it },
                        label = "Преподаватель",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    GenericTextField(
                        value = auditoria,
                        onValueChange = { auditoria = it },
                        label = "Аудитория",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    GenericTextField(
                        value = timeStart,
                        onValueChange = { timeStart = it },
                        label = "Начало (HH:mm)",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    GenericTextField(
                        value = timeEnd,
                        onValueChange = { timeEnd = it },
                        label = "Конец (HH:mm)",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            if (timeStart.isNotBlank() && timeEnd.isNotBlank()) {
                                val newPairInfo = PairInfo(
                                    doctrine = doctrine,
                                    teacher = teacher,
                                    auditoria = auditoria,
                                    corpus = "Главный корпус",
                                    number = 1,
                                    start = timeStart,
                                    end = timeEnd,
                                    warn = ""
                                )

                                val updatedPair = PairItem(
                                    time = "$timeStart - $timeEnd",
                                    pairInfo = listOf(newPairInfo),
                                    isoDateStart = "$currentDate $timeStart:00",
                                    isoDateEnd = "$currentDate $timeEnd:00"
                                )

                                onSave(updatedPair)
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.Primary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Сохранить",
                            style = AppTypography.body1.copy(fontSize = 14.sp)
                        )
                    }
                }
            }
        }
    }
}