package com.example.edusync.presentation.viewModels.group

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.edusync.common.FileUtil
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.edusync.common.Constants.BASE_URL
import com.example.edusync.common.Resource
import com.example.edusync.data.remote.dto.CreatePollRequest
import com.example.edusync.data.remote.dto.MessageDto
import com.example.edusync.data.remote.webSocket.WebSocketController
import com.example.edusync.domain.model.chats.ChatUser
import com.example.edusync.domain.model.message.FileAttachment
import com.example.edusync.domain.model.message.Message
import com.example.edusync.domain.model.message.PollData
import com.example.edusync.domain.use_case.chat.DeleteChatUseCase
import com.example.edusync.domain.use_case.chat.GetChatParticipantsUseCase
import com.example.edusync.domain.use_case.chat.LeaveChatUseCase
import com.example.edusync.domain.use_case.chat.RefreshInviteCodeUseCase
import com.example.edusync.domain.use_case.chat.RemoveChatParticipantUseCase
import com.example.edusync.domain.use_case.favorite.AddToFavoritesUseCase
import com.example.edusync.domain.use_case.favorite.GetFavoritesUseCase
import com.example.edusync.domain.use_case.favorite.RemoveFromFavoritesUseCase
import com.example.edusync.domain.use_case.file.GetFileByIdUseCase
import com.example.edusync.domain.use_case.message.DeleteMessageUseCase
import com.example.edusync.domain.use_case.message.EditMessageUseCase
import com.example.edusync.domain.use_case.message.MessagePagingUseCase
import com.example.edusync.domain.use_case.message.ReplyToMessageUseCase
import com.example.edusync.domain.use_case.message.SearchMessagesUseCase
import com.example.edusync.domain.use_case.message.SendMessageUseCase
import com.example.edusync.domain.use_case.poll.CreatePollUseCase
import com.example.edusync.domain.use_case.poll.DeletePollUseCase
import com.example.edusync.domain.use_case.poll.GetPollsUseCase
import com.example.edusync.domain.use_case.poll.UnvotePollUseCase
import com.example.edusync.domain.use_case.poll.VotePollUseCase
import com.example.edusync.presentation.navigation.Destination
import com.example.edusync.presentation.navigation.Navigator
import com.example.edusync.presentation.views.group.components.chatBubble.ChatItem
import com.example.edusync.presentation.views.group.components.fileAttachment.isImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class GroupViewModel(
    private val navigator: Navigator,

    private val getChatParticipantsUseCase: GetChatParticipantsUseCase,
    private val removeChatParticipantUseCase: RemoveChatParticipantUseCase,

    private val refreshInviteCodeUseCase: RefreshInviteCodeUseCase,
    private val deleteChatUseCase: DeleteChatUseCase,
    private val leaveChatUseCase: LeaveChatUseCase,

    private val searchMessagesUseCase: SearchMessagesUseCase,
    private val replyToMessageUseCase: ReplyToMessageUseCase,
    private val deleteMessageUseCase: DeleteMessageUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val editMessageUseCase: EditMessageUseCase,
    private val messagePagingUseCase: MessagePagingUseCase,
    private val getFileByIdUseCase: GetFileByIdUseCase,

    private val createPollUseCase: CreatePollUseCase,
    private val votePollUseCase: VotePollUseCase,
    private val unvotePollUseCase: UnvotePollUseCase,
    private val getPollsUseCase: GetPollsUseCase,
    private val deletePollUseCase: DeletePollUseCase,

    private val addToFavoritesUseCase: AddToFavoritesUseCase,
    private val removeFromFavoritesUseCase: RemoveFromFavoritesUseCase,
    private val getFavoritesUseCase: GetFavoritesUseCase
) : ViewModel() {

    private val _highlightedMessage = MutableLiveData<Message?>()
    val highlightedMessage: LiveData<Message?> = _highlightedMessage

    private val _messages = MutableStateFlow<List<Message>>(emptyList())

    private val _realtimeMessages = MutableStateFlow<List<Message>>(emptyList())

    private val _attachedFiles = MutableLiveData<List<FileAttachment>>(emptyList())
    val attachedFiles: LiveData<List<FileAttachment>> = _attachedFiles

    private val _replyMessage = MutableLiveData<Message?>()
    val replyMessage: LiveData<Message?> = _replyMessage

    private val _editingMessage = MutableLiveData<Message?>()
    val editingMessage: LiveData<Message?> = _editingMessage

    private val _selectedFiles = MutableStateFlow<Set<FileAttachment>>(emptySet())
    val selectedFiles: StateFlow<Set<FileAttachment>> = _selectedFiles

    private val _isInSelectionMode = MutableLiveData(false)
    val isInSelectionMode: LiveData<Boolean> = _isInSelectionMode

    private val _participants = MutableLiveData<List<ChatUser>>(emptyList())
    val participants: LiveData<List<ChatUser>> = _participants

    private val _inviteCode = MutableLiveData<String?>()
    val inviteCode: LiveData<String?> = _inviteCode

    private var currentUserId: Int = -1
    private val pendingMessages = mutableListOf<Triple<String, Int, List<FileAttachment>>>()

    val messageFlow = MutableStateFlow<PagingData<Message>>(PagingData.empty())

    private val _polls = MutableStateFlow<List<PollData>>(emptyList())

    private val _chatItems = MutableStateFlow<List<ChatItem>>(emptyList())
    val chatItems: StateFlow<List<ChatItem>> = _chatItems

    private val _isMessagesLoaded = MutableStateFlow(false)

    private val _favoriteIds = MutableStateFlow<Set<Int>>(emptySet())
    val favoriteIds: StateFlow<Set<Int>> = _favoriteIds

    private var isPollsLoaded = false
    private var isMessagesLoaded = false
    private var isParticipantsLoaded = false

    init {
        viewModelScope.launch {
            getFavoritesUseCase().collect { result ->
                if (result is Resource.Success) {
                    val ids = result.data.orEmpty().map { it.id }
                    _favoriteIds.value = ids.toSet()
                }
            }
            selectedFiles.collect { files ->
                if (files.isEmpty()) {
                    exitSelectionMode()
                }
            }
        }
    }

    fun initChatSession(chatId: Int, userId: Int, context: Context) {
        updateCurrentUserId(userId)
        loadParticipants(chatId)
        loadPagedMessages(chatId, context = context)
        loadPolls(chatId)
    }

    private fun updateChatItems() {
        val teacher = participants.value?.firstOrNull { it.isTeacher }

        val allMessages = (_realtimeMessages.value + _messages.value)
            .distinctBy { it.id }
            .toMutableList()

        val pollMessages = _polls.value
            .filter { it.timestampMillis > 0 }
            .filterNot { poll -> allMessages.any { it.pollData?.id == poll.id } }
            .map { poll ->
                Message(
                    id = -poll.id,
                    text = null,
                    sender = teacher?.fullName ?: "Преподаватель",
                    timestamp = poll.createdAt,
                    timestampMillis = poll.timestampMillis,
                    isMe = teacher?.id == currentUserId,
                    pollData = poll,
                    files = emptyList()
                )
            }

        allMessages.addAll(pollMessages)

        val messageItems = allMessages
            .sortedByDescending { it.timestampMillis }
            .map { ChatItem.MessageItem(it) }

        _chatItems.value = messageItems
    }

    fun updateVisibleMessages(pagedMessages: List<Message>) {
        _messages.value = pagedMessages
        updateChatItems()
    }

    fun toggleFileSelection(file: FileAttachment, context: Context) {
        val mimeType = getMimeType(file.uri, context)
        if (mimeType.startsWith("application")) {
            _selectedFiles.update { current ->
                current.toMutableSet().apply {
                    if (contains(file)) remove(file) else add(file)
                }
            }
        }
    }

    fun addOrRemoveSelectedFavorites() {
        viewModelScope.launch {
            val current = _favoriteIds.value.toSet()
            val selected = selectedFiles.value
                .filterNot { it.isImage() }
                .mapNotNull { it.id }

            val newSet = current.toMutableSet()

            selected.forEach { fileId ->
                launch {
                    if (current.contains(fileId)) {
                        removeFromFavoritesUseCase(fileId).collect { result ->
                            if (result is Resource.Success) {
                                newSet.remove(fileId)
                                _favoriteIds.value = newSet.toSet()
                            }
                        }
                    } else {
                        addToFavoritesUseCase(fileId).collect { result ->
                            if (result is Resource.Success) {
                                newSet.add(fileId)
                                _favoriteIds.value = newSet.toSet()
                            }
                        }
                    }
                }
            }
        }
    }

    fun updateMessageFromDto(dto: MessageDto) {
        _realtimeMessages.update { list ->
            list.map {
                if (it.id == dto.id) it.copy(text = dto.text, isEdited = true) else it
            }
        }
        updateChatItems()
    }


    fun deleteMessageLocally(messageId: Int) {
        _realtimeMessages.update { it.filterNot { msg -> msg.id == messageId } }
        _messages.update { it.filterNot { msg -> msg.id == messageId } }
        updateChatItems()
    }

    private fun loadParticipants(chatId: Int) {
        if (isParticipantsLoaded) return
        isParticipantsLoaded = true
        viewModelScope.launch {
            getChatParticipantsUseCase(chatId).collect { result ->
                if (result is Resource.Success) {
                    _participants.value = result.data ?: emptyList()

                    WebSocketController.register(chatId, this@GroupViewModel)
                    WebSocketController.subscribeToChat(chatId)

                    val toDeliver = pendingMessages.toList()
                    pendingMessages.clear()
                    toDeliver.forEach { (text, userId, files) ->
                        receiveMessage(text, null, userId, files)
                    }
                }
            }
        }
    }

    private fun updateCurrentUserId(id: Int) {
        currentUserId = id
    }

    fun resetChatSession() {
        isPollsLoaded = false
        isMessagesLoaded = false
        isParticipantsLoaded = false
    }

    fun removeParticipant(chatId: Int, userId: Int) {
        viewModelScope.launch {
            removeChatParticipantUseCase(chatId, userId).collect { result ->
                if (result is Resource.Success) {
                    loadParticipants(chatId)
                }
            }
        }
    }

    private suspend fun fetchFile(
        context: Context,
        fileId: Int,
        fileUrl: String? = null
    ): FileAttachment? {
        val rawName = getFileNameFromUrl(fileUrl) ?: "file_$fileId"
        val fileName = rawName.replaceFirst(Regex("^\\d+_"), "")
        val cachedFile = getCachedFile(fileId, fileName, context)

        if (cachedFile.exists()) {
            return FileAttachment(
                id = fileId,
                uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    cachedFile
                ),
                fileName = fileName,
                fileSize = formatFileSize(cachedFile.length())
            )
        }

        return try {
            var result: FileAttachment? = null
            getFileByIdUseCase(fileId, fileUrl).collect { resource ->
                if (resource is Resource.Success) {
                    val (body, _) = resource.data ?: return@collect
                    saveFileToCache(body, cachedFile)
                    result = FileAttachment(
                        id = fileId,
                        uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.provider",
                            cachedFile
                        ),
                        fileName = fileName,
                        fileSize = formatFileSize(cachedFile.length())
                    )
                }
            }
            result
        } catch (e: Exception) {
            Log.e("fetchFile", "Ошибка загрузки файла: ${e.message}")
            null
        }
    }

    private fun saveFileToCache(body: ResponseBody, file: File) {
        body.byteStream().use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }

    private fun getFileNameFromUrl(url: String?): String? {
        return url?.substringAfterLast("/")?.substringBefore("?")
    }

    fun clearRealtimeMessages() {
        _realtimeMessages.value = emptyList()
    }

    private fun loadPolls(chatId: Int) {
        if (isPollsLoaded) return
        isPollsLoaded = true
        viewModelScope.launch {
            getPollsUseCase(chatId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val updatedPolls = result.data?.map { dto ->
                            PollData(
                                id = dto.id,
                                question = dto.question,
                                createdAt = formatTimestamp(dto.created_at),
                                timestampMillis = parseIsoTimestampToMillis(dto.created_at),
                                options = dto.options.map {
                                    PollData.Option(
                                        id = it.id,
                                        text = it.text,
                                        votes = it.votes
                                    )
                                }
                            )
                        } ?: emptyList()

                        _polls.value = updatedPolls

                        val teacher = participants.value?.firstOrNull { it.isTeacher } ?: return@collect

                        updatedPolls.forEach { poll ->
                            receiveMessage(
                                text = "",
                                sender = teacher.fullName,
                                userId = teacher.id,
                                files = emptyList(),
                                pollData = poll
                            )
                        }
                    }

                    is Resource.Error -> {
                        Log.e("GroupVM", "Ошибка загрузки опросов: ${result.message}")
                    }

                    else -> Unit
                }
            }
        }
    }

    fun refreshInviteCode(
        chatId: Int,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            refreshInviteCodeUseCase(chatId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val joinCode = result.data?.chat?.join_code
                        Log.d("Fucking_Code", "${result.data?.chat}")
                        if (joinCode != null) {
                            _inviteCode.value = joinCode
                            onSuccess(joinCode)
                        } else {
                            onError("Код не найден")
                        }
                    }

                    is Resource.Error -> {
                        onError(result.message ?: "Ошибка получения кода")
                    }

                    else -> {}
                }
            }
        }
    }


    fun setInviteCode(code: String) {
        _inviteCode.value = code
    }

    fun clearInviteCode() {
        _inviteCode.value = null
    }

    fun deleteChat(chatId: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            deleteChatUseCase(chatId).collect { result ->
                when (result) {
                    is Resource.Success -> onSuccess()
                    is Resource.Error -> onError(result.message ?: "Ошибка удаления чата")
                    else -> {}
                }
            }
        }
    }

    fun leaveChat(chatId: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            leaveChatUseCase(chatId).collect { result ->
                when (result) {
                    is Resource.Success -> onSuccess()
                    is Resource.Error -> onError(result.message ?: "Ошибка выхода из чата")
                    else -> {}
                }
            }
        }
    }

    private fun loadPagedMessages(chatId: Int, query: String? = null, context: Context) {
        if (isMessagesLoaded) return
        isMessagesLoaded = true
        viewModelScope.launch {
            _isMessagesLoaded.value = false

            messagePagingUseCase.getMessages(chatId, query)
                .flow
                .map { dtoList -> dtoList.map { dto -> enrichMessageFromDto(dto, context) } }
                .cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    messageFlow.value = pagingData
                    _isMessagesLoaded.value = true
                }
        }
    }

    private suspend fun enrichMessageFromDto(dto: MessageDto, context: Context): Message {
        WebSocketController.mapMessageIdToChat(dto.id, dto.chat_id)

        val files = dto.files?.mapNotNull { file ->
            fetchFile(context, file.id, file.file_url)
        } ?: emptyList()

        return mapDtoToMessage(dto).copy(files = files)
    }



    fun replyToMessage(chatId: Int, parentId: Int, text: String, context: Context) {
        viewModelScope.launch {
            Log.d("ReplyTest", "Replying to messageId=$parentId with text=$text")
            replyToMessageUseCase(chatId, parentId, text).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _replyMessage.value = null
                    }

                    is Resource.Error -> {
                        Toast.makeText(
                            context,
                            result.message ?: "Ошибка ответа",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {}
                }
            }
        }
    }

    fun editMessage(
        chatId: Int,
        messageId: Int,
        newText: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            editMessageUseCase(chatId, messageId, newText).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val updatedList = _messages.value.map {
                            if (it.id == messageId) it.copy(text = newText, isEdited = true) else it
                        }
                        _messages.value = updatedList

                        _realtimeMessages.update { list ->
                            list.map {
                                if (it.id == messageId) it.copy(
                                    text = newText,
                                    isEdited = true
                                ) else it
                            }
                        }

                        updateChatItems()
                        _editingMessage.value = null
                        onSuccess()
                    }

                    is Resource.Error -> onError(result.message ?: "Ошибка редактирования")
                    else -> {}
                }
            }
        }
    }

    private fun getCachedFile(fileId: Int, originalFileName: String, context: Context): File {
        val cacheDir = File(context.cacheDir, "cached_files").apply { mkdirs() }
        return File(cacheDir, "$fileId-$originalFileName")
    }

    private fun formatTimestamp(isoString: String): String {
        return try {
            val trimmed = isoString.substringBefore("Z")
                .takeWhile { it != '.' } +
                    "." + isoString.substringAfter(".", "000000").padEnd(6, '0').take(6) +
                    "Z"

            val inputFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(trimmed) ?: return isoString

            formatTimestamp(date)
        } catch (e: Exception) {
            isoString
        }
    }

    fun exitSearchMode(chatId: Int, context: Context) {
        _messages.value = emptyList()
        _realtimeMessages.value = emptyList()
        isMessagesLoaded = false
        _isMessagesLoaded.value = false

        viewModelScope.launch {
            loadPagedMessages(chatId = chatId, query = null, context = context)
        }
    }


    private fun formatTimestamp(date: Date): String {
        return try {
            val day = SimpleDateFormat("d", Locale.getDefault()).format(date)
            val monthIndex = SimpleDateFormat("M", Locale.getDefault()).format(date).toInt()
            val monthNames = listOf(
                "", "янв", "фев", "мар", "апр", "мая", "июня",
                "июля", "авг", "сен", "окт", "ноя", "дек"
            )
            val month = monthNames.getOrNull(monthIndex) ?: ""
            val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)

            "$day $month, $time"
        } catch (e: Exception) {
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
        }
    }

    private fun resolveFileUri(fileUrl: String): Uri {
        val cleaned = if (fileUrl.startsWith("http")) {
            fileUrl
        } else {
            "${BASE_URL.trimEnd('/')}/${fileUrl.trimStart('/')}"
        }
        return Uri.parse(cleaned)
    }

    private fun parseIsoTimestampToMillis(isoString: String): Long {
        return try {
            val trimmed = isoString.substringBefore("Z")
                .takeWhile { it != '.' } +
                    "." + isoString.substringAfter(".", "000000").padEnd(6, '0').take(6) + "Z"

            val inputFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(trimmed)
            date?.time ?: 0L
        } catch (e: Exception) {
            Log.e("TimeParse", "Error parsing $isoString: ${e.message}")
            0L
        }
    }

    fun receiveMessageFromDto(dto: MessageDto, context: Context) {
        viewModelScope.launch {
            WebSocketController.mapMessageIdToChat(dto.id, dto.chat_id)
            val usersMap = _participants.value.orEmpty().associateBy { it.id }
            val senderName = usersMap[dto.user_id]?.fullName ?: "Неизвестный"
            val millis = parseIsoTimestampToMillis(dto.created_at)

            val files = dto.files?.mapNotNull { file ->
                fetchFile(context, file.id, file.file_url)
            } ?: emptyList()

            val pollData = dto.poll?.let {
                PollData(
                    id = it.id,
                    question = it.question,
                    createdAt = formatTimestamp(it.created_at),
                    options = it.options.map { opt ->
                        PollData.Option(opt.id, opt.text, opt.votes)
                    }
                )
            }

            val message = Message(
                id = dto.id,
                text = dto.text,
                sender = senderName,
                timestamp = formatTimestamp(dto.created_at),
                timestampMillis = millis,
                isMe = dto.user_id == currentUserId,
                files = files,
                replyToMessageId = dto.parent_message_id,
                pollData = pollData,
                showSenderName = _realtimeMessages.value.firstOrNull()?.sender != senderName
            )

            _realtimeMessages.update { list ->
                if (list.any { it.id == message.id }) list else listOf(message) + list
            }

            pollData?.let { newPoll ->
                val existingIds = _polls.value.map { it.id }
                if (newPoll.id !in existingIds) {
                    _polls.update { it + newPoll }
                }
            }

            updateChatItems()
        }
    }

    fun deleteMessageRemote(
        chatId: Int,
        messageId: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            deleteMessageUseCase(chatId, messageId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _messages.update { it.filterNot { msg -> msg.id == messageId } }
                        _realtimeMessages.update { it.filterNot { msg -> msg.id == messageId } }
                        onSuccess()
                        updateChatItems()
                    }

                    is Resource.Error -> onError(result.message ?: "Ошибка удаления")
                    else -> {}
                }
            }
        }
    }

    fun sendMessage(chatId: Int, text: String, context: Context) {

        val trimmedText = text.trim()
        val files = attachedFiles.value.orEmpty()

        if (trimmedText.isBlank() && files.isEmpty()) return

        val fileList = files.map {
            FileUtil.getFileFromUri(context, it.uri)
        }

        Log.d("UploadDebug", "Files to send: ${fileList.map { it }}")

        Log.d("DownloadDebug", "Downloading from $fileList")


        viewModelScope.launch {

            sendMessageUseCase(chatId, trimmedText, fileList).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _attachedFiles.value = emptyList()
                        _replyMessage.value = null
                        _editingMessage.value = null
                    }

                    is Resource.Error -> {
                        Log.e("GroupVM", "Ошибка отправки: ${result.message}")
                    }

                    else -> {}
                }
            }
        }
    }


    fun goBack() {
        viewModelScope.launch {
            navigator.navigateUp()
        }
    }

    private fun clearSelection() {
        _selectedFiles.value = emptySet()
    }

    fun enterSelectionMode() {
        _isInSelectionMode.value = true
        clearSelection()
    }

    fun exitSelectionMode() {
        _isInSelectionMode.value = false
        clearSelection()
    }

    fun receiveMessage(
        text: String,
        sender: String?,
        userId: Int,
        files: List<FileAttachment> = emptyList(),
        pollData: PollData? = null
    ) {
        val participantsList = _participants.value

        if (participantsList.isNullOrEmpty()) {
            pendingMessages.removeAll { it.second == userId && it.first == text }
            pendingMessages.add(Triple(text, userId, files))
            Log.d("receiveMessage", "Delayed (no participants) from $userId: $text")
            return
        }

        val resolvedName = sender ?: participantsList.firstOrNull { it.id == userId }?.fullName
        if (resolvedName == null) {
            pendingMessages.removeAll { it.second == userId && it.first == text }
            pendingMessages.add(Triple(text, userId, files))
            Log.d("receiveMessage", "Delayed (no name) from $userId: $text")
            return
        }

        pollData?.let { poll ->
            val alreadyExists = _polls.value.any { it.id == poll.id }
            if (!alreadyExists) {
                _polls.update { it + poll }
            }
        }
        updateChatItems()

        val previousMessage = _messages.value.firstOrNull()
        val newMessage = Message(
            id = (_messages.value.filter { it.id > 0 }.maxOfOrNull { it.id } ?: 0) + 1,
            text = text,
            sender = resolvedName,
            timestamp = pollData?.createdAt ?: formatTimestamp(Date()),
            timestampMillis = pollData?.timestampMillis ?: System.currentTimeMillis(),
            isMe = userId == currentUserId,
            files = files,
            pollData = pollData,
            showSenderName = previousMessage?.sender != resolvedName
        )

        _realtimeMessages.update { list ->
            if (list.any { it.id == newMessage.id }) list else listOf(newMessage) + list
        }

        updateChatItems()
        Log.d("receiveMessage", "Displayed: $resolvedName -> $text")
    }

    fun scrollToMessage(message: Message) {
        _highlightedMessage.value = message
    }

    fun clearHighlight() {
        _highlightedMessage.value = null
    }

    fun attachFile(uri: Uri, context: Context) {
        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(uri, null, null, null, null)
        var sizeInBytes = 0L

        cursor?.use {
            if (it.moveToFirst()) {
                val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                sizeInBytes = if (sizeIndex >= 0) it.getLong(sizeIndex) else 0L
                if (sizeInBytes > 10 * 1024 * 1024) {
                    Toast.makeText(context, "Файл превышает 10МБ", Toast.LENGTH_SHORT).show()
                    return
                }
            }
        }

        val fileName = getFileName(uri, context) ?: "Без имени"
        val fileSize = formatFileSize(sizeInBytes)

        val attachment = FileAttachment(
            id = null,
            uri = uri,
            fileName = fileName,
            fileSize = fileSize
        )

        val currentList = _attachedFiles.value.orEmpty()
        _attachedFiles.value = currentList + attachment
    }

    fun removeFile(file: FileAttachment) {
        _attachedFiles.value = _attachedFiles.value?.filterNot { it.uri == file.uri }
    }

    fun startEditing(messageId: Int, allMessages: List<Message>) {
        _editingMessage.value = allMessages.firstOrNull { msg -> msg.id == messageId }
    }

    fun cancelEditing() {
        _editingMessage.value = null
    }

    fun setReplyMessage(message: Message) {
        _replyMessage.value = message
    }

    fun clearReply() {
        _replyMessage.value = null
    }

    private fun getFileName(uri: Uri, context: Context): String? {
        return context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            cursor.moveToFirst()
            cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME).let {
                if (it != -1) cursor.getString(it) else null
            }
        }
    }

    fun voteInPoll(chatId: Int, pollId: Int, optionId: Int, context: Context) {
        viewModelScope.launch {
            votePollUseCase(chatId, pollId, optionId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _polls.update { polls ->
                            polls.map { poll ->
                                if (poll.id == pollId) {
                                    val updatedOptions = poll.options.map { opt ->
                                        val alreadySelected = poll.selectedOption
                                        when (opt.id) {
                                            optionId -> opt.copy(votes = opt.votes + 1)
                                            alreadySelected -> opt.copy(votes = opt.votes - 1)
                                            else -> opt
                                        }
                                    }
                                    poll.copy(options = updatedOptions, selectedOption = optionId)
                                } else poll
                            }
                        }

                        val updatedPoll = _polls.value.firstOrNull { it.id == pollId }
                        updatedPoll?.let { poll ->
                            _messages.update { list ->
                                list.map { msg ->
                                    if (msg.pollData?.id == pollId) msg.copy(pollData = poll) else msg
                                }
                            }

                            _realtimeMessages.update { list ->
                                list.map { msg ->
                                    if (msg.pollData?.id == pollId) msg.copy(pollData = poll) else msg
                                }
                            }
                        }

                        updateChatItems()
                    }

                    is Resource.Error -> {
                        Log.e("GroupVM", "Ошибка голосования: ${result.message}")

                        val shownText = when {
                            result.message?.contains("уже проголосовали", ignoreCase = true) == true -> {
                                "Вы уже проголосовали в этом опросе"
                            }

                            result.message?.contains("cannot vote", ignoreCase = true) == true -> {
                                "Вы уже голосовали"
                            }

                            else -> {
                                result.message ?: "Ошибка голосования"
                            }
                        }

                        Toast.makeText(context, shownText, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }
    }

    fun searchMessages(chatId: Int, query: String, context: Context) {
        viewModelScope.launch {
            searchMessagesUseCase(chatId, query, limit = 40, offset = 0).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val dtoList = result.data ?: emptyList()
                        val enriched = dtoList.map { enrichMessageFromDto(it, context) }

                        _messages.value = enriched
                        _polls.value = emptyList()
                        _realtimeMessages.value = emptyList()

                        updateChatItems()
                    }

                    is Resource.Error -> {
                        Toast.makeText(
                            context,
                            result.message ?: "Ошибка поиска",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {}
                }
            }
        }
    }

    fun unvotePoll(chatId: Int, pollId: Int, optionId: Int) {
        viewModelScope.launch {
            unvotePollUseCase(chatId, pollId, optionId).collect { result ->
                if (result is Resource.Success) {
                    _polls.update { polls ->
                        polls.map { poll ->
                            if (poll.id == pollId && poll.selectedOption == optionId) {
                                val updatedOptions = poll.options.map { opt ->
                                    if (opt.id == optionId) opt.copy(votes = opt.votes - 1)
                                    else opt
                                }
                                poll.copy(options = updatedOptions, selectedOption = null)
                            } else poll
                        }
                    }

                    val updatedPoll = _polls.value.firstOrNull { it.id == pollId }
                    if (updatedPoll != null) {
                        _messages.update { list ->
                            list.map { msg ->
                                if (msg.pollData?.id == pollId) msg.copy(pollData = updatedPoll) else msg
                            }
                        }

                        _realtimeMessages.update { list ->
                            list.map { msg ->
                                if (msg.pollData?.id == pollId) msg.copy(pollData = updatedPoll) else msg
                            }
                        }
                    }

                    updateChatItems()
                } else if (result is Resource.Error) {
                    Log.e("GroupVM", "Ошибка отмены голоса: ${result.message}")
                }
            }
        }
    }

    fun deletePoll(chatId: Int, pollId: Int) {
        viewModelScope.launch {
            deletePollUseCase(chatId, pollId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        removePollLocally(pollId)
                        updateChatItems()
                    }

                    is Resource.Error -> {
                        Log.e("GroupVM", "Ошибка удаления опроса: ${result.message}")
                    }

                    else -> {}
                }
            }
        }
    }

    fun sendPoll(chatId: Int, question: String, options: List<String>) {
        viewModelScope.launch {
            createPollUseCase(chatId, CreatePollRequest(question, options)).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val pollId = result.data?.poll_id
                        if (pollId != null) {
                            loadPolls(chatId)
                        }
                    }

                    is Resource.Error -> Log.e(
                        "GroupVM",
                        "Ошибка создания опроса: ${result.message}"
                    )

                    else -> {}
                }
            }
        }
    }

    fun removePollLocally(pollId: Int) {
        Log.d("PollDebug", "Удаляем опрос $pollId локально")
        _polls.update { it.filterNot { poll -> poll.id == pollId } }

        _messages.update { it.filterNot { msg -> msg.pollData?.id == pollId } }
        _realtimeMessages.update { it.filterNot { msg -> msg.pollData?.id == pollId } }

        updateChatItems()
    }


    private fun formatFileSize(sizeBytes: Long): String {
        return when {
            sizeBytes < 1024 -> "$sizeBytes Б"
            sizeBytes < 1024 * 1024 -> String.format("%.1f КБ", sizeBytes / 1024.0)
            else -> String.format("%.1f МБ", sizeBytes / (1024.0 * 1024.0))
        }
    }

    fun openFile(uri: Uri, context: Context) {
        val isPdf = uri.toString().lowercase().endsWith(".pdf")
        val mimeType = getMimeType(uri, context)

        if (isPdf || mimeType == "application/pdf") {
            viewModelScope.launch {
                try {
                    if (uri.scheme == "http" || uri.scheme == "https") {
                        val file = withContext(Dispatchers.IO) {
                            val url = URL(uri.toString())
                            val connection = url.openConnection() as HttpURLConnection
                            connection.connect()

                            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                                throw Exception("HTTP error ${connection.responseCode}")
                            }

                            val inputStream = connection.inputStream
                            val tempFile = File.createTempFile("download", ".pdf", context.cacheDir)
                            tempFile.outputStream().use { output -> inputStream.copyTo(output) }
                            tempFile
                        }

                        FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.provider",
                            file
                        )
                    } else {
                        uri
                    }
                    navigator.navigate(Destination.PdfScreenDestination(uri = uri.toString()))
                } catch (e: Exception) {
                    Toast.makeText(context, "Не удалось открыть PDF", Toast.LENGTH_SHORT).show()
                }
            }
            return
        }

        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Ошибка открытия файла", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getMimeType(uri: Uri, context: Context): String {
        return if (uri.scheme == "content") {
            context.contentResolver.getType(uri) ?: "*/*"
        } else {
            when (uri.toString().substringAfterLast('.', "").lowercase()) {
                "pdf" -> "application/pdf"
                "doc", "docx" -> "application/msword"
                "xls", "xlsx" -> "application/vnd.ms-excel"
                "txt" -> "text/plain"
                "png", "jpg", "jpeg", "gif" -> "image/*"
                "mp4" -> "video/mp4"
                else -> "*/*"
            }
        }
    }

    private fun cleanFileName(fileUrl: String): String {
        return fileUrl.substringAfterLast('/').substringAfter('_')
    }

    private fun mapDtoToMessage(dto: MessageDto, poll: PollData? = null): Message {
        val senderName = participants.value
            ?.firstOrNull { it.id == dto.user_id }
            ?.fullName ?: "Неизвестный"

        val millis = parseIsoTimestampToMillis(dto.created_at)

        val pollData = poll ?: dto.poll?.let {
            PollData(
                id = it.id,
                question = it.question,
                createdAt = formatTimestamp(it.created_at),
                options = it.options.map { opt ->
                    PollData.Option(
                        id = opt.id,
                        text = opt.text,
                        votes = opt.votes
                    )
                }
            )
        }

        return Message(
            id = dto.id,
            text = dto.text,
            sender = senderName,
            timestamp = formatTimestamp(dto.created_at),
            timestampMillis = millis,
            isMe = dto.user_id == currentUserId,
            files = dto.files?.map {
                FileAttachment(
                    id = it.id,
                    uri = resolveFileUri(it.file_url),
                    fileName = cleanFileName(it.file_url),
                    fileSize = "—"
                )
            } ?: emptyList(),
            replyToMessageId = dto.parent_message_id,
            pollData = pollData
        )
    }
}