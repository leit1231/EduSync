package com.example.edusync.presentation.views.main.component.select_group

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edusync.R
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography

@Composable
fun SelectGroupTopBar(
    selectedGroup: String?,
    selectedTeacher: String?,
    onGroupClick: () -> Unit,
    onTeacherClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(
                color = AppColors.Background,
                shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
            )
            .padding(vertical = 16.dp, horizontal = 16.dp)
    ) {
        val isGroupSelected = !selectedGroup.isNullOrBlank()

        Box(
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .background(
                    color = if (isGroupSelected) AppColors.Primary else AppColors.Background,
                    shape = RoundedCornerShape(20.dp)
                )
                .border(
                    width = 1.dp,
                    color = AppColors.Primary,
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable { onGroupClick() },
            contentAlignment = Alignment.Center
        ) {
            if (isGroupSelected) {
                Text(
                    text = selectedGroup ?: "",
                    style = AppTypography.body1.copy(fontSize = 16.sp),
                    color = AppColors.Background,
                    textAlign = TextAlign.Center
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "Выбрать",
                        tint = AppColors.Secondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Группа",
                        style = AppTypography.body1.copy(fontSize = 16.sp),
                        color = AppColors.Secondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        val isTeacherSelected = !selectedTeacher.isNullOrBlank()

        Box(
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .background(
                    color = if (isTeacherSelected) AppColors.Primary else AppColors.Background,
                    shape = RoundedCornerShape(20.dp)
                )
                .border(
                    width = 1.dp,
                    color = AppColors.Primary,
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable { onTeacherClick() },
            contentAlignment = Alignment.Center
        ) {
            if (isTeacherSelected) {
                Text(
                    text = selectedTeacher ?: "",
                    style = AppTypography.body1.copy(fontSize = 16.sp),
                    color = AppColors.Background,
                    textAlign = TextAlign.Center
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "Выбрать",
                        tint = AppColors.Secondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Преподаватель",
                        style = AppTypography.body1.copy(fontSize = 16.sp),
                        color = AppColors.Secondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}