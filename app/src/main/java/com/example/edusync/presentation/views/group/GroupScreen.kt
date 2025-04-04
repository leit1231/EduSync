package com.example.edusync.presentation.views.group

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.edusync.R
import com.example.edusync.presentation.components.custom_text_field.search_field.SearchField
import com.example.edusync.presentation.components.modal_window.CreateCodeToJoinGroupWindow
import com.example.edusync.presentation.components.modal_window.CreateNotificationWindow
import com.example.edusync.presentation.components.modal_window.DeleteGroupExitAccountWindow
import com.example.edusync.presentation.navigation.Destination
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.viewModels.group.FileAttachment
import com.example.edusync.presentation.viewModels.group.GroupViewModel
import com.example.edusync.presentation.views.group.components.survey.CreatePollDialog
import com.example.edusync.presentation.views.group.components.popUp.ParticipantsPopup
import com.example.edusync.presentation.views.group.components.topBar.SelectionTopBar
import com.example.edusync.presentation.views.group.components.chatBubble.ChatBubble
import com.example.edusync.presentation.views.group.components.messageInput.MessageInput
import com.example.edusync.presentation.views.group.components.popUp.ShowGroupDropdownMenu
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupScreen(groupName: Destination.GroupScreen) {

    val viewModel: GroupViewModel = koinViewModel()
    val messages by viewModel.messages.observeAsState(emptyList())
    val subjectName = groupName.name

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
                            ) { viewModel.goBack() }
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