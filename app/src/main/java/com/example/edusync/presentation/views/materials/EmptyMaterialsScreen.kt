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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import ru.eduHub.edusync.R
import com.example.edusync.presentation.components.modal_window.JoinGroupModalWindow
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography
import com.example.edusync.presentation.viewModels.materials.MaterialsScreenViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun EmptyMaterialsScreen(isTeacher: Boolean) {
    val viewModel = koinViewModel<MaterialsScreenViewModel>()
    var isModalOpen by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

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
                stringResource(R.string.no_teacher_groups)
            else
                stringResource(R.string.no_student_group),
            textAlign = TextAlign.Center,
            style = AppTypography.body1.copy(fontSize = 16.sp),
            color = AppColors.Secondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (isTeacher) {
            Button(
                onClick = {viewModel.goToCreateGroup()},
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.add_group),
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
                    text = stringResource(R.string.join_the_group),
                    style = AppTypography.body1.copy(fontSize = 14.sp),
                    color = AppColors.Background
                )
            }
        }
    }

    if (isModalOpen) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = {
                isModalOpen = false
                errorMessage = ""
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            JoinGroupModalWindow(
                modifier = Modifier.fillMaxWidth(),
                errorMessage = errorMessage,
                onJoin = { code ->
                    viewModel.joinByInvite(
                        inviteCode = code,
                        onSuccess = {
                            isModalOpen = false
                            errorMessage = ""
                        },
                        onError = { msg ->
                            errorMessage = msg
                        }
                    )
                }
            )
        }
    }
}
