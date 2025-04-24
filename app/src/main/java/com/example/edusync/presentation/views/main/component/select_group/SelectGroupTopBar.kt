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
    selectedGroup: String,
    selectedTeacher: String?,
    isTeacher: Boolean,
    onGroupClick: (Boolean) -> Unit,
) {
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
                    color = AppColors.Primary,
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable { onGroupClick(isTeacher) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isTeacher) selectedTeacher ?: "Выбрать" else selectedGroup,
                style = AppTypography.body1.copy(fontSize = 16.sp),
                color = AppColors.Background,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .background(
                    color = AppColors.Background,
                    shape = RoundedCornerShape(20.dp)
                )
                .border(
                    width = 1.dp,
                    color = AppColors.Primary,
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable { onGroupClick(!isTeacher) },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = "Выбрать",
                    tint = AppColors.Secondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isTeacher) "Группа" else "Преподаватель",
                    style = AppTypography.body1.copy(fontSize = 16.sp),
                    color = AppColors.Secondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}