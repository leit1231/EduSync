package com.example.edusync.presentation.views.materials

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.edusync.R
import com.example.edusync.common.NavRoutes
import com.example.edusync.presentation.components.modal_window.JoinGroupModalWindow
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography

@Composable
fun EmptyMaterialsScreen(navController: NavController) {
    var isModalOpen by remember { mutableStateOf(false) }
    val isTeacher = false

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_no_materials),
            contentDescription = "Empty Materials",
            tint = AppColors.Secondary
        )

        Text(
            text = if (isTeacher)
                "У вас пока нет групп со студентами, но вы можете создать группу и пригласить туда студентов"
            else
                "У вас пока нет группы с преподавателем, попросите его создать группу, или войдите используя специальный код",
            textAlign = TextAlign.Center,
            style = AppTypography.body1.copy(fontSize = 16.sp),
            color = AppColors.Secondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (isTeacher) {
            Button(
                onClick = { navController.navigate(NavRoutes.CreateGroupScreen.route) },
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Добавить группу",
                    style = AppTypography.body1.copy(fontSize = 14.sp),
                    color = AppColors.Background
                )
            }
        } else {
            Button(
                onClick = { isModalOpen = true },
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Присоединиться к группе",
                    style = AppTypography.body1.copy(fontSize = 14.sp),
                    color = AppColors.Background
                )
            }
        }
    }

    if (isModalOpen) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { isModalOpen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false) // Добавьте эту строку
        )
        {
            JoinGroupModalWindow(
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
