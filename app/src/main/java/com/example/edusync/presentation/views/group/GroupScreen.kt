package com.example.edusync.presentation.views.group

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.edusync.R
import com.example.edusync.presentation.components.custom_text_field.search_field.SearchField
import com.example.edusync.presentation.components.modal_window.CreateCodeToJoinGroupWindow
import com.example.edusync.presentation.components.modal_window.CreateNotificationWindow
import com.example.edusync.presentation.components.modal_window.DeleteGroupExitAccountWindow
import com.example.edusync.presentation.components.modal_window.DeleteMessageWindow
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography
import com.example.edusync.presentation.viewModels.group.FileAttachment
import com.example.edusync.presentation.viewModels.group.GroupViewModel
import com.example.edusync.presentation.viewModels.group.Message
import com.example.edusync.presentation.views.group.components.CreatePollDialog
import com.example.edusync.presentation.views.group.components.ParticipantsPopup
import com.example.edusync.presentation.views.group.components.SelectionTopBar
import com.example.edusync.presentation.views.group.components.ShowGroupDropdownMenu
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupScreen(navController: NavHostController) {

    val viewModel: GroupViewModel = koinViewModel()
    val messages by viewModel.messages.observeAsState(emptyList())
    val subjectName = navController.currentBackStackEntry
        ?.arguments
        ?.getString("subjectName") ?: ""

    val listState = rememberLazyListState()
    val highlightedMessage by viewModel.highlightedMessage.observeAsState()
    var showParticipantsPopup by remember { mutableStateOf(false) }
    var showGroupPopup by remember { mutableStateOf(false) }
    var showCreateCodeDialog by remember { mutableStateOf(false) }
    var showCreateNotificationDialog by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showDeleteExitDialog by remember { mutableStateOf(false) }
    val isInSelectionMode by viewModel.isInSelectionMode.observeAsState(false)
    var showCreatePollDialog by remember { mutableStateOf(false) }

    LaunchedEffect(messages.size) {
        listState.animateScrollToItem(0)
    }

    LaunchedEffect(highlightedMessage) {
        highlightedMessage?.let { message ->
            val index = messages.indexOfFirst { it.id == message.id }
            if (index != -1) {
                listState.animateScrollToItem(index)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.receiveMessage(
            text = "Тест тест тест тест тест тест тест тест тест тест тест тест тест тест тест тест тест",
            sender = "Людмила Фёдоровна",
            files = listOf(
                FileAttachment(
                    uri = Uri.parse("file://example.pdf"),
                    fileName = "Документ.pdf",
                    fileSize = "1.2 МБ"
                )
            )
        )
    }

    Scaffold(
        topBar = {
            if (isInSelectionMode) {
                SelectionTopBar(viewModel)
            } else if (isSearchActive) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp, top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_back),
                        contentDescription = "Назад",
                        tint = AppColors.Primary,
                        modifier = Modifier.clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { isSearchActive = false }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    SearchField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        onSearch = { /* Логика поиска */ },
                        imeAction = ImeAction.Search
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
            } else {
                TopAppBar(
                    title = {
                        Text(
                            text = subjectName,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(indication = null,
                                    interactionSource = remember { MutableInteractionSource() }) {
                                    showParticipantsPopup = true
                                }
                        )
                    },
                    navigationIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = "Назад",
                            tint = AppColors.Primary,
                            modifier = Modifier.clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { navController.popBackStack() }
                        )
                    },
                    actions = {
                        Icon(
                            painter = painterResource(R.drawable.ic_more_vert),
                            contentDescription = "Дополнительно",
                            tint = AppColors.Primary,
                            modifier = Modifier
                                .size(28.dp)
                                .clickable(indication = null,
                                    interactionSource = remember { MutableInteractionSource() }) {
                                    showGroupPopup = true
                                }
                        )
                    }
                )
            }
        },
        content = { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppColors.Background)
                    .padding(paddingValues)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f),
                    reverseLayout = true
                ) {
                    items(messages) { message ->
                        ChatBubble(
                            message = message,
                            viewModel,
                            context = LocalContext.current,
                        )
                    }
                }
                MessageInput(viewModel)
            }
        })
    if (showParticipantsPopup) {
        ParticipantsPopup(
            onDismiss = { showParticipantsPopup = false },
            initialParticipants = List(30) { "Лютый Николай Денисович" },
            title = subjectName
        )
    }
    if (showGroupPopup) {
        ShowGroupDropdownMenu(
            onDismiss = { showGroupPopup = false },
            modifier = Modifier.width(200.dp),
            isTeacher = true,
            onAddStudentClick = { showCreateCodeDialog = true },
            onToggleNotifications = { notificationsEnabled = !notificationsEnabled },
            onSearchMaterialsClick = { isSearchActive = true },
            onCreateNotificationClick = { showCreateNotificationDialog = true },
            onDeleteExitGroupClick = { showDeleteExitDialog = true },
            onCreatePollClick = { showCreatePollDialog = true }
        )
    }
    if (showDeleteExitDialog) {
        DeleteGroupExitAccountWindow(
            navController = navController,
            onDismiss = { showDeleteExitDialog = false },
            isTeacher = true
        )
    }
    if (showCreateCodeDialog) {
        CreateCodeToJoinGroupWindow(
            onDismiss = { showCreateCodeDialog = false },
            onParticipantAdded = { /* Логика добавления */ }
        )
    }

    if (showCreateNotificationDialog) {
        CreateNotificationWindow(onDismiss = { showCreateNotificationDialog = false })
    }
    if (showCreatePollDialog) {
        CreatePollDialog(
            onDismiss = { showCreatePollDialog = false },
            onPollCreated = { question, options ->
                viewModel.sendPoll(question, options)
                showCreatePollDialog = false
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatBubble(
    message: Message,
    viewModel: GroupViewModel,
    context: Context
) {
    val isTeacher: Boolean = true
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

fun FileAttachment.isImage(): Boolean {
    val imageExtensions = setOf("png", "jpg", "jpeg")
    return fileName.substringAfterLast('.', "").lowercase() in imageExtensions
}

@Composable
fun FileAttachmentList(
    files: List<FileAttachment>,
    onRemoveFile: (FileAttachment) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        items(files) { file ->
            FileAttachmentView(file) { onRemoveFile(file) }
        }
    }
}

@Composable
fun FileAttachmentItem(
    file: FileAttachment,
    viewModel: GroupViewModel,
    onFileClicked: (Uri) -> Unit,
    isInSelectionMode: Boolean
) {
    val selectedFiles by viewModel.selectedFiles.collectAsStateWithLifecycle()
    val isSelected = selectedFiles.contains(file)
    val backgroundColor =
        if (isSelected) AppColors.Primary.copy(alpha = 0.2f) else Color.Transparent

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (isInSelectionMode) {
                    viewModel.toggleFileSelection(file)
                } else {
                    onFileClicked(file.uri)
                }
            }
    )
    val extension = file.fileName.substringAfterLast('.', "").lowercase()
    val isImage = extension in setOf("png", "jpg", "jpeg")

    if (isImage) {
        AsyncImage(
            model = file.uri,
            contentDescription = "Изображение",
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
        )
    } else {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(
                    when (extension) {
                        "pdf" -> R.drawable.ic_pdf
                        "doc", "docx" -> R.drawable.ic_doc
                        "xls", "xlsx" -> R.drawable.ic_xls
                        "txt" -> R.drawable.ic_txt
                        "ppt", "pptx" -> R.drawable.ic_ppt
                        else -> R.drawable.ic_null_file
                    }
                ),
                contentDescription = "Файл",
                modifier = Modifier.size(50.dp),
                tint = Color.Unspecified
            )
            Spacer(Modifier.width(8.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = file.fileName,
                    color = AppColors.Secondary,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = file.fileSize,
                    color = AppColors.SecondaryTransparent,
                    fontSize = 14.sp
                )
            }
        }
    }
}


