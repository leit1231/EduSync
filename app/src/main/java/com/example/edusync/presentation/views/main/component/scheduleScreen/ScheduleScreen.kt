package com.example.edusync.presentation.views.main.component.scheduleScreen

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.edusync.presentation.theme.ui.AppColors
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import com.example.edusync.R
import com.example.edusync.presentation.components.modal_window.CreateEditPairDialog
import com.example.edusync.presentation.components.modal_window.CreateReminder
import com.example.edusync.presentation.viewModels.mainScreen.MainScreenViewModel
import com.example.edusync.presentation.views.main.shedule.PairItem
import com.example.edusync.presentation.views.main.shedule.Schedule
import com.example.edusync.presentation.views.main.component.dateItem.DateItem
import com.example.edusync.presentation.views.main.component.pair.PairItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScheduleLayout(
    data: Schedule,
    viewModel: MainScreenViewModel,
    onEditClick: (PairItem) -> Unit,
    onDeleteClick: (PairItem) -> Unit
) {
    val visible = remember { MutableTransitionState(false) }
    val pagerState = rememberPagerState { data.days.size }
    val scope = rememberCoroutineScope()
    val isEditMode by viewModel.isEditMode.collectAsState()
    val selectedPair = viewModel.selectedPair.collectAsState().value
    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                stickyHeader {
                    val width = remember { mutableStateOf(0.dp) }
                    val density = LocalDensity.current
                    Column(
                        modifier = Modifier.onGloballyPositioned {
                            width.value = with(density) { it.size.width.toDp() }
                        }
                    ) {
                        val popUpExpanded = remember { mutableStateOf(false) }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        ) {
                            DateItem(
                                day = data.days[page],
                                onClick = { popUpExpanded.value = true },
                                onLeftClick = {
                                    if (page > 0) scope.launch { pagerState.scrollToPage(page - 1) }
                                },
                                onRightClick = {
                                    if (page < data.days.size - 1) scope.launch { pagerState.scrollToPage(page + 1) }
                                },
                                modifier = Modifier.align(Alignment.Center)
                            )

                            Icon(
                                painter = painterResource(
                                    id = if (isEditMode) R.drawable.ic_edit_off else R.drawable.ic_change
                                ),
                                contentDescription = "Toggle edit mode",
                                tint = AppColors.Secondary,
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .size(30.dp)
                                    .clickable { viewModel.toggleEditMode() }
                                    .padding(top = 4.dp, end = 4.dp)
                            )

                            if (selectedPair != null && !isEditMode) {
                                CreateEditPairDialog(
                                    currentDate = selectedPair.isoDateStart.substring(0, 10),
                                    pair = selectedPair,
                                    onSave = { updatedPair ->
                                        viewModel.updatePair(updatedPair)
                                        viewModel.setSelectedPair(null)
                                    },
                                    onDismiss = { viewModel.setSelectedPair(null) }
                                )
                            }
                        }

                        DatesPopup(
                            expanded = popUpExpanded.value,
                            onDismissRequest = { popUpExpanded.value = false },
                            list = data,
                            onItemClick = { index ->
                                scope.launch {
                                    popUpExpanded.value = false
                                    delay(100)
                                    pagerState.scrollToPage(index)
                                }
                            },
                            page,
                            width.value
                        )
                    }
                }

                items(items = data.days[page].pairs) { pair ->
                    var showReminder by remember { mutableStateOf(false) }
                    PairItem(
                        pair = pair,
                        isEditMode = isEditMode,
                        onEditClick = onEditClick,
                        onDeleteClick = onDeleteClick,
                        scrollInProgress = pagerState.isScrollInProgress,
                        onReminderClick = { showReminder = true }
                    )

                    if (showReminder) {
                        CreateReminder(
                            pair = pair,
                            onDismiss = { showReminder = false },
                            onSave = { p, text -> viewModel.saveReminder(p, text) }
                        )
                    }
                }
            }
        }
        if (selectedPair != null) {
            CreateEditPairDialog(
                currentDate = selectedPair.isoDateStart.substring(0, 10),
                pair = selectedPair,
                onSave = { updatedPair ->
                    viewModel.updatePair(updatedPair)
                    viewModel.setSelectedPair(null)
                },
                onDismiss = { viewModel.setSelectedPair(null) }
            )
        }

        if (isEditMode) {
            FloatingActionButton(
                onClick = {
                    val currentDate = data.days[pagerState.currentPage].isoDateDay
                    viewModel.createNewPair(currentDate)
                    showAddDialog = true
                },
                containerColor = AppColors.Primary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить пару", tint = AppColors.Background)
            }
        }
    }

    if (showAddDialog) {
        CreateEditPairDialog(
            currentDate = data.days[pagerState.currentPage].isoDateDay,
            onSave = { newPair ->
                viewModel.addPair(newPair)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    DisposableEffect(key1 = visible) {
        scope.launch {
            delay(150)
            visible.targetState = true
        }
        onDispose {}
    }
}