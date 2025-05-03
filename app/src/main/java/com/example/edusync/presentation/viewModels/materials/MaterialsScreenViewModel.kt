package com.example.edusync.presentation.viewModels.materials

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusync.common.Resource
import com.example.edusync.data.local.EncryptedSharedPreference
import com.example.edusync.data.remote.dto.ChatResponse
import com.example.edusync.domain.model.group.Group
import com.example.edusync.domain.use_case.chat.GetChatsUseCase
import com.example.edusync.domain.use_case.chat.JoinChatByInviteUseCase
import com.example.edusync.domain.use_case.group.GetGroupsByInstitutionIdUseCase
import com.example.edusync.presentation.navigation.Destination
import com.example.edusync.presentation.navigation.Navigator
import kotlinx.coroutines.launch

class MaterialsScreenViewModel(
    private val getChatsUseCase: GetChatsUseCase,
    private val prefs: EncryptedSharedPreference,
    private val getGroupsUseCase: GetGroupsByInstitutionIdUseCase,
    private val joinChatByInviteUseCase: JoinChatByInviteUseCase,
    private val navigator: Navigator
) : ViewModel() {

    private val _chats = mutableStateListOf<ChatResponse>()

    private val _groups = mutableStateListOf<Group>()
    val groups: List<Group> get() = _groups

    private val groupIdToName = mutableMapOf<Int, String>()

    private val _searchQuery = mutableStateOf("")
    val searchQuery: String get() = _searchQuery.value

    private val _filteredChats = mutableStateOf<List<ChatResponse>>(emptyList())
    val filteredChats: State<List<ChatResponse>> get() = _filteredChats


    init {
        loadChats()
        loadGroups()
    }

    fun getGroupNameById(groupId: Int): String {
        Log.d("ChatItem", "groupId: $groupId → groupName: ${groupIdToName[groupId]}")
        return groupIdToName[groupId] ?: "Неизвестная группа"
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        filterChats()
    }

    private fun filterChats() {
        val query = _searchQuery.value.lowercase()
        _filteredChats.value = _chats.filter {
            it.subject_name.contains(query, ignoreCase = true)
        }
    }


    private fun loadGroups() {
        viewModelScope.launch {
            val user = prefs.getUser()
            val institutionId = user?.institutionId

            if (institutionId != null) {
                getGroupsUseCase(institutionId).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            val groups = result.data ?: emptyList()
                            setGroups(groups)
                        }
                        is Resource.Error -> {
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    fun joinByInvite(inviteCode: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            joinChatByInviteUseCase(inviteCode).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        loadChats()
                        onSuccess()
                    }
                    is Resource.Error -> {
                        onError(result.message ?: "Не удалось присоединиться")
                    }
                    else -> {}
                }
            }
        }
    }

    private fun loadChats() {
        viewModelScope.launch {
            getChatsUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val resultChats = result.data ?: emptyList()
                        _chats.clear()
                        _chats.addAll(resultChats)
                        filterChats()
                    }

                    is Resource.Error -> {}
                    else -> {}
                }
            }
        }
    }


    private fun setGroups(groupList: List<Group>) {
        _groups.clear()
        _groups.addAll(groupList)
        groupIdToName.clear()
        groupIdToName.putAll(groupList.associateBy({ it.id }, { it.name }))
    }

    fun goToGroup(group: String) {
        viewModelScope.launch {
            navigator.navigate(
                destination = Destination.GroupScreen(group)
            )
        }
    }

    fun goToCreateGroup() {
        viewModelScope.launch {
            navigator.navigate(destination = Destination.CreateGroupScreen)
        }
    }
}