package com.example.edusync.presentation.viewModels.mainScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainScreenViewModel: ViewModel() {
    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ofPattern("d MMMM")

    private val _schedule = MutableStateFlow<List<DaySchedule>>(emptyList())
    val schedule: StateFlow<List<DaySchedule>> = _schedule

    @RequiresApi(Build.VERSION_CODES.O)
    private val today = LocalDate.now()
    @RequiresApi(Build.VERSION_CODES.O)
    private val _selectedDate = MutableStateFlow(today)
    @RequiresApi(Build.VERSION_CODES.O)
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    init {
        loadSchedule()
    }

    private fun loadSchedule() {
        val fakeData = listOf(
            DaySchedule("15 марта", listOf(Lesson(1, "Экономика", "08:00", "09:30", "405 ауд.", "Григорьева Л.Ф."))),
            DaySchedule("14 марта", listOf(Lesson(2, "Математика", "10:00", "11:30", "202 ауд.", "Иванов И.И.")))
        )
        _schedule.value = fakeData
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun nextDay() {
        viewModelScope.launch {
            _selectedDate.value = _selectedDate.value.plusDays(1)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun previousDay() {
        if (_selectedDate.value != today) {
            viewModelScope.launch {
                _selectedDate.value = _selectedDate.value.minusDays(1)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getFormattedDate(): String {
        return _selectedDate.value.format(formatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    val canSwipeBack: StateFlow<Boolean> = selectedDate.map { it.isAfter(today) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)
}


data class Lesson(
    val id: Int,
    val title: String,
    val startTime: String,
    val endTime: String,
    val room: String,
    val teacher: String
)

data class DaySchedule(
    val date: String,
    val lessons: List<Lesson>
)