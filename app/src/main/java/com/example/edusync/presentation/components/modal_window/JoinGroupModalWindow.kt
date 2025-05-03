package com.example.edusync.presentation.components.modal_window

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edusync.presentation.components.custom_text_field.generic_text_field.GenericTextField
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography

@Composable
fun JoinGroupModalWindow(
    modifier: Modifier = Modifier,
    onJoin: (String) -> Unit
) {
    var code by remember { mutableStateOf("") }

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
                text = "Присоединиться к группе",
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = AppColors.Secondary,
                style = AppTypography.title.copy(fontSize = 24.sp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            GenericTextField(
                value = code,
                onValueChange = { newValue -> code = newValue },
                label = "Введите код",
                isError = false,
                errorMessage = "",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onJoin(code) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Присоединиться",
                    style = AppTypography.body1.copy(fontSize = 14.sp),
                )
            }
        }
    }
}