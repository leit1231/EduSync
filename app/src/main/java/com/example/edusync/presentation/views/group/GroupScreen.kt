package com.example.edusync.presentation.views.group

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import ru.eduHub.edusync.R
import com.example.edusync.common.Constants
import com.example.edusync.data.local.EncryptedSharedPreference
import com.example.edusync.data.remote.webSocket.WebSocketController
import com.example.edusync.presentation.components.custom_text_field.search_field.SearchField
import com.example.edusync.presentation.components.modal_window.CreateCodeToJoinGroupWindow
import com.example.edusync.presentation.components.modal_window.CreateNotificationWindow
import com.example.edusync.presentation.components.modal_window.DeleteGroupExitAccountWindow
import com.example.edusync.presentation.navigation.Destination
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.viewModels.group.GroupViewModel
import com.example.edusync.presentation.views.group.components.survey.CreatePollDialog
import com.example.edusync.presentation.views.group.components.popUp.ParticipantsPopup
import com.example.edusync.presentation.views.group.components.topBar.SelectionTopBar
import com.example.edusync.presentation.views.group.components.chatBubble.ChatBubble
import com.example.edusync.presentation.views.group.components.chatBubble.ChatItem
import com.example.edusync.presentation.views.group.components.messageInput.MessageInput
import com.example.edusync.presentation.views.group.components.popUp.ShowGroupDropdownMenu
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupScreen(groupId: Destination.GroupScreen, groupName: Destination.GroupScreen) {
    val viewModel: GroupViewModel = koinViewModel()
    val pagingMessages = viewModel.messageFlow.collectAsLazyPagingItems()
    val context = LocalContext.current
    val isTeacher = Constants.getIsTeacher(context)
    val currentUserId = EncryptedSharedPreference(context).getUser()?.id ?: -1

    val chatId = groupId.id
    val subjectName = groupName.name
    val participants by viewModel.participants.observeAsState(emptyList())
    val inviteCode by viewModel.inviteCode.observeAsState()
    val isInSelectionMode by viewModel.isInSelectionMode.observeAsState(false)
    val highlightedMessage by viewModel.highlightedMessage.observeAsState()
    val chatItems by viewModel.chatItems.collectAsState()

    var showParticipantsPopup by remember { mutableStateOf(false) }
    var showGroupPopup by remember { mutableStateOf(false) }
    var showCreateNotificationDialog by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showDeleteExitDialog by remember { mutableStateOf(false) }
    var showCreatePollDialog by remember { mutableStateOf(false) }

    val swipeThreshold = 200f
    var offsetX by remember { mutableFloatStateOf(0f) }
    var isSwiping by remember { mutableStateOf(false) }

    val animatedOffsetX by animateFloatAsState(
        targetValue = if (isSwiping) offsetX else 0f,
        label = "SwipeAnimation"
    )

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var showScrollToBottom by remember { mutableStateOf(false) }

    LaunchedEffect(isSearchActive) {
        snapshotFlow { pagingMessages.itemSnapshotList.items }
            .collectLatest { items ->
                viewModel.updateVisibleMessages(items)
            }
    }


    LaunchedEffect(pagingMessages.itemSnapshotList.items) {
        viewModel.updateVisibleMessages(pagingMessages.itemSnapshotList.items)
    }

    LaunchedEffect(highlightedMessage) {
        highlightedMessage?.let { targetMessage ->
            val snapshot = pagingMessages.itemSnapshotList.items
            val index = snapshot.indexOfFirst { it.id == targetMessage.id }
            if (index != -1) {
                coroutineScope.launch {
                    listState.animateScrollToItem(index)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.initChatSession(chatId = chatId, userId = currentUserId, context = context)
    }

    DisposableEffect(Unit) {
        onDispose {
            WebSocketController.unregister(chatId)
            viewModel.clearRealtimeMessages()
            viewModel.resetChatSession()
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                showScrollToBottom = index > 2
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset { IntOffset(animatedOffsetX.roundToInt(), 0) }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { _, dragAmount ->
                        if (offsetX + dragAmount >= 0) {
                            offsetX += dragAmount
                            isSwiping = true
                        }
                    },
                    onDragEnd = {
                        if (offsetX > swipeThreshold) {
                            offsetX = 1000f
                            isSwiping = true
                            viewModel.goBack()
                        } else {
                            offsetX = 0f
                            isSwiping = false
                        }
                    }
                )
            }
    ) {
        Scaffold(
            topBar = {
                when {
                    isInSelectionMode -> SelectionTopBar(viewModel)
                    isSearchActive -> {
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
                                modifier = Modifier.clickable {
                                    isSearchActive = false
                                    viewModel.exitSearchMode(chatId, context)
                                }
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            SearchField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                onSearch = {
                                    viewModel.searchMessages(chatId, searchQuery, context)
                                },
                                imeAction = ImeAction.Search
                            )
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    else -> {
                        TopAppBar(
                            title = {
                                Text(
                                    text = subjectName,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showParticipantsPopup = true }
                                )
                            },
                            navigationIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_back),
                                    contentDescription = "Назад",
                                    tint = AppColors.Primary,
                                    modifier = Modifier.clickable { viewModel.goBack() }
                                )
                            },
                            actions = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_more_vert),
                                    contentDescription = "Меню",
                                    tint = AppColors.Primary,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clickable { showGroupPopup = true }
                                )
                            }
                        )
                    }
                }
            },
            content = { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(AppColors.Background)
                    ) {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.weight(1f),
                            reverseLayout = true
                        ) {
                            items(chatItems) { item ->
                                val message = (item as ChatItem.MessageItem).message
                                ChatBubble(
                                    message = message,
                                    viewModel = viewModel,
                                    context = context,
                                    chatId = chatId,
                                    allMessages = chatItems.mapNotNull { (it as? ChatItem.MessageItem)?.message },
                                    isTeacher = isTeacher
                                )
                            }
                        }
                        MessageInput(viewModel, chatId = chatId)
                    }

                    if (showScrollToBottom) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_down),
                            contentDescription = "Scroll to Bottom",
                            tint = AppColors.Primary,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 16.dp, bottom = 80.dp)
                                .size(40.dp)
                                .clickable {
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(0)
                                    }
                                }
                        )
                    }
                }
            }
        )

        // Модальные окна — как раньше
        if (showParticipantsPopup) {
            ParticipantsPopup(
                onDismiss = { showParticipantsPopup = false },
                participants = participants,
                title = subjectName,
                onRemove = { userId -> viewModel.removeParticipant(chatId, userId) },
                isTeacher = isTeacher,
                currentUserId = currentUserId
            )
        }

        if (showGroupPopup) {
            ShowGroupDropdownMenu(
                onDismiss = { showGroupPopup = false },
                modifier = Modifier.width(200.dp),
                isTeacher = isTeacher,
                onAddStudentClick = {
                    viewModel.refreshInviteCode(chatId,
                        onSuccess = { viewModel.setInviteCode(it) },
                        onError = {}
                    )
                },
                onToggleNotifications = { notificationsEnabled = !notificationsEnabled },
                onSearchMaterialsClick = { isSearchActive = true },
                onCreateNotificationClick = { showCreateNotificationDialog = true },
                onDeleteExitGroupClick = { showDeleteExitDialog = true },
                onCreatePollClick = { showCreatePollDialog = true }
            )
        }

        if (showDeleteExitDialog) {
            DeleteGroupExitAccountWindow(
                isTeacher = isTeacher,
                onDismiss = { showDeleteExitDialog = false },
                onConfirmClick = {
                    if (isTeacher) {
                        viewModel.deleteChat(chatId, { viewModel.goBack() }, {})
                    } else {
                        viewModel.leaveChat(chatId, { viewModel.goBack() }, {})
                    }
                }
            )
        }

        inviteCode?.let {
            CreateCodeToJoinGroupWindow(it) { viewModel.clearInviteCode() }
        }

        if (showCreateNotificationDialog) {
            CreateNotificationWindow { showCreateNotificationDialog = false }
        }

        if (showCreatePollDialog) {
            CreatePollDialog(
                onDismiss = { showCreatePollDialog = false },
                onPollCreated = { q, o -> viewModel.sendPoll(chatId, q, o) }
            )
        }
    }
}