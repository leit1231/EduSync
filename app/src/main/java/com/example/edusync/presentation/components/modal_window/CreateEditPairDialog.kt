package com.example.edusync.presentation.components.modal_window

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.edusync.domain.model.schedule.PairInfo
import com.example.edusync.presentation.components.custom_text_field.generic_text_field.GenericTextField
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography
import com.example.edusync.domain.model.schedule.PairItem
import com.example.edusync.presentation.components.custom_text_field.dropdownMenu.CustomDropdownMenu
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateEditPairDialog(
    subjects: List<String>,
    teachers: List<String>,
    teacherIdMap: Map<String, Int>,
    groups: List<String>,
    onGroupSelected: (String) -> Unit,
    pair: PairItem? = null,
    onSave: suspend (PairItem) -> Result<Unit>,
    onDismiss: () -> Unit
) {
    val inputIsoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
    val displayFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault())

    val initialSubject = pair?.pairInfo?.firstOrNull()?.doctrine.orEmpty()
    val initialGroup = pair?.pairInfo?.firstOrNull()?.group.orEmpty()
    val initialDate = try {
        pair?.isoDateStart?.substringBefore("T")?.let {
            LocalDate.parse(it, inputIsoFormatter).format(displayFormatter)
        }
    } catch (e: Exception) { null } ?: ""

    val initialStart = pair?.pairInfo?.firstOrNull()?.start.orEmpty()
    val initialEnd = pair?.pairInfo?.firstOrNull()?.end.orEmpty()
    val initialAud = pair?.pairInfo?.firstOrNull()?.auditoria.orEmpty()

    val initialTeacher = pair?.pairInfo?.firstOrNull()?.teacher?.takeIf { it.isNotBlank() }?.let { teacherName ->
        teacherIdMap.keys.firstOrNull {
            normalizeInitials(it).contains(normalizeInitials(teacherName))
        }
    } ?: ""


    var selectedSubject by remember { mutableStateOf(initialSubject) }
    var selectedTeacher by remember { mutableStateOf(initialTeacher) }
    var selectedGroup by remember { mutableStateOf(initialGroup) }
    var selectedDate by remember { mutableStateOf(initialDate) }
    var selectedStartTime by remember { mutableStateOf(initialStart) }
    var selectedEndTime by remember { mutableStateOf(initialEnd) }
    var auditoria by remember { mutableStateOf(initialAud) }

    var expandedSubject by remember { mutableStateOf(false) }
    var expandedTeacher by remember { mutableStateOf(false) }
    var expandedGroup by remember { mutableStateOf(false) }

    var errorText by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (selectedGroup.isNotBlank()) {
            onGroupSelected(selectedGroup)
        }
    }

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
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AppColors.Background, RoundedCornerShape(16.dp))
                        .border(BorderStroke(1.dp, AppColors.Primary), RoundedCornerShape(16.dp)),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = if (pair == null || pair.scheduleId == null) "Добавить пару" else "Изменить пару",
                            fontWeight = FontWeight.Medium,
                            color = AppColors.Secondary,
                            style = AppTypography.title.copy(fontSize = 24.sp)
                        )
                    }

                    item {
                        CustomDropdownMenu(
                            label = "Выберите группу",
                            options = groups,
                            selectedOption = selectedGroup,
                            expanded = expandedGroup,
                            onExpandedChange = { expandedGroup = it },
                            onOptionSelected = {
                                selectedGroup = it
                                onGroupSelected(it)
                            }
                        )
                    }

                    item {
                        CustomDropdownMenu(
                            label = "Выберите предмет",
                            options = subjects.distinct(),
                            selectedOption = selectedSubject,
                            expanded = expandedSubject,
                            onExpandedChange = { expandedSubject = it },
                            onOptionSelected = { selectedSubject = it }
                        )
                    }

                    item {
                        CustomDropdownMenu(
                            label = "Выберите преподавателя",
                            options = teachers,
                            selectedOption = selectedTeacher,
                            expanded = expandedTeacher,
                            onExpandedChange = { expandedTeacher = it },
                            onOptionSelected = { selectedTeacher = it }
                        )
                    }

                    item {
                        GenericTextField(
                            value = auditoria,
                            onValueChange = { auditoria = it },
                            label = "Аудитория",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        CustomDateTimePicker(
                            selectedDate = selectedDate,
                            selectedStartTime = selectedStartTime,
                            selectedEndTime = selectedEndTime,
                            onDateSelected = { selectedDate = it },
                            onStartTimeSelected = { selectedStartTime = it },
                            onEndTimeSelected = { selectedEndTime = it }
                        )
                    }

                    item {
                        if (!errorText.isNullOrBlank()) {
                            Text(
                                text = errorText!!,
                                color = Color.Red,
                                style = AppTypography.body1.copy(fontSize = 14.sp)
                            )
                        }
                    }

                    item {
                        Button(
                            onClick = {
                                scope.launch {
                                    loading = true
                                    val inputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                                    val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                    val isoFormattedDate = try {
                                        LocalDate.parse(selectedDate, inputFormatter).format(outputFormatter)
                                    } catch (e: Exception) {
                                        errorText = "Неверный формат даты"
                                        loading = false
                                        return@launch
                                    }

                                    val corpus = try {
                                        val audNumber = auditoria.filter { it.isDigit() }.toInt()
                                        if (audNumber > 100) "1 корпус" else "2 корпус"
                                    } catch (_: Exception) {
                                        "1 корпус"
                                    }

                                    val newPairInfo = PairInfo(
                                        doctrine = selectedSubject,
                                        teacher = selectedTeacher,
                                        group = selectedGroup,
                                        auditoria = auditoria,
                                        corpus = corpus,
                                        number = calculatePairNumber(selectedStartTime),
                                        start = selectedStartTime,
                                        end = selectedEndTime,
                                        warn = ""
                                    )

                                    val updatedPair = PairItem(
                                        time = "$selectedStartTime - $selectedEndTime",
                                        isoDateStart = "$isoFormattedDate $selectedStartTime:00",
                                        isoDateEnd = "$isoFormattedDate $selectedEndTime:00",
                                        scheduleId = pair?.scheduleId,
                                        pairInfo = listOf(newPairInfo)
                                    )

                                    val result = onSave(updatedPair)
                                    if (result.isSuccess) {
                                        errorText = null
                                        loading = false
                                        onDismiss()
                                    } else {
                                        errorText = result.exceptionOrNull()?.message ?: "Ошибка сохранения"
                                        loading = false
                                    }
                                }
                            },
                            enabled = !loading,
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (loading) "Сохраняем..." else "Сохранить",
                                style = AppTypography.body1.copy(fontSize = 14.sp)
                            )
                        }
                    }
                }
            }
        }
    }
}

fun calculatePairNumber(startTime: String): Int {
    return when (startTime) {
        "08:00" -> 1
        "09:40" -> 2
        "11:30" -> 3
        "13:10" -> 4
        "15:00" -> 5
        "16:40" -> 6
        "18:20" -> 7
        else -> 1
    }
}

fun normalizeInitials(initials: String): String {
    return initials.trim().replace(" +".toRegex(), " ")
}