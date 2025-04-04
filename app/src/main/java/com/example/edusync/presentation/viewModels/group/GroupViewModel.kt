package com.example.edusync.presentation.viewModels.group

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusync.presentation.navigation.Destination
import com.example.edusync.presentation.navigation.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GroupViewModel(private val navigator: Navigator) : ViewModel() {

    private val _highlightedMessage = MutableLiveData<Message?>()
    val highlightedMessage: LiveData<Message?> = _highlightedMessage

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

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

    fun toggleFileSelection(file: FileAttachment) {
        _selectedFiles.update { current ->
            current.toMutableSet().apply {
                if (contains(file)) remove(file) else add(file)
            }
        }
    }

    fun goBack(){
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


    init {
        viewModelScope.launch {
            selectedFiles.collect { files ->
                if (files.isEmpty()) {
                    exitSelectionMode()
                }
            }
        }
    }

    fun receiveMessage(text: String, sender: String, files: List<FileAttachment> = emptyList()) {
        val previousMessage = _messages.value?.firstOrNull()
        val newMessage = Message(
            id = (_messages.value?.size ?: 0) + 1,
            text = text.ifEmpty { null },
            sender = sender,
            timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
            isMe = false,
            files = files,
            showSenderName = previousMessage?.sender != sender
        )
        _messages.value = _messages.value.orEmpty() + newMessage
    }

    fun deleteSelectedFiles() {
        val selected = _selectedFiles.value
        val messagesToRemove = _messages.value?.filter { message ->
            message.files.any { file -> file in selected }
        } ?: emptyList()
        _messages.value = _messages.value?.filterNot { it in messagesToRemove }
        exitSelectionMode()
    }

    fun scrollToMessage(message: Message) {
        _highlightedMessage.value = message
    }

    fun clearHighlight() {
        _highlightedMessage.value = null
    }

    fun sendMessage(text: String, files: List<FileAttachment>) {
        val trimmedText = text.trim()

        if (trimmedText.isBlank() && files.isEmpty()) return

        editingMessage.value?.let { existingMessage ->
            val updatedMessage = existingMessage.copy(
                text = trimmedText,
                files = files,
                timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                isEdited = true
            )
            val updatedMessages = _messages.value?.toMutableList()?.apply {
                val index = indexOfFirst { it.id == existingMessage.id }
                if (index != -1) set(index, updatedMessage)
            } ?: emptyList()
            _messages.value = updatedMessages
            _editingMessage.value = null
        } ?: run {
            val newMessage = Message(
                id = (_messages.value?.size ?: 0) + 1,
                text = trimmedText.ifEmpty { null },
                sender = "You",
                timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                isMe = true,
                files = files,
                showSenderName = _messages.value?.firstOrNull()?.sender != "Вы",
                replyTo = _replyMessage.value
            )
            _messages.value = listOf(newMessage) + _messages.value.orEmpty()
        }
        _attachedFiles.value = emptyList()
        _replyMessage.value = null
    }

    fun attachFile(uri: Uri, context: Context) {
        val fileName = getFileName(uri, context)
        val fileSize = getFileSize(uri, context)

        val newFile = FileAttachment(
            uri = uri,
            fileName = fileName ?: "Файл",
            fileSize = fileSize ?: "0 Б"
        )

        _attachedFiles.value = _attachedFiles.value.orEmpty() + newFile
    }

    fun removeFile(file: FileAttachment) {
        _attachedFiles.value = _attachedFiles.value?.filterNot { it.uri == file.uri }
    }

    fun startEditing(messageId: Int) {
        _editingMessage.value = _messages.value?.find { it.id == messageId }
    }

    fun cancelEditing() {
        _editingMessage.value = null
    }

    fun deleteMessage(messageId: Int) {
        _messages.value = _messages.value?.filterNot { it.id == messageId }
    }


    fun clearAttachments() {
        _attachedFiles.value = emptyList()
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

    fun voteInPoll(messageId: Int, option: String) {
        _messages.value = _messages.value?.map { message ->
            if (message.id == messageId && message.pollData != null) {
                val newOptions = message.pollData.options.toMutableMap()
                newOptions[option] = newOptions.getOrDefault(option, 0) + 1
                message.copy(
                    pollData = message.pollData.copy(
                        options = newOptions,
                        totalVotes = newOptions.values.sum(),
                        selectedOption = option
                    )
                )
            } else {
                message
            }
        }
    }

    fun sendPoll(question: String, options: List<String>) {
        val newMessage = Message(
            id = (_messages.value?.size ?: 0) + 1,
            text = null,
            sender = "You",
            timestamp = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
            isMe = true,
            pollData = PollData(question, options.associateWith { 0 }.toMutableMap())
        )
        _messages.value = listOf(newMessage) + _messages.value.orEmpty()
    }

    private fun getFileSize(uri: Uri, context: Context): String? {
        return context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            cursor.moveToFirst()
            cursor.getColumnIndex(OpenableColumns.SIZE).let {
                if (it != -1) formatFileSize(cursor.getLong(it)) else null
            }
        }
    }

    private fun formatFileSize(sizeBytes: Long): String {
        return when {
            sizeBytes < 1024 -> "$sizeBytes Б"
            sizeBytes < 1024 * 1024 -> String.format("%.1f КБ", sizeBytes / 1024.0)
            else -> String.format("%.1f МБ", sizeBytes / (1024.0 * 1024.0))
        }
    }

    fun openFile(uri: Uri, context: Context) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, getMimeType(uri, context))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Не удалось открыть файл", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getMimeType(uri: Uri, context: Context): String {
        val extension = uri.path?.substringAfterLast(".")?.lowercase() ?: ""
        return when (extension) {
            "pdf" -> "application/pdf"
            "doc", "docx" -> "application/msword"
            "xls", "xlsx" -> "application/vnd.ms-excel"
            "txt" -> "text/plain"
            "png", "jpg", "jpeg" -> "image/*"
            else -> "*/*"
        }
    }
}


fun sendMessage(
    text: String,
    viewModel: GroupViewModel,
    context: Context
) {
    val trimmedText = text.trim()
    val attachedFiles = viewModel.attachedFiles.value.orEmpty()

    if (attachedFiles.isNotEmpty() || trimmedText.isNotBlank()) {
        viewModel.sendMessage(
            text = trimmedText,
            files = attachedFiles
        )
        viewModel.clearAttachments()
    }
}

data class Message(
    val id: Int,
    val text: String?,
    val sender: String,
    val timestamp: String,
    val isMe: Boolean,
    val files: List<FileAttachment> = emptyList(),
    val isRead: Boolean = false,
    val showSenderName: Boolean = true,
    val isEdited: Boolean = false,
    val replyTo: Message? = null,
    val pollData: PollData? = null
)

data class FileAttachment(
    val uri: Uri,
    val fileName: String,
    val fileSize: String
)
data class PollData(
    val question: String,
    val options: Map<String, Int>,
    val totalVotes: Int = options.values.sum(),
    val selectedOption: String? = null
)