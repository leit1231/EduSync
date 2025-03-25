package com.example.edusync.presentation.views.main.component.select_group

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edusync.R
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography

@Composable
fun SelectGroupTopBar(
    selectedGroup: String,
    isTeacher: Boolean,
    onGroupClick: () -> Unit,
) {
    val isLeftSelected = remember { mutableStateOf(true) }

    Row(
        modifier = Modifier
            .background(
                color = AppColors.Background,
                shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
            )
            .padding(vertical = 16.dp, horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .background(
                    color = if (isLeftSelected.value) AppColors.Primary else AppColors.Background,
                    shape = RoundedCornerShape(20.dp)
                )
                .border(
                    width = 1.dp,
                    color = if (isLeftSelected.value) Color.Transparent else AppColors.Primary,
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable { isLeftSelected.value = true
                    onGroupClick()},
            contentAlignment = Alignment.Center
        ) {
            Row {
                if (!isLeftSelected.value) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "Выбрать",
                        tint = AppColors.Secondary
                    )
                }
                Text(
                    text = if (!isLeftSelected.value) {
                        if (!isTeacher) "Группа" else "Преподаватель"
                    } else {
                        if (!isTeacher) selectedGroup else "Григорьева Л.Ф."
                    },
                    style = AppTypography.body1.copy(fontSize = 16.sp),
                    color = if (!isLeftSelected.value) AppColors.Secondary else AppColors.Background
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .background(
                    color = if (!isLeftSelected.value) AppColors.Primary else AppColors.Background,
                    shape = RoundedCornerShape(20.dp)
                )
                .border(
                    width = 1.dp,
                    color = if (!isLeftSelected.value) Color.Transparent else AppColors.Primary,
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable {
                    isLeftSelected.value = false
                    onGroupClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Row {
                if (isLeftSelected.value) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "Выбрать",
                        tint = AppColors.Secondary
                    )
                }
                Text(
                    text = if (!isLeftSelected.value) {
                        if (!isTeacher) "Григорьева Л.Ф." else "ИС-11"
                    } else {
                        if (!isTeacher) "Преподаватель" else "Группа"
                    },
                    style = AppTypography.body1.copy(fontSize = 16.sp),
                    color = if (!isLeftSelected.value) AppColors.Background else AppColors.Secondary
                )
            }
        }
    }
}