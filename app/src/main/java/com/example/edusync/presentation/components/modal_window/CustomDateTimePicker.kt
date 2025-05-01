package com.example.edusync.presentation.components.modal_window

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.edusync.presentation.components.custom_text_field.date_pick_text_field.CustomDateField
import com.example.edusync.presentation.theme.ui.AppColors
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDateTimePicker(
    selectedDate: String,
    selectedStartTime: String,
    selectedEndTime: String,
    onDateSelected: (String) -> Unit,
    onStartTimeSelected: (String) -> Unit,
    onEndTimeSelected: (String) -> Unit
) {
    val showDatePicker = remember { mutableStateOf(false) }
    val showStartTimePicker = remember { mutableStateOf(false) }
    val showEndTimePicker = remember { mutableStateOf(false) }

    val today = LocalDate.now()
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

    val dateState = rememberDatePickerState(
        initialDisplayedMonthMillis = today,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= today
            }
        }
    )


    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    if (showDatePicker.value) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker.value = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = dateState.selectedDateMillis ?: return@TextButton
                    val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                    onDateSelected(formatter.format(date))
                    showDatePicker.value = false
                }) {
                    Text("OK", color = AppColors.Primary)
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = AppColors.OnBackground,
                titleContentColor = AppColors.Secondary,
                headlineContentColor = AppColors.Secondary,
                weekdayContentColor = AppColors.Primary,
                subheadContentColor = AppColors.Primary,
                selectedDayContentColor = AppColors.OnBackground,
                selectedDayContainerColor = AppColors.Primary
            )
        ) {
            DatePicker(state = dateState)
        }
    }

    if (showStartTimePicker.value) {
        CustomTimePickerDialog(
            onDismissRequest = { showStartTimePicker.value = false },
            onConfirm = { hour, minute ->
                onStartTimeSelected("%02d:%02d".format(hour, minute))
            }
        )
    }

    if (showEndTimePicker.value) {
        CustomTimePickerDialog(
            onDismissRequest = { showEndTimePicker.value = false },
            onConfirm = { hour, minute ->
                onEndTimeSelected("%02d:%02d".format(hour, minute))
            }
        )
    }

    Column {
        CustomDateField(
            value = selectedDate,
            onClick = { showDatePicker.value = true }
        )
        Spacer(modifier = Modifier.height(16.dp))
        CustomDateField(
            value = selectedStartTime,
            label = "Время начала",
            onClick = { showStartTimePicker.value = true }
        )
        Spacer(modifier = Modifier.height(16.dp))
        CustomDateField(
            value = selectedEndTime,
            label = "Время конца",
            onClick = { showEndTimePicker.value = true }
        )
    }
}