package com.example.edusync.presentation.components.modal_window

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography

@Composable
fun LogoutWindow(onClick: () -> Unit, onDismiss: () -> Unit, modifier: Modifier = Modifier) {

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .background(
                color = AppColors.Background,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                border = BorderStroke(1.dp, AppColors.Primary),
                shape = RoundedCornerShape(16.dp)
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .background(
                    color = AppColors.Background,
                    shape = RoundedCornerShape(16.dp)
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Выйти из аккаунта?",
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = AppColors.Secondary,
                style = AppTypography.title.copy(fontSize = 24.sp)
            )
            Text(
                text = "После выхода вам придется снова авторизоваться для получения полного функционала сервиса",
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = AppColors.Secondary,
                style = AppTypography.body1.copy(fontSize = 14.sp),
                modifier = Modifier.padding(vertical = 14.dp)
            )
            Row {
                Button(onClick = {onDismiss()},
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)) {
                    Text(
                        text = "Отменить",
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Left,
                        color = AppColors.Background,
                        style = AppTypography.body1.copy(fontSize = 14.sp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { onClick() },
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