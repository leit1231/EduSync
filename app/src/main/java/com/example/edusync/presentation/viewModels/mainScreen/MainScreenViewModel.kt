package com.example.edusync.presentation.viewModels.mainScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusync.common.LoadingState
import com.example.edusync.common.Resource
import com.example.edusync.data.local.EncryptedSharedPreference
import com.example.edusync.presentation.navigation.Destination
import com.example.edusync.presentation.navigation.Navigator
import com.example.edusync.domain.model.schedule.Day
import com.example.edusync.presentation.views.main.mainScreen.MainScreenState
import com.example.edusync.domain.model.schedule.PairInfo
import com.example.edusync.domain.model.schedule.PairItem
import com.example.edusync.domain.model.schedule.Schedule
import com.example.edusync.domain.use_case.group.GetGroupByIdUseCase
import com.example.edusync.presentation.views.main.component.dateItem.toCalendar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainScreenViewModel(
    private val navigator: Navigator,
    private val encryptedSharedPreference: EncryptedSharedPreference,
    private val getGroupById: GetGroupByIdUseCase
): ViewModel() {

    private val _isAllScheduleVisible = MutableStateFlow(false)
    val isAllScheduleVisible: StateFlow<Boolean> = _isAllScheduleVisible.asStateFlow()

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode

    private val _selectedPair = MutableStateFlow<PairItem?>(null)
    val selectedPair: StateFlow<PairItem?> = _selectedPair

    private val _state = MutableStateFlow(MainScreenState())
    val state = _state.asStateFlow()

    private val _isTeacher = MutableStateFlow(false)
    val isTeacher: StateFlow<Boolean> = _isTeacher.asStateFlow()

    private val _institutionId = MutableStateFlow<Int?>(null)
    val institutionId: StateFlow<Int?> = _institutionId

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val user = encryptedSharedPreference.getUser()
            _isTeacher.value = user?.isTeacher ?: false
            _institutionId.value = user?.institutionId

            _state.update {
                it.copy(
                    schedule = generateTestSchedule(),
                    scheduleLoading = LoadingState.Success
                )
            }

            user?.groupId?.let { groupId ->
                getGroupById(groupId).collect { groupResult ->
                    when (groupResult) {
                        is Resource.Success -> {
                            _state.update {
                                it.copy(selectedGroup = groupResult.data?.name ?: "Группа не найдена")
                            }
                        }
                        is Resource.Error -> {
                            Log.e("MainScreen", "Ошибка загрузки группы: ${groupResult.message}")
                            _state.update { it.copy(selectedGroup = "Ошибка загрузки") }
                        }
                        is Resource.Loading -> {
                        }
                    }
                }
            }
        }
    }

    fun goToSearch(isTeacherMode: Boolean) {
        viewModelScope.launch {
            val institutionId = _institutionId.value ?: return@launch
            navigator.navigate(Destination.SearchScreen(isTeacherMode, institutionId))
        }
    }

    fun toggleEditMode() {
        _isEditMode.value = !_isEditMode.value
        if (!_isEditMode.value) {
            setSelectedPair(null)
        }
    }

    fun setSelectedPair(pair: PairItem?) {
        _selectedPair.value = pair
    }

    fun showAllSchedule() {
        _isAllScheduleVisible.value = true
    }

    fun showSchedule() {
        _isAllScheduleVisible.value = false
    }

    fun addPair(newPair: PairItem) {
        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(
                    schedule = currentState.schedule?.copy(
                        days = currentState.schedule.days.map { day ->
                            if (day.isoDateDay == newPair.isoDateStart.substring(0, 10)) {
                                day.copy(pairs = day.pairs + newPair)
                            } else day
                        }.map { sortPairs(it) }
                    )
                )
            }
        }
    }

    fun updatePair(updatedPair: PairItem) {
        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(
                    schedule = currentState.schedule?.copy(
                        days = currentState.schedule.days.map { day ->
                            day.copy(pairs = day.pairs.map {
                                if (it.isoDateStart == updatedPair.isoDateStart) updatedPair else it
                            })
                        }.map { sortPairs(it) }
                    )
                )
            }
        }
    }

    fun deletePair(pair: PairItem) {
        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(
                    schedule = currentState.schedule?.copy(
                        days = currentState.schedule.days.map { day ->
                            day.copy(pairs = day.pairs.filter { it != pair })
                        }
                    )
                )
            }
        }
    }

    private fun sortPairs(day: Day): Day {
        return day.copy(pairs = day.pairs.sortedBy { it.isoDateStart })
    }


    private fun generateTestSchedule(): Schedule {
        return Schedule(
            name = "ИС-41",
            days = listOf(
                createDay("2025-03-19", listOf(
                    createPair("09:00", "10:30", "Математический анализ" , "Иванов И.И."),
                    createPair("10:40", "12:10", "Программирование", "Петров П.П."),
                    createPair("13:00", "14:30", "Экономика в профессиональной деятельности", "Сидоров С.С."),
                    createPair("15:00", "16:30", "Литература", "Сидоров С.С."),
                    createPair("15:00", "16:30", "Литература", "Сидоров С.С."),
                    createPair("15:00", "16:30", "Литература", "Сидоров С.С."),
                    createPair("15:00", "16:30", "Литература", "Сидоров С.С."),
                ).sortedBy { it.isoDateStart }),
                createDay("2025-03-20", listOf(
                    createPair("09:00", "10:30", "История", "Кузнецова А.В."),
                    createPair("11:00", "12:30", "Английский язык", "Смирнова Е.А.")
                ).sortedBy { it.isoDateStart }),
                createDay("2025-03-21", listOf(
                    createPair("10:00", "11:30", "Экономика", "Васильев Д.Е."),
                    createPair("12:00", "13:30", "Философия", "Николаева М.Р.")
                ).sortedBy { it.isoDateStart }),
                createDay("2025-03-22", listOf(
                    createPair("09:30", "11:00", "Химия", "Козлов О.Г."),
                    createPair("11:30", "13:00", "Биология", "Морозова Т.Н.")
                ).sortedBy { it.isoDateStart })
            )
        )
    }

    private fun createDay(date: String, pairs: List<PairItem>): Day {
        return Day(
            isoDateDay = date,
            day = SimpleDateFormat("EEEE", Locale.getDefault()).format(date.toCalendar().time),
            pairs = pairs
        )
    }

    private fun createPair(startTime: String, endTime: String, subject: String, teacher: String): PairItem {
        val date = "2025-03-19"
        return PairItem(
            time = "$startTime - $endTime",
            isoDateStart = "$date ${startTime}:00",
            isoDateEnd = "$date ${endTime}:00",
            pairInfo = listOf(
                PairInfo(
                    doctrine = subject,
                    teacher = teacher,
                    auditoria = (101..105).random().toString(),
                    corpus = "Главный корпус",
                    number = 1,
                    start = startTime,
                    end = endTime,
                    warn = ""
                )
            )
        )
    }

    fun createNewPair(date: String): PairItem {
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Calendar.getInstance().time)
        return PairItem(
            time = "$currentTime - $currentTime",
            isoDateStart = "$date $currentTime:00",
            isoDateEnd = "$date $currentTime:00",
            pairInfo = listOf(
                PairInfo(
                doctrine = "",
                teacher = "",
                auditoria = "",
                corpus = "",
                number = 0,
                start = currentTime,
                end = currentTime,
                warn = ""
            )
            )
        )
    }

    fun saveReminder(pair: PairItem, reminderText: String) {
        viewModelScope.launch {
            val dayIndex = state.value.schedule?.days?.indexOfFirst {
                it.isoDateDay == pair.isoDateStart.substring(0, 10)
            } ?: return@launch

            val pairIndex =
                state.value.schedule?.days?.get(dayIndex)?.pairs?.indexOf(pair) ?: return@launch

            val updatedPair = pair.copy(
                pairInfo = pair.pairInfo.map {
                    it.copy(warn = reminderText)
                }
            )

            _state.update { currentState ->
                currentState.copy(
                    schedule = currentState.schedule?.copy(
                        days = currentState.schedule.days.mapIndexed { index, day ->
                            if (index == dayIndex) {
                                day.copy(pairs = day.pairs.mapIndexed { pIndex, p ->
                                    if (pIndex == pairIndex) updatedPair else p
                                })
                            } else day
                        }
                    )
                )
            }
        }
    }
}
