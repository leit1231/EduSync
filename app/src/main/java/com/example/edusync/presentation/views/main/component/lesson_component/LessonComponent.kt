package com.example.edusync.presentation.views.main.component.lesson_component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.viewModels.mainScreen.Lesson

@Composable
fun LessonComponent(
    lesson: Lesson,
    isTeacher: Boolean,
    onEditClick: (Lesson) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFF75F042), RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF121212))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "${lesson.id} ${lesson.title}",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "${lesson.startTime} - ${lesson.endTime}",
                color = Color.Gray
            )
            Text(
                text = lesson.room,
                color = Color.Gray
            )
            Text(
                text = lesson.teacher,
                color = Color.Gray
            )
            if (isTeacher) {
                IconButton(
                    onClick = { onEditClick(lesson) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF75F042))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchComponent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp), // Добавляем отступы по бокам
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            Button(
                onClick = { /* Действие при нажатии на кнопку "ИС-41" */ },
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
            ) {
                Text(text = "ИС-41")
            }
            TextField(
                value = "",
                onValueChange = { /* Обработка ввода текста */ },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    cursorColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(start = 16.dp), // Отступ слева для иконки
                trailingIcon = {
                    // Здесь можно добавить дополнительные иконки или элементы справа от поля
                }
            )
        }
    }
}