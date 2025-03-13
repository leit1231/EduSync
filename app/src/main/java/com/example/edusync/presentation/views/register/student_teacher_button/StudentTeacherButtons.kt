package com.example.edusync.presentation.views.register.student_teacher_button

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.edusync.R
import com.example.edusync.presentation.theme.ui.AppColors

@Composable
fun RoleSelectionButtons(
    selected: Boolean,
    onStudentClick: () -> Unit,
    onTeacherClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RoleButton(
            text = "Студент",
            isSelected = !selected,
            onClick = onStudentClick,
            icon = painterResource(R.drawable.ic_person),
            modifier = Modifier
                .weight(1f)
                .height(50.dp)
        )
        RoleButton(
            text = "Преподаватель",
            isSelected = selected,
            onClick = onTeacherClick,
            icon = painterResource(R.drawable.ic_teacher),
            modifier = Modifier
                .weight(1f)
                .height(50.dp)
        )
    }
}

@Composable
fun RoleButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: Painter,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) AppColors.Primary else AppColors.SecondaryTransparent
    val backgroundColor = if (isSelected) AppColors.Background else Color.Transparent
    val textColor = if (isSelected) AppColors.Primary else AppColors.SecondaryTransparent

    Box(
        modifier = modifier
            .height(50.dp)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = textColor,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}