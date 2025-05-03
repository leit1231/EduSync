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
import com.example.edusync.data.remote.dto.ScheduleUpdateRequest
import com.example.edusync.data.remote.dto.SubjectResponse
import com.example.edusync.data.remote.dto.TeacherInitialsResponse
import com.example.edusync.data.remote.dto.toDomain
import com.example.edusync.data.repository.schedule.ReminderRepository
import com.example.edusync.domain.model.group.Group
import com.example.edusync.presentation.navigation.Destination
import com.example.edusync.presentation.navigation.Navigator
import com.example.edusync.presentation.views.main.mainScreen.MainScreenState
import com.example.edusync.domain.model.schedule.PairInfo
import com.example.edusync.domain.model.schedule.PairItem
import com.example.edusync.domain.model.schedule.withReminders
import com.example.edusync.domain.repository.schedule.ScheduleRepository
import com.example.edusync.domain.use_case.group.GetGroupsByInstitutionIdUseCase
import com.example.edusync.domain.use_case.schedule.CreateScheduleUseCase
import com.example.edusync.domain.use_case.schedule.DeleteScheduleUseCase
import com.example.edusync.domain.use_case.schedule.GetGroupScheduleUseCase
import com.example.edusync.domain.use_case.schedule.GetScheduleByTeacherUseCase
import com.example.edusync.domain.use_case.schedule.UpdateScheduleUseCase
import com.example.edusync.domain.use_case.subject.GetSubjectsByGroupUseCase
import com.example.edusync.domain.use_case.teachers.GetTeacherInitialsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainScreenViewModel(
    private val context: Context,
    private val navigator: Navigator,
    private val scheduleRepository: ScheduleRepository,
    private val encryptedSharedPreference: EncryptedSharedPreference,
    private val getGroupsByInstitutionId: GetGroupsByInstitutionIdUseCase,
    private val getTeacherInitialsUseCase: GetTeacherInitialsUseCase,
    private val getGroupScheduleUseCase: GetGroupScheduleUseCase,
    private val getTeacherScheduleUseCase: GetScheduleByTeacherUseCase,
    private val reminderRepository: ReminderRepository,
    private val createScheduleUseCase: CreateScheduleUseCase,
    private val updateScheduleUseCase: UpdateScheduleUseCase,
    private val deleteScheduleUseCase: DeleteScheduleUseCase,
    private val getSubjectsByGroupUseCase: GetSubjectsByGroupUseCase
) : ViewModel() {

    private val _isAllScheduleVisible = MutableStateFlow(false)
    val isAllScheduleVisible: StateFlow<Boolean> = _isAllScheduleVisible.asStateFlow()

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode

    private val _selectedPair = MutableStateFlow<PairItem?>(null)
    val selectedPair: StateFlow<PairItem?> = _selectedPair

    private val _state = MutableStateFlow(MainScreenState())
    val state = _state.asStateFlow()

    private val _isTeacherScheduleVisible = MutableStateFlow(false)
    val isTeacherScheduleVisible: StateFlow<Boolean> = _isTeacherScheduleVisible.asStateFlow()

    private val _teacherInitialsList = MutableStateFlow<List<TeacherInitialsResponse>>(emptyList())
    val teacherInitialsList: StateFlow<List<TeacherInitialsResponse>> = _teacherInitialsList

    private val _teacherId = MutableStateFlow<Int?>(null)

    private val _institutionId = MutableStateFlow<Int?>(null)
    val institutionId: StateFlow<Int?> = _institutionId

    private val _allGroups = MutableStateFlow<List<Group>>(emptyList())
    val allGroups: StateFlow<List<Group>> = _allGroups

    private val _subjectList = MutableStateFlow<List<SubjectResponse>>(emptyList())
    val subjectList: StateFlow<List<SubjectResponse>> = _subjectList

    private val _subjectNames = MutableStateFlow<List<String>>(emptyList())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val user = encryptedSharedPreference.getUser()

            user?.institutionId?.let { institutionId ->
                loadGroupsFromDb(institutionId)
                loadTeacherInitials()
            }

            _institutionId.value = user?.institutionId
            _teacherId.value = encryptedSharedPreference.getTeacherId()

            if (user?.isTeacher == true) {
                if (_teacherId.value == null) {
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
            loadSubjects(id)
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

    private fun loadCurrentSchedule() {
        val groupId = SelectedScheduleStorage.selectedGroupId
        val teacherId = SelectedScheduleStorage.selectedTeacherId

        when {
            groupId != null -> loadGroupSchedule(groupId)
            teacherId != null -> loadTeacherSchedule(teacherId)
            else -> {}
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
            SelectedScheduleStorage.clearTeacher()
            _state.update {
                it.copy(
                    selectedTeacher = null,
                    schedule = null,
                    scheduleLoading = LoadingState.Loading
                )
            }
            _isTeacherScheduleVisible.value = false
        }
    }

    fun clearGroup() {
        viewModelScope.launch {
            SelectedScheduleStorage.clearGroup()
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

    private fun parseLocal(dateTime: String, onlyDate: Boolean = false): Date {
        val pattern = if (onlyDate) {
            if (dateTime.contains("T")) "yyyy-MM-dd'T'HH:mm:ss'Z'" else "yyyy-MM-dd HH:mm:ss"
        } else {
            if (dateTime.contains("T")) "yyyy-MM-dd'T'HH:mm:ss'Z'" else "yyyy-MM-dd HH:mm:ss"
        }
        return SimpleDateFormat(pattern, Locale.getDefault()).parse(dateTime)!!
    }

    fun loadSubjects(groupId: Int) {
        viewModelScope.launch {
            getSubjectsByGroupUseCase(groupId).onSuccess { subjects ->
                _subjectList.value = subjects
                _subjectNames.value = subjects.map { it.name }
            }.onFailure {
                _subjectList.value = emptyList()
                _subjectNames.value = emptyList()
            }
        }
    }

    private fun loadGroupsFromDb(institutionId: Int) {
        viewModelScope.launch {
            getGroupsByInstitutionId(institutionId).collect { result ->
                if (result is Resource.Success) {
                    _allGroups.value = result.data ?: emptyList()
                }
            }
        }
    }

    private fun loadTeacherInitials() {
        viewModelScope.launch {
            getTeacherInitialsUseCase().collect { result ->
                if (result is Resource.Success) {
                    _teacherInitialsList.value = result.data ?: emptyList()
                }
            }
        }
    }

    suspend fun addPair(pair: PairItem): Result<Unit> {
        return try {
            val info = pair.pairInfo.firstOrNull() ?: return Result.failure(Exception("Нет данных"))
            val groupId = allGroups.value.firstOrNull { it.name.trim().equals(info.group.trim(), true) }?.id
            val subjectId = subjectList.value.firstOrNull { it.name.trim().equals(info.doctrine.trim(), true) }?.id
            val teacherId = if (info.teacher.isNotBlank()) {
                teacherInitialsList.value.firstOrNull {
                    normalizeInitials(it.initials) == normalizeInitials(info.teacher)
                }?.id
            } else null

            if (groupId == null || subjectId == null || teacherId == null) {
                Log.e("SCHEDULE", "Invalid data. Teacher: '${info.teacher}' | available: ${teacherInitialsList.value.joinToString { it.initials }}")
                return Result.failure(Exception("Некорректные данные: группа/предмет/преподаватель"))
            }

            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val request = ScheduleUpdateRequest(
                group_id = groupId,
                subject_id = subjectId,
                teacher_initials_id = teacherId,
                date = formatter.format(parseLocal(pair.isoDateStart, true)),
                start_time = formatter.format(parseLocal(pair.isoDateStart)),
                end_time = formatter.format(parseLocal(pair.isoDateEnd)),
                classroom = "${info.auditoria}/1",
                pair_number = info.number
            )

            val result = createScheduleUseCase(request)
            if (result.isSuccess) loadCurrentSchedule()
            result
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePair(pair: PairItem): Result<Unit> {
        return try {
            val scheduleId = pair.scheduleId ?: return Result.failure(Exception("Нет ID пары"))
            val info = pair.pairInfo.firstOrNull() ?: return Result.failure(Exception("Нет информации"))
            val groupId = allGroups.value.firstOrNull { it.name == info.group }?.id
            val subjectId = subjectList.value.firstOrNull { it.name == info.doctrine }?.id
            val teacherId = if (info.teacher.isNotBlank()) {
                teacherInitialsList.value.firstOrNull {
                    normalizeInitials(it.initials) == normalizeInitials(info.teacher)
                }?.id
            } else null

            if (groupId == null || subjectId == null || teacherId == null) {
                Log.e("SCHEDULE", "Invalid update. Teacher: '${info.teacher}' | available: ${teacherInitialsList.value.joinToString { it.initials }}")
                return Result.failure(Exception("Некорректные данные для обновления"))
            }

            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val request = ScheduleUpdateRequest(
                group_id = groupId,
                subject_id = subjectId,
                teacher_initials_id = teacherId,
                date = formatter.format(parseLocal(pair.isoDateStart, true)),
                start_time = formatter.format(parseLocal(pair.isoDateStart)),
                end_time = formatter.format(parseLocal(pair.isoDateEnd)),
                classroom = "${info.auditoria}/1",
                pair_number = info.number
            )

            val result = updateScheduleUseCase(scheduleId, request)
            if (result.isSuccess) loadCurrentSchedule()
            result
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun deletePair(pair: PairItem) {
        viewModelScope.launch {
            val scheduleId = pair.scheduleId ?: return@launch
            deleteScheduleUseCase(scheduleId).onSuccess {
                loadCurrentSchedule()
            }
        }
    }

    fun createNewPair(date: String): PairItem {
        return PairItem(
            time = "",
            isoDateStart = "$date ",
            isoDateEnd = "$date ",
            scheduleId = null,
            pairInfo = listOf(
                PairInfo(
                    doctrine = "",
                    teacher = "",
                    group = "",
                    auditoria = "",
                    corpus = "",
                    number = 0,
                    start = "",
                    end = "",
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