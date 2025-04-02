package com.example.edusync.presentation.views.group.components.chatBubble

import android.content.Context
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.edusync.R
import com.example.edusync.presentation.components.modal_window.DeleteMessageWindow
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography
import com.example.edusync.presentation.viewModels.group.GroupViewModel
import com.example.edusync.presentation.viewModels.group.Message
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
    context: Context
) {
    val isTeacher = true
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
                    if (isInSelectionMode) {
                        message.files.forEach { viewModel.toggleFileSelection(it) }
                    } else {
                        if (message.replyTo != null) {
                            viewModel.scrollToMessage(message.replyTo)
                        } else {
                            expanded = true
                        }
                    }
                },
                onLongClick = {
                    if (message.files.isNotEmpty()) {
                        viewModel.enterSelectionMode()
                        message.files.forEach { viewModel.toggleFileSelection(it) }
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
                        } else {
                            Modifier
                        }
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .widthIn(max = 250.dp),
                horizontalAlignment = Alignment.Start
            ) {
                message.replyTo?.let { repliedMessage ->
                    Column(
                        modifier = Modifier
                            .background(
                                AppColors.Primary.copy(alpha = 0.1f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 12.dp)
                            .clickable { viewModel.scrollToMessage(repliedMessage) }
                    ) {
                        Text(
                            text = repliedMessage.sender,
                            color = AppColors.Primary.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = AppTypography.title.copy(fontSize = 14.sp)
                        )
                        when {
                            repliedMessage.text != null -> {
                                Text(
                                    text = repliedMessage.text,
                                    color = AppColors.Secondary.copy(alpha = 0.7f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = AppTypography.body1.copy(fontSize = 12.sp)
                                )
                            }
                            repliedMessage.files.isNotEmpty() -> {
                                val imageCount = repliedMessage.files.count { it.isImage() }
                                val hasImages = imageCount > 0
                                if (hasImages) {
                                    Text(
                                        text = if (imageCount == 1) "Фотография"
                                        else "Фотографии ($imageCount)",
                                        color = AppColors.Secondary.copy(alpha = 0.7f),
                                        style = AppTypography.body1.copy(fontSize = 12.sp)
                                    )
                                } else {
                                    Text(
                                        text = "Файл: ${repliedMessage.files.first().fileName}",
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
                if (message.pollData != null) {
                    val poll = message.pollData
                    val answers = poll.options.map { (text, count) ->
                        AnswerData(text = text, count = count)
                    }

                    Survey(
                        question = poll.question,
                        totalCount = poll.totalVotes,
                        answers = answers,
                        selected = answers.find { it.text == poll.selectedOption },
                        onClick = { selectedAnswer ->
                            viewModel.voteInPoll(message.id, selectedAnswer.text)
                        }
                    )
                }
                if (!message.isMe && message.showSenderName) {
                    Text(
                        text = message.sender,
                        color = AppColors.Secondary,
                        style = AppTypography.body1.copy(fontSize = 14.sp),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                message.files.forEach { file ->
                    FileAttachmentItem(
                        file = file,
                        viewModel = viewModel,
                        onFileClicked = { uri -> viewModel.openFile(uri, context) },
                        isInSelectionMode = isInSelectionMode
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
                    if (message.isEdited) {
                        Text(
                            text = " изменено",
                            color = AppColors.Secondary.copy(alpha = 0.5f),
                            style = AppTypography.lightText.copy(fontSize = 10.sp),
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                    Text(
                        text = message.timestamp,
                        color = AppColors.Secondary,
                        style = AppTypography.lightText.copy(fontSize = 12.sp),
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    if (message.isMe) {
                        Icon(
                            painter = painterResource(
                                if (message.isRead) R.drawable.ic_done_all
                                else R.drawable.ic_done
                            ),
                            contentDescription = "Статус прочтения",
                            tint = AppColors.Primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .align(if (message.isMe) Alignment.TopEnd else Alignment.TopStart)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                DropdownMenuItem(
                    text = { Text("Ответить") },
                    onClick = {
                        expanded = false
                        viewModel.setReplyMessage(message)
                    }
                )
                DropdownMenuItem(
                    text = { Text("Копировать", style = AppTypography.body1) },
                    onClick = {
                        expanded = false
                        message.text?.let {
                            clipboardManager.setText(AnnotatedString(it))
                            Toast.makeText(context, "Текст скопирован", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
                if (message.isMe) {
                    DropdownMenuItem(
                        text = { Text("Изменить", style = AppTypography.body1) },
                        onClick = {
                            expanded = false
                            viewModel.startEditing(message.id)
                        }
                    )
                }
                if (isTeacher || message.isMe) {
                    DropdownMenuItem(
                        text = { Text("Удалить", style = AppTypography.body1) },
                        onClick = {
                            expanded = false
                            messageToDelete = message.id
                            showDeleteMessageDialog = true
                        }
                    )
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
                            viewModel.deleteMessage(messageToDelete!!)
                            showDeleteMessageDialog = false
                        }
                    )
                }
            }
        }
    }
}