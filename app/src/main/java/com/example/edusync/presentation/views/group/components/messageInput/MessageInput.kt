package com.example.edusync.presentation.views.group.components.messageInput

import android.Manifest
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.eduHub.edusync.R
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography
import com.example.edusync.presentation.viewModels.group.GroupViewModel
import com.example.edusync.presentation.views.group.components.fileAttachment.FileAttachmentList
import com.example.edusync.presentation.views.group.components.fileAttachment.isImage

@Composable
fun MessageInput(
    viewModel: GroupViewModel,
    chatId: Int,
    context: Context = LocalContext.current
) {
    var messageText by remember { mutableStateOf(TextFieldValue("")) }
    val attachedFiles by viewModel.attachedFiles.observeAsState(emptyList())
    val replyMessage by viewModel.replyMessage.observeAsState()
    val editingMessage by viewModel.editingMessage.observeAsState()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    val documentLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris -> uris.forEach { uri -> viewModel.attachFile(uri, context) } }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.all { it }
        if (granted) {
            documentLauncher.launch(arrayOf("*/*"))
        } else {
            Toast.makeText(context, "Разрешение на чтение файлов не предоставлено", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(editingMessage) {
        messageText = TextFieldValue(
            text = editingMessage?.text ?: "",
            selection = TextRange((editingMessage?.text ?: "").length)
        )
        editingMessage?.let {
            focusRequester.requestFocus()
        }
    }

    Column(
        modifier = Modifier
            .padding(top = 8.dp)
            .imePadding()
    ) {
        replyMessage?.let { message ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.OnBackground)
                    .padding(8.dp)
                    .clickable { viewModel.scrollToMessage(message) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.in_answer),
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
                            val text = when {
                                imageCount == 1 -> stringResource(R.string.single_photo)
                                imageCount > 1 -> stringResource(R.string.multiple_photos, imageCount)
                                else -> stringResource(R.string.file_label, message.files.first().fileName)
                            }
                            Text(
                                text = text,
                                style = AppTypography.body1.copy(fontSize = 12.sp),
                                color = AppColors.Secondary
                            )
                        }
                    }
                }

                IconButton(onClick = { viewModel.clearReply() }) {
                    Icon(
                        painterResource(R.drawable.ic_close),
                        contentDescription = "Отменить",
                        tint = AppColors.Primary
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
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
            onValueChange = {
                if (it.text.length <= 1000) messageText = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .focusRequester(focusRequester),
            placeholder = {
                Text(stringResource(R.string.enter_message), color = AppColors.Secondary)
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
                        ) {
                            focusManager.clearFocus()
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                                permissionLauncher.launch(
                                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                                )
                            } else {
                                documentLauncher.launch(arrayOf("*/*"))
                            }
                        }
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
                                .clickable {
                                    editingMessage?.id?.let { id ->
                                        viewModel.editMessage(
                                            chatId = chatId,
                                            messageId = id,
                                            newText = messageText.text,
                                            onSuccess = { messageText = TextFieldValue("") },
                                            onError = { }
                                        )
                                    }
                                }
                                .padding(horizontal = 8.dp)
                        )
                        Icon(
                            painter = painterResource(R.drawable.ic_close),
                            contentDescription = "Отменить",
                            tint = AppColors.Error,
                            modifier = Modifier
                                .clickable {
                                    viewModel.cancelEditing()
                                    messageText = TextFieldValue("")
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
                            .clickable {
                                if (replyMessage != null) {
                                    viewModel.replyToMessage(
                                        chatId,
                                        replyMessage!!.id,
                                        messageText.text,
                                        context
                                    )
                                } else {
                                    viewModel.sendMessage(chatId, messageText.text, context)
                                }
                                messageText = TextFieldValue("")
                            }
                            .padding(horizontal = 8.dp)
                    )
                }
            },
            keyboardActions = KeyboardActions(
                onSend = {
                    if (editingMessage != null) {
                        editingMessage?.id?.let { id ->
                            viewModel.editMessage(
                                chatId = chatId,
                                messageId = id,
                                newText = messageText.text,
                                onSuccess = { messageText = TextFieldValue("") },
                                onError = { }
                            )
                        }
                    } else if (replyMessage != null) {
                        viewModel.replyToMessage(chatId, replyMessage!!.id, messageText.text, context)
                    } else {
                        viewModel.sendMessage(chatId, messageText.text, context)
                    }
                    if (editingMessage == null && replyMessage == null) {
                        focusManager.clearFocus()
                    }
                }
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            maxLines = 6,
            colors = TextFieldDefaults.colors(
                focusedTextColor = AppColors.Secondary,
                unfocusedTextColor = AppColors.Secondary,
                cursorColor = AppColors.Primary,
                focusedContainerColor = AppColors.OnBackground,
                unfocusedContainerColor = AppColors.OnBackground,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedPlaceholderColor = AppColors.Secondary.copy(alpha = 0.5f),
                unfocusedPlaceholderColor = AppColors.Secondary.copy(alpha = 0.5f)
            )
        )
    }
}