package com.example.edusync.presentation.views.materials

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.edusync.common.Constants
import com.example.edusync.presentation.components.custom_text_field.search_field.SearchField
import com.example.edusync.presentation.components.modal_window.JoinGroupModalWindow
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography
import com.example.edusync.presentation.viewModels.materials.MaterialsScreenViewModel
import com.example.edusync.presentation.views.materials.component.ChatItem
import org.koin.androidx.compose.koinViewModel

@Composable
fun MaterialsScreen() {

    val viewModel: MaterialsScreenViewModel = koinViewModel()
    val lifecycleOwner = LocalLifecycleOwner.current
    val chats by viewModel.filteredChats
    val searchQuery = viewModel.searchQuery
    val context = LocalContext.current
    val isTeacher = Constants.getIsTeacher(context)
    val isModalDialogVisible = remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val lifecycle = lifecycleOwner.lifecycle

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.connectWebSocketIfNeeded()
                    viewModel.reloadChats()
                }
                Lifecycle.Event.ON_STOP -> {
                    viewModel.scheduleDisconnectIfIdle()
                }
                Lifecycle.Event.ON_DESTROY -> {
                    viewModel.disconnectWebSocket()
                }
                else -> {}
            }
        }

        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Материалы",
            style = AppTypography.title.copy(fontSize = 24.sp),
            color = AppColors.Secondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        SearchField(
            label = "Поиск чата",
            value = searchQuery,
            onValueChange = viewModel::updateSearchQuery,
            onSearch = {},
            imeAction = ImeAction.Search,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (chats.isEmpty()) {
            EmptyMaterialsScreen(isTeacher)
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                items(chats) { chat ->
                    ChatItem(chat = chat, viewModel = viewModel, isTeacher = isTeacher)
                }
            }
        }


        if (isTeacher) {
            Button(
                onClick = { viewModel.goToCreateGroup() },
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Text("Добавить группу", style = AppTypography.body1.copy(fontSize = 14.sp), color = AppColors.Background)
            }
        } else {
            Button(
                onClick = { isModalDialogVisible.value = true },
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Text("Присоединиться к группе", style = AppTypography.body1.copy(fontSize = 14.sp), color = AppColors.Background)
            }
        }
    }

    if (isModalDialogVisible.value) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)).clickable {
                isModalDialogVisible.value = false
            }
        ) {
            JoinGroupModalWindow(
                modifier = Modifier.align(Alignment.Center),
                onJoin = { code ->
                    viewModel.joinByInvite(
                        inviteCode = code,
                        onSuccess = { isModalDialogVisible.value = false },
                        onError = { /* TODO: показать Snackbar */ }
                    )
                }
            )

        }
    }
}