@Composable
fun FileAttachmentView(file: FileAttachment, onRemove: (() -> Unit)? = null) {

    val extension = file.fileName.substringAfterLast('.', "").lowercase()
    val isImage = extension in setOf("png", "jpg", "jpeg")

    Column(
        modifier = Modifier
            .width(90.dp)
            .background(AppColors.OnBackground, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {

        Icon(
            painter = painterResource(R.drawable.ic_close),
            contentDescription = "Удалить",
            tint = AppColors.Primary,
            modifier = Modifier
                .size(20.dp)
                .align(Alignment.End)
                .clickable { onRemove?.invoke() }
        )

        if (isImage) {
            AsyncImage(
                model = file.uri,
                contentDescription = "Изображение",
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.ic_jpg)
            )
        } else {
            Icon(
                painter = painterResource(
                    when (extension) {
                        "pdf" -> R.drawable.ic_pdf
                        "doc", "docx" -> R.drawable.ic_doc
                        "xls", "xlsx" -> R.drawable.ic_xls
                        "txt" -> R.drawable.ic_txt
                        "ppt", "pptx" -> R.drawable.ic_ppt
                        else -> R.drawable.ic_null_file
                    }
                ),
                contentDescription = "Файл",
                modifier = Modifier.size(50.dp),
                tint = Color.Unspecified
            )
        }

        Text(
            text = file.fileName,
            color = AppColors.Secondary,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        Text(
            text = file.fileSize,
            color = AppColors.Secondary.copy(alpha = 0.7f),
            fontSize = 12.sp,
        )
    }
}

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

private fun sendMessage(
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
