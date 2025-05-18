package com.example.edusync.presentation.views.group.components.chatBubble

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.eduHub.edusync.R
import com.example.edusync.domain.model.message.Message
import com.example.edusync.presentation.components.modal_window.DeleteMessageWindow
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography
import com.example.edusync.presentation.viewModels.group.GroupViewModel
import com.example.edusync.presentation.views.group.components.fileAttachment.FileAttachmentItem
import com.example.edusync.presentation.views.group.components.fileAttachment.isImage
import com.example.edusync.presentation.views.group.components.survey.AnswerData
import com.example.edusync.presentation.views.group.components.survey.Survey
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatBubble(
    message: Message,
    viewModel: GroupViewModel,
    context: Context,
    chatId: Int,
    allMessages: List<Message>,
    isTeacher: Boolean
) {
    val focusManager = LocalFocusManager.current
    var expanded by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    val bubbleColor = if (message.isMe) AppColors.Background else AppColors.ChatColor
    val selectedFiles by viewModel.selectedFiles.collectAsStateWithLifecycle()
    val isInSelectionMode by viewModel.isInSelectionMode.observeAsState(false)

    val isMessageHighlighted = message == viewModel.highlightedMessage.observeAsState().value
    val hasSelectedFiles = message.files.any { it in selectedFiles }
    val backgroundColor by animateColorAsState(
        targetValue = if (hasSelectedFiles || isMessageHighlighted)
            AppColors.Highlight
        else
            Color.Transparent,
        animationSpec = tween(500)
    )

    val repliedMessage = message.replyToMessageId?.let { id ->
        allMessages.firstOrNull { it.id == id }
    }

    var showDeleteMessageDialog by remember { mutableStateOf(false) }
    var messageToDelete by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(hasSelectedFiles, isMessageHighlighted) {
        if ((hasSelectedFiles || isMessageHighlighted) && !isInSelectionMode) {
            delay(1000)
            viewModel.clearHighlight()
        }
    }

    Row(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = {
                    if (!expanded && !isInSelectionMode) {
                        focusManager.clearFocus(force = false)
                    }
                    if (isInSelectionMode) {
                        message.files.forEach { viewModel.toggleFileSelection(it, context) }
                    } else {
                        if (repliedMessage != null) {
                            viewModel.scrollToMessage(repliedMessage)
                        } else {
                            expanded = true
                        }
                    }
                },
                onLongClick = {
                    if (message.files.isNotEmpty()) {
                        viewModel.enterSelectionMode()
                        message.files.forEach { viewModel.toggleFileSelection(it, context) }
                    }
                }
            ),
        horizontalArrangement = if (message.isMe) Arrangement.End else Arrangement.Start
    ) {
        Box {
            Column(
                modifier = Modifier
                    .background(bubbleColor, shape = RoundedCornerShape(12.dp))
                    .then(
                        if (message.isMe) {
                            Modifier.border(
                                width = 2.dp,
                                color = AppColors.Primary,
                                shape = RoundedCornerShape(12.dp)
                            )
                        } else Modifier
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .widthIn(max = 250.dp),
                horizontalAlignment = Alignment.Start
            ) {
                if (!message.isMe) {
                    Text(
                        text = message.sender,
                        color = AppColors.Secondary,
                        style = AppTypography.body1.copy(fontSize = 14.sp),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                repliedMessage?.let {
                    Column(
                        modifier = Modifier
                            .background(
                                AppColors.Primary.copy(alpha = 0.1f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 12.dp)
                            .clickable { viewModel.scrollToMessage(it) }
                    ) {
                        Text(
                            text = it.sender,
                            color = AppColors.Primary.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = AppTypography.title.copy(fontSize = 14.sp)
                        )
                        when {
                            it.text != null -> {
                                Text(
                                    text = it.text,
                                    color = AppColors.Secondary.copy(alpha = 0.7f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = AppTypography.body1.copy(fontSize = 12.sp)
                                )
                            }
                            it.files.isNotEmpty() -> {
                                val imageCount = it.files.count { it.isImage() }
                                if (imageCount > 0) {
                                    val text = if (imageCount == 1) {
                                        stringResource(R.string.single_photo)
                                    } else {
                                        stringResource(R.string.multiple_photos, imageCount)
                                    }
                                    Text(
                                        text = text,
                                        color = AppColors.Secondary.copy(alpha = 0.7f),
                                        style = AppTypography.body1.copy(fontSize = 12.sp)
                                    )
                                } else {
                                    Text(
                                        text = stringResource(R.string.file_label, it.files.first().fileName),
                                        color = AppColors.Secondary.copy(alpha = 0.7f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        style = AppTypography.body1.copy(fontSize = 12.sp)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                message.pollData?.let { poll ->
                    val answers = poll.options.map {
                        AnswerData(
                            id = it.id,
                            text = it.text,
                            count = it.votes
                        )
                    }

                    Survey(
                        question = poll.question,
                        totalCount = poll.totalVotes,
                        answers = answers,
                        selected = answers.find { it.id == poll.selectedOption },
                        onClick = { selectedAnswer ->
                            if (poll.selectedOption == null) {
                                viewModel.voteInPoll(chatId, poll.id, selectedAnswer.id, context)
                            }
                        }
                    )
                }

                message.files.forEach { file ->
                    FileAttachmentItem(
                        file = file,
                        viewModel = viewModel,
                        onFileClicked = { uri -> viewModel.openFile(uri, context) },
                        isInSelectionMode = isInSelectionMode,
                        context = context
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                message.text?.let {
                    Text(
                        text = it,
                        color = AppColors.Secondary,
                        style = AppTypography.body1.copy(fontSize = 16.sp)
                    )
                }

                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message.timestamp,
                        color = AppColors.Secondary,
                        style = AppTypography.lightText.copy(fontSize = 12.sp),
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
            }

            if (message.pollData != null) {

                val hasVoted = message.pollData.selectedOption != null

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .align(if (message.isMe) Alignment.TopEnd else Alignment.TopStart)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    if (hasVoted) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.unvote)) },
                            onClick = {
                                expanded = false
                                val selectedOption = message.pollData.selectedOption
                                if (selectedOption != null) {
                                    viewModel.unvotePoll(chatId, message.pollData.id, selectedOption)
                                } else {
                                    Log.e("ChatBubble", "Ошибка: selectedOption = null")
                                }
                            }
                        )
                    }
                    if (isTeacher) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.delete_poll)) },
                            onClick = {
                                expanded = false
                                viewModel.deletePoll(chatId, message.pollData.id)
                            }
                        )
                    }
                }
            } else {
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .align(if (message.isMe) Alignment.TopEnd else Alignment.TopStart)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.answer), style = AppTypography.body1) },
                        onClick = {
                            expanded = false
                            viewModel.setReplyMessage(message)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.copy), style = AppTypography.body1) },
                        onClick = {
                            expanded = false
                            message.text?.let {
                                clipboardManager.setText(AnnotatedString(it))
                            }
                        }
                    )
                    if (message.isMe) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.change), style = AppTypography.body1) },
                            onClick = {
                                expanded = false
                                viewModel.startEditing(message.id, allMessages)
                            }
                        )
                    }
                    if (isTeacher || message.isMe) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.delete), style = AppTypography.body1) },
                            onClick = {
                                expanded = false
                                messageToDelete = message.id
                                showDeleteMessageDialog = true
                            }
                        )
                    }
                }
            }

            if (showDeleteMessageDialog) {
                Dialog(
                    onDismissRequest = { showDeleteMessageDialog = false },
                    properties = DialogProperties(usePlatformDefaultWidth = true)
                ) {
                    DeleteMessageWindow(
                        onDismiss = { showDeleteMessageDialog = false },
                        onDelete = {
                            showDeleteMessageDialog = false
                            viewModel.deleteMessageRemote(
                                chatId = chatId,
                                messageId = messageToDelete ?: return@DeleteMessageWindow,
                                onSuccess = {
                                    Toast.makeText(context, "Сообщение удалено", Toast.LENGTH_SHORT).show()
                                },
                                onError = {
                                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}