package com.example.edusync.presentation.viewModels.mainScreen

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusync.common.LoadingState
import com.example.edusync.common.NetworkUtils
import com.example.edusync.common.Resource
import com.example.edusync.data.local.SelectedScheduleStorage
import com.example.edusync.data.local.EncryptedSharedPreference
import com.example.edusync.data.remote.dto.TeacherInitialsResponse
import com.example.edusync.data.remote.dto.toDomain
import com.example.edusync.data.repository.schedule.ReminderRepository
import com.example.edusync.presentation.navigation.Destination
import com.example.edusync.presentation.navigation.Navigator
import com.example.edusync.domain.model.schedule.Day
import com.example.edusync.presentation.views.main.mainScreen.MainScreenState
import com.example.edusync.domain.model.schedule.PairInfo
import com.example.edusync.domain.model.schedule.PairItem
import com.example.edusync.domain.model.schedule.withReminders
import com.example.edusync.domain.repository.schedule.ScheduleRepository
import com.example.edusync.domain.use_case.group.GetGroupsByInstitutionIdUseCase
import com.example.edusync.domain.use_case.schedule.GetGroupScheduleUseCase
import com.example.edusync.domain.use_case.schedule.GetScheduleByTeacherUseCase
import com.example.edusync.domain.use_case.teachers.GetTeacherInitialsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainScreenViewModel(
    private val context: Context,
    private val navigator: Navigator,
    private val encryptedSharedPreference: EncryptedSharedPreference,
    private val getGroupsByInstitutionId: GetGroupsByInstitutionIdUseCase,
    private val getTeacherInitialsUseCase: GetTeacherInitialsUseCase,
    private val getGroupScheduleUseCase: GetGroupScheduleUseCase,
    private val getTeacherScheduleUseCase: GetScheduleByTeacherUseCase,
    private val scheduleRepository: ScheduleRepository,
    private val reminderRepository: ReminderRepository
) : ViewModel() {

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

    private val _isTeacherScheduleVisible = MutableStateFlow(false)
    val isTeacherScheduleVisible: StateFlow<Boolean> = _isTeacherScheduleVisible.asStateFlow()

    private val _teacherInitialsList = MutableStateFlow<List<TeacherInitialsResponse>>(emptyList())
    val teacherInitialsList: StateFlow<List<TeacherInitialsResponse>> = _teacherInitialsList

    private val _teacherId = MutableStateFlow<Int?>(null)

    private val _institutionId = MutableStateFlow<Int?>(null)
    val institutionId: StateFlow<Int?> = _institutionId

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val user = encryptedSharedPreference.getUser()

            _isTeacher.value = user?.isTeacher ?: false
            _institutionId.value = user?.institutionId
            _teacherId.value = encryptedSharedPreference.getTeacherId()

            if (user?.isTeacher == true) {
                if (_teacherId.value == null) {
                    // Если в SharedPreference нет teacherId, временно сохраняем user.id
                    encryptedSharedPreference.saveTeacherId(user.id)
                    _teacherId.value = user.id
                }

                getTeacherInitialsUseCase().collect { resource ->
                    if (resource is Resource.Success) {
                        val initialsList = resource.data ?: emptyList()
                        _teacherInitialsList.value = initialsList

                        val generatedInitials = generateTeacherInitials(user.fullName)
                        val matched = initialsList.firstOrNull {
                            normalizeInitials(it.initials) == normalizeInitials(generatedInitials)
                        }

                        if (matched != null) {
                            encryptedSharedPreference.saveTeacherId(matched.id)
                            _teacherId.value = matched.id
                            Log.d("TEACHER_ID", "Matched initials: ${matched.initials}, id=${matched.id}")
                        } else {
                            Log.w("TEACHER_ID", "No matching initials found for: $generatedInitials")
                        }
                    }
                }
            }
        }
    }

    fun setSelectedGroup(id: Int, name: String) {
        viewModelScope.launch {
            SelectedScheduleStorage.clearGroup()
            SelectedScheduleStorage.setGroup(id, name)
            _state.update {
                it.copy(
                    selectedGroup = name,
                    selectedTeacher = null,
                    scheduleLoading = LoadingState.Loading
                )
            }
            loadGroupSchedule(id)
            _isTeacherScheduleVisible.value = false
        }
    }

    fun setSelectedTeacher(id: Int, initials: String) {
        viewModelScope.launch {
            SelectedScheduleStorage.setTeacher(id, initials)
            _state.update {
                it.copy(
                    selectedTeacher = initials,
                    selectedGroup = null,
                    scheduleLoading = LoadingState.Loading
                )
            }
            loadTeacherSchedule(id)
            _isTeacherScheduleVisible.value = true
        }
    }

    private fun loadGroupSchedule(groupId: Int) {
        viewModelScope.launch {
            val hasInternet = NetworkUtils.hasInternetConnection(context)
            _state.update { it.copy(scheduleLoading = LoadingState.Loading) }

            if (!hasInternet) {
                val cached = scheduleRepository.getCachedGroupSchedule(groupId)
                if (cached != null) {
                    val reminders = reminderRepository.getReminders(groupId = groupId, teacherId = null)
                    _state.update {
                        it.copy(
                            schedule = cached.toDomain("Кэш").withReminders(reminders),
                            scheduleLoading = LoadingState.Success
                        )
                    }
                } else {
                    _state.update { it.copy(scheduleLoading = LoadingState.Empty) }
                }
                return@launch
            }

            try {
                getGroupScheduleUseCase(groupId).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            resource.data?.let { scheduleItems ->
                                scheduleRepository.saveGroupSchedule(groupId, scheduleItems)
                                val reminders = reminderRepository.getReminders(groupId, null)
                                _state.update {
                                    it.copy(
                                        schedule = scheduleItems.toDomain("Группа").withReminders(reminders),
                                        scheduleLoading = LoadingState.Success
                                    )
                                }
                            } ?: run {
                                _state.update { it.copy(scheduleLoading = LoadingState.Empty) }
                            }
                        }
                        is Resource.Error -> _state.update {
                            it.copy(scheduleLoading = LoadingState.Error(resource.message ?: ""))
                        }
                        is Resource.Loading -> {}
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(scheduleLoading = LoadingState.Error(e.message ?: "Ошибка загрузки"))
                }
            }
        }
    }

    private fun loadTeacherSchedule(teacherId: Int) {
        viewModelScope.launch {
            val hasInternet = NetworkUtils.hasInternetConnection(context)
            _state.update { it.copy(scheduleLoading = LoadingState.Loading) }

            if (!hasInternet) {
                val cached = scheduleRepository.getCachedTeacherSchedule(teacherId)
                if (cached != null) {
                    val reminders = reminderRepository.getReminders(groupId = null, teacherId = teacherId)
                    _state.update {
                        it.copy(
                            schedule = cached.toDomain("Кэш").withReminders(reminders),
                            scheduleLoading = LoadingState.Success
                        )
                    }
                } else {
                    _state.update { it.copy(scheduleLoading = LoadingState.Empty) }
                }
                return@launch
            }

            try {
                getTeacherScheduleUseCase(teacherId).collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            resource.data?.let { scheduleItems ->
                                scheduleRepository.saveTeacherSchedule(teacherId, scheduleItems)
                                val reminders = reminderRepository.getReminders(null, teacherId)
                                _state.update {
                                    it.copy(
                                        schedule = scheduleItems.toDomain("Преподаватель").withReminders(reminders),
                                        scheduleLoading = LoadingState.Success
                                    )
                                }
                            } ?: run {
                                _state.update { it.copy(scheduleLoading = LoadingState.Empty) }
                            }
                        }
                        is Resource.Error -> _state.update {
                            it.copy(scheduleLoading = LoadingState.Error(resource.message ?: ""))
                        }
                        is Resource.Loading -> {}
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(scheduleLoading = LoadingState.Error(e.message ?: "Ошибка загрузки"))
                }
            }
        }
    }

    fun getUser() = encryptedSharedPreference.getUser()

    fun getTeacherId() = encryptedSharedPreference.getTeacherId()

    suspend fun getGroupNameById(groupId: Int?): String? {
        if (groupId == null) return null
        return getGroupsByInstitutionId(_institutionId.value ?: return null)
            .firstOrNull { it is Resource.Success }
            ?.let { (it as Resource.Success).data?.firstOrNull { group -> group.id == groupId }?.name }
    }

    fun goToSearch(isTeacherMode: Boolean) {
        viewModelScope.launch {
            val institutionId = _institutionId.value ?: return@launch
            navigator.navigate(Destination.SearchScreen(isTeacherMode, institutionId))
        }
    }

    fun clearTeacher() {
        viewModelScope.launch {
            SelectedScheduleStorage.clearTeacher() // Очистка локального сохранения
            _state.update {
                it.copy(
                    selectedTeacher = null,
                    schedule = null,
                    scheduleLoading = LoadingState.Loading // Можно показать индикатор загрузки
                )
            }
            _isTeacherScheduleVisible.value = false
        }
    }

    fun clearGroup() {
        viewModelScope.launch {
            SelectedScheduleStorage.clearGroup() // Очистка локального сохранения
            _state.update {
                it.copy(
                    selectedGroup = null,
                    schedule = null,
                    scheduleLoading = LoadingState.Loading
                )
            }
            _isTeacherScheduleVisible.value = false
        }
    }

    private fun normalizeInitials(initials: String): String {
        return initials.trim().replace(" +".toRegex(), " ")
    }

    fun generateTeacherInitials(fullName: String): String {
        val parts = fullName.split(" ").filter { it.isNotBlank() }
        return when (parts.size) {
            3 -> "${parts[0]} ${parts[1].firstOrNull()}.${parts[2].firstOrNull()}."
            2 -> "${parts[0]} ${parts[1].firstOrNull()}."
            else -> fullName
        }.let(::normalizeInitials)
    }

    private fun sortPairs(day: Day): Day {
        return day.copy(pairs = day.pairs.sortedBy { it.isoDateStart })
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

    fun createNewPair(date: String): PairItem {
        val currentTime =
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Calendar.getInstance().time)
        return PairItem(
            time = "$currentTime - $currentTime",
            isoDateStart = "$date $currentTime:00",
            isoDateEnd = "$date $currentTime:00",
            pairInfo = listOf(
                PairInfo(
                    doctrine = "",
                    teacher = "",
                    group = "",
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
            val groupId = SelectedScheduleStorage.selectedGroupId
            val teacherId = SelectedScheduleStorage.selectedTeacherId

            reminderRepository.saveReminder(
                isoDateTime = pair.isoDateStart,
                groupId = groupId,
                teacherId = teacherId,
                text = reminderText
            )

            val updatedPair = pair.copy(
                pairInfo = pair.pairInfo.map { it.copy(warn = reminderText) }
            )

            _state.update { currentState ->
                currentState.copy(
                    schedule = currentState.schedule?.copy(
                        days = currentState.schedule.days.map { day ->
                            day.copy(pairs = day.pairs.map {
                                if (it.isoDateStart == updatedPair.isoDateStart) updatedPair else it
                            })
                        }
                    )
                )
            }
        }
    }
}