package com.example.edusync.presentation.views.group.components.messageInput

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edusync.R
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography
import com.example.edusync.presentation.viewModels.group.GroupViewModel
import com.example.edusync.presentation.viewModels.group.sendMessage
import com.example.edusync.presentation.views.group.components.fileAttachment.FileAttachmentList
import com.example.edusync.presentation.views.group.components.fileAttachment.isImage

@Composable
fun MessageInput(
    viewModel: GroupViewModel,
    context: Context = LocalContext.current
) {
    var messageText by remember { mutableStateOf("") }
    val attachedFiles by viewModel.attachedFiles.observeAsState(emptyList())
    val replyMessage by viewModel.replyMessage.observeAsState()
    val editingMessage by viewModel.editingMessage.observeAsState()
    val focusManager = LocalFocusManager.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        uris.forEach { uri -> viewModel.attachFile(uri, context) }
    }

    LaunchedEffect(editingMessage) {
        messageText = editingMessage?.text ?: ""
    }

    Column(modifier = Modifier.padding(top = 8.dp)) {
        replyMessage?.let { message ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.OnBackground)
                    .padding(8.dp)
                    .clickable { viewModel.scrollToMessage(message) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "В ответ",
                            color = AppColors.Primary,
                            style = AppTypography.body1.copy(fontSize = 14.sp)
                        )
                        Text(
                            text = message.sender,
                            color = AppColors.Secondary,
                            style = AppTypography.body1.copy(fontSize = 14.sp),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    when {
                        message.text != null -> {
                            Text(
                                text = message.text,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = AppTypography.body1.copy(fontSize = 12.sp),
                                color = AppColors.Secondary
                            )
                        }

                        message.files.isNotEmpty() -> {
                            val imageCount = message.files.count { it.isImage() }
                            val hasImages = imageCount > 0

                            if (hasImages) {
                                Text(
                                    text = if (imageCount == 1) "Фотография"
                                    else "Фотографии ($imageCount)",
                                    style = AppTypography.body1.copy(fontSize = 12.sp),
                                    color = AppColors.Secondary
                                )
                            } else {
                                Text(
                                    text = "Файл: ${message.files.first().fileName}",
                                    style = AppTypography.body1.copy(fontSize = 12.sp),
                                    color = AppColors.Secondary
                                )
                            }
                        }
                    }
                }
                IconButton(onClick = { viewModel.clearReply() }) {
                    Icon(painterResource(R.drawable.ic_close), "Отменить", tint = AppColors.Primary)
                }
            }
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(),
                thickness = 2.dp,
                color = AppColors.ChatColor
            )
        }

        if (attachedFiles.isNotEmpty()) {
            FileAttachmentList(
                files = attachedFiles,
                onRemoveFile = { viewModel.removeFile(it) }
            )
        }

        TextField(
            value = messageText,
            onValueChange = { messageText = it },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp),
            placeholder = {
                Text(
                    text = "Введите сообщение",
                    color = AppColors.Secondary
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_attach),
                    contentDescription = "Прикрепить файл",
                    tint = AppColors.Secondary,
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { launcher.launch("*/*") }
                        .padding(horizontal = 8.dp)
                )
            },
            trailingIcon = {
                if (editingMessage != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_done),
                            contentDescription = "Сохранить",
                            tint = AppColors.Primary,
                            modifier = Modifier
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    viewModel.sendMessage(messageText, emptyList())
                                    messageText = ""
                                }
                                .padding(horizontal = 8.dp)
                        )
                        Icon(
                            painter = painterResource(R.drawable.ic_close),
                            contentDescription = "Отменить",
                            tint = AppColors.Error,
                            modifier = Modifier
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    viewModel.cancelEditing()
                                    messageText = ""
                                }
                                .padding(horizontal = 8.dp)
                        )
                    }
                } else {
                    Icon(
                        painter = painterResource(R.drawable.ic_send),
                        contentDescription = "Отправить",
                        tint = AppColors.Secondary,
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                sendMessage(
                                    text = messageText,
                                    viewModel = viewModel,
                                    context = context
                                )
                                messageText = ""
                            }
                            .padding(horizontal = 8.dp)
                    )
                }
            },
            keyboardActions = KeyboardActions(
                onSend = {
                    if (editingMessage != null) {
                        viewModel.sendMessage(messageText, emptyList())
                        messageText = ""
                    } else {
                        sendMessage(
                            text = messageText,
                            viewModel = viewModel,
                            context = context
                        )
                        messageText = ""
                    }
                    focusManager.clearFocus()
                }
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            maxLines = 6,
            colors = TextFieldDefaults.colors(
                focusedTextColor = AppColors.Secondary,
                unfocusedTextColor = AppColors.Secondary,
                disabledTextColor = AppColors.Secondary.copy(alpha = 0.5f),
                cursorColor = AppColors.Primary,
                focusedContainerColor = AppColors.OnBackground,
                unfocusedContainerColor = AppColors.OnBackground,
                disabledContainerColor = AppColors.OnBackground,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedPlaceholderColor = AppColors.Secondary.copy(alpha = 0.5f),
                unfocusedPlaceholderColor = AppColors.Secondary.copy(alpha = 0.5f)
            )
        )
    }
}