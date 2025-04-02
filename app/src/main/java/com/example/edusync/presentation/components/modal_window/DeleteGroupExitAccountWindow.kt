package com.example.edusync.presentation.components.modal_window

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.edusync.R
import com.example.edusync.common.NavRoutes
import com.example.edusync.presentation.components.custom_text_field.read_only_text_field.ReadOnlyTextField
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography

@Composable
fun DeleteGroupExitAccountWindow(
    navController: NavController,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    isTeacher: Boolean = true
) {

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.Background.copy(alpha = 0.9f))
            .clickable { onDismiss() }
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.9f)
                .background(
                    color = AppColors.Background,
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    border = BorderStroke(1.dp, AppColors.Primary),
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable { }
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = if (isTeacher) "Удалить группу?" else "Выйти из группы?",
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = AppColors.Secondary,
                    style = AppTypography.title.copy(fontSize = 16.sp)
                )
                Text(
                    text = if (isTeacher) "Вы точно хотите удалить группу? Вы и ваши ученики потеряют доступ ко всем сообщениям и файлам, которые вы присылали в данный чат" else "Вы точно хотите выйти из группы, вам придётся попросить преподавателя чтобы опять войти в группу?",
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = AppColors.Secondary,
                    style = AppTypography.body1.copy(fontSize = 14.sp),
                    modifier = Modifier.padding(vertical = 10.dp)
                )
                Row {
                    Button(
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
                    ) {
                        Text(
                            text = "Отменить",
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Left,
                            color = AppColors.Background,
                            style = AppTypography.body1.copy(fontSize = 14.sp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    if (isTeacher) {
                        Button(
                            onClick = {
                                onDismiss()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            border = BorderStroke(1.dp, color = AppColors.Error)
                        ) {
                            Text(
                                text = "Удалить группу",
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Left,
                                color = AppColors.Error,
                                style = AppTypography.body1.copy(fontSize = 14.sp)
                            )
                        }
                    } else {
                        Button(
                            onClick = {
                                onDismiss()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            border = BorderStroke(1.dp, color = AppColors.Error)
                        ) {
                            Text(
                                text = "Выйти",
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Left,
                                color = AppColors.Error,
                                style = AppTypography.body1.copy(fontSize = 14.sp)
                            )
                        }
                    }
                }
            }
        }
    }
}