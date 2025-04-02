package com.example.edusync.presentation.views.group.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.edusync.R

@Composable
fun ShowGroupDropdownMenu(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    isTeacher: Boolean = false,
    onAddStudentClick: () -> Unit,
    onToggleNotifications: () -> Unit,
    onSearchMaterialsClick: () -> Unit,
    onCreateNotificationClick: () -> Unit,
    onDeleteExitGroupClick: () -> Unit,
    onCreatePollClick: () -> Unit
) {
    val notificationsEnabled = remember { mutableStateOf(true) }
    var expanded by remember { mutableStateOf(true) }
    var parentSize by remember { mutableStateOf(IntSize.Zero) }
    var anchorPosition by remember { mutableStateOf(Offset.Zero) }
    val density = LocalDensity.current

    val menuItems = remember(notificationsEnabled.value) {
        mutableListOf<MenuItemData>().apply {
            add(MenuItemData(
                text = "Поиск материалов",
                iconRes = R.drawable.ic_search,
                onClick = {
                    onSearchMaterialsClick()
                    onDismiss()
                }
            ))
            add(MenuItemData(
                text = if (notificationsEnabled.value) "Выключить уведомления" else "Включить уведомления",
                iconRes = if (notificationsEnabled.value) R.drawable.ic_off_notification else R.drawable.ic_notification,
                onClick = {
                    notificationsEnabled.value = !notificationsEnabled.value
                    onToggleNotifications()
                }
            ))
            if (isTeacher) {
                add(MenuItemData(
                    text = "Добавить студента",
                    iconRes = R.drawable.ic_add_person,
                    onClick = {
                        onAddStudentClick()
                        onDismiss()
                    }
                ))
                add(MenuItemData(
                    text = "Создать опрос",
                    iconRes = R.drawable.ic_check,
                    onClick = {
                        onCreatePollClick()
                        onDismiss()
                    }
                ))
                add(MenuItemData(
                    text = "Создать уведомление",
                    iconRes = R.drawable.ic_add_notification,
                    onClick = {
                        onCreateNotificationClick()
                        onDismiss()
                    }
                ))
                add(MenuItemData(
                    text = "Удалить группу",
                    iconRes = R.drawable.ic_delete,
                    onClick = {
                        onDeleteExitGroupClick()
                        onDismiss()
                    }
                ))
            } else {
                add(MenuItemData(
                    text = "Выйти из группы",
                    iconRes = R.drawable.ic_exit,
                    onClick = {
                        onDeleteExitGroupClick()
                        onDismiss()
                    }
                ))
            }
        }
    }

    Box(
        modifier = modifier.onGloballyPositioned { coordinates ->
            anchorPosition = coordinates.positionInRoot()
            parentSize = coordinates.size
        }
    ) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                onDismiss()
            },
            modifier = Modifier.background(Color.DarkGray, shape = RoundedCornerShape(8.dp)).width(250.dp),
            offset = with(density) {
                DpOffset(parentSize.width.toDp(), 0.dp)
            }
        ) {
            menuItems.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.text) },
                    onClick = {
                        item.onClick()
                    },
                    trailingIcon = { Icon(painterResource(item.iconRes), null) }
                )
            }
        }
    }
}

data class MenuItemData(
    val text: String,
    val iconRes: Int,
    val onClick: () -> Unit
)
