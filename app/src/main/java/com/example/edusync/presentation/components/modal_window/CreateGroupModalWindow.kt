package com.example.edusync.presentation.components.modal_window

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edusync.R
import com.example.edusync.presentation.components.custom_text_field.read_only_text_field.ReadOnlyTextField
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography

@Composable
fun CreateGroupModalWindow(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val link by remember { mutableStateOf("https://fsaljkflhsaf") }

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
                text = "Ссылка создана",
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = AppColors.Secondary,
                style = AppTypography.title.copy(fontSize = 24.sp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            ReadOnlyTextField(
                value = link,
                label = "Ссылка",
                trailingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_link),
                        contentDescription = "Копировать ссылку",
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable {
                                clipboardManager.setText(AnnotatedString(link))
                            },
                        tint = AppColors.Secondary
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, link)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, "Поделиться ссылкой")
                    context.startActivity(shareIntent)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Поделиться",
                    style = AppTypography.body1.copy(fontSize = 14.sp),
                )
            }
        }
    }
}