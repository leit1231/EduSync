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
import com.example.edusync.data.remote.webSocket.WebSocketManager
import com.example.edusync.domain.model.chats.toChatInfo
import com.example.edusync.domain.model.group.Group
import com.example.edusync.domain.use_case.chat.GetChatsUseCase
import com.example.edusync.domain.use_case.chat.JoinChatByInviteUseCase
import com.example.edusync.domain.use_case.group.GetGroupsByInstitutionIdUseCase
import com.example.edusync.presentation.navigation.Destination
import com.example.edusync.presentation.navigation.Navigator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

    private var disconnectJob: Job? = null
    private val chatIds: MutableSet<Int> = mutableSetOf()

    var isLoading = mutableStateOf(false)
    var hasError = mutableStateOf(false)

    init {
        loadGroups()
        loadChats()
    }

    fun connectWebSocketIfNeeded() {
        if (WebSocketManager.isConnected) return
        val token = prefs.getAccessToken() ?: return
        WebSocketManager.connect(token)
        chatIds.forEach { WebSocketManager.subscribe(it) }
    }

    fun scheduleDisconnectIfIdle() {
        disconnectJob?.cancel()
        disconnectJob = viewModelScope.launch {
            delay(3 * 60 * 1000)
            WebSocketManager.disconnect()
        }
    }

    fun disconnectWebSocket() {
        disconnectJob?.cancel()
        WebSocketManager.disconnect()
    }

    override fun onCleared() {
        super.onCleared()
        disconnectWebSocket()
    }

    fun handleInviteCodeIfNeeded(inviteCode: String) {
        if (inviteCode.isBlank() || prefs.wasInviteCodeHandled(inviteCode)) return

        joinByInvite(
            inviteCode = inviteCode,
            onSuccess = {
                prefs.setInviteCodeHandled(inviteCode)
            },
            onError = {
                prefs.setInviteCodeHandled(inviteCode)
            }
        )
    }

    private fun onChatsLoaded(ids: List<Int>) {
        chatIds.clear()
        chatIds.addAll(ids)
        ids.forEach { WebSocketManager.subscribe(it) }
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

    fun reloadChats() {
        loadChats()
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

    fun reloadChatsSilently() {
        viewModelScope.launch {
            getChatsUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val newChats = result.data ?: emptyList()
                        if (newChats != _chats) {
                            _chats.clear()
                            _chats.addAll(newChats)
                            prefs.saveChats(newChats.map { it.toChatInfo() })
                            filterChats()
                            onChatsLoaded(newChats.map { it.id })
                        }
                        hasError.value = false
                    }

                    is Resource.Error -> {
                        hasError.value = true
                    }

                    else -> {}
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
            isLoading.value = true
            hasError.value = false

            getChatsUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val resultChats = result.data ?: emptyList()
                        _chats.clear()
                        _chats.addAll(resultChats)
                        prefs.saveChats(resultChats.map { it.toChatInfo() })
                        filterChats()
                        onChatsLoaded(resultChats.map { it.id })

                        isLoading.value = false
                        hasError.value = false
                    }

                    is Resource.Error -> {
                        isLoading.value = false
                        hasError.value = true
                        _chats.clear()
                        _filteredChats.value = emptyList()
                    }

                    else -> {
                        isLoading.value = false
                    }
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

    fun goToGroup(chatId: Int, group: String) {
        viewModelScope.launch {
            navigator.navigate(
                destination = Destination.GroupScreen(chatId, group)
            )
        }
    }

    fun goToCreateGroup() {
        viewModelScope.launch {
            navigator.navigate(destination = Destination.CreateGroupScreen)
        }
    }
}