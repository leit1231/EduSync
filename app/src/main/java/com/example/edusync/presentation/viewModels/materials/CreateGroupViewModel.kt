package com.example.edusync.presentation.viewModels.materials

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusync.common.Resource
import com.example.edusync.data.local.EncryptedSharedPreference
import com.example.edusync.data.remote.dto.CreateChatRequest
import com.example.edusync.domain.model.group.Group
import com.example.edusync.domain.use_case.chat.CreateChatUseCase
import com.example.edusync.domain.use_case.group.GetGroupsByInstitutionIdUseCase
import com.example.edusync.domain.use_case.subject.GetSubjectsByGroupUseCase
import com.example.edusync.presentation.navigation.Destination
import com.example.edusync.presentation.navigation.Navigator
import com.example.edusync.presentation.views.materials.group.CreateGroupState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateGroupViewModel(
    private val navigator: Navigator,
    private val createChatUseCase: CreateChatUseCase,
    private val getSubjectsByGroupUseCase: GetSubjectsByGroupUseCase,
    private val getGroupsByInstitutionIdUseCase: GetGroupsByInstitutionIdUseCase,
    private val prefs: EncryptedSharedPreference
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateGroupState())
    val uiState: StateFlow<CreateGroupState> = _uiState

    private val _groups = MutableStateFlow<List<Group>>(emptyList())
    val groups: StateFlow<List<Group>> = _groups

    private val _subjects = MutableStateFlow<Map<String, Int>>(emptyMap())
    val subjectNames: List<String> get() = _subjects.value.keys.toList()

    var expandedGroup = MutableStateFlow(false)
    var expandedSubject = MutableStateFlow(false)

    init {
        loadGroups()
    }

    private fun loadGroups() {
        val institutionId = prefs.getUser()?.institutionId ?: return
        viewModelScope.launch {
            getGroupsByInstitutionIdUseCase(institutionId).collect { result ->
                if (result is Resource.Success) {
                    _groups.value = result.data ?: emptyList()
                }
            }
        }
    }

    fun onGroupSelected(groupName: String) {
        _uiState.update { it.copy(selectedGroup = groupName) }
        val groupId = _groups.value.firstOrNull { it.name == groupName }?.id ?: return

        viewModelScope.launch {
            getSubjectsByGroupUseCase(groupId).onSuccess { subjects ->
                _subjects.value = subjects.associate { it.name to it.id }
            }.onFailure {
                _subjects.value = emptyMap()
            }
        }
    }

    fun onSubjectSelected(subject: String) {
        _uiState.update { it.copy(selectedSubject = subject) }
    }

    fun goBack() {
        viewModelScope.launch { navigator.navigateUp() }
    }

    suspend fun goToMaterials() {
        navigator.navigate(
            destination = Destination.MaterialsScreen,
            navOptions = {
                popUpTo(Destination.CreateGroupScreen) {
                    inclusive = true
                }
            }
        )
    }

    fun createChat(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val group = _groups.value.firstOrNull { it.name == uiState.value.selectedGroup }
        val subjectId = _subjects.value[uiState.value.selectedSubject]

        if (group == null || subjectId == null) {
            onError("Выберите группу и предмет")
            return
        }

        val request = CreateChatRequest(group_id = group.id, subject_id = subjectId)
        viewModelScope.launch {
            createChatUseCase(request).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        Log.d("CreateChat", "Chat created: ${result.data}")
                        result.data?.chat_info?.invite_link?.let(onSuccess)
                    }
                    is Resource.Error -> onError(result.message ?: "Ошибка создания")
                    else -> {}
                }
            }
        }
    }
}