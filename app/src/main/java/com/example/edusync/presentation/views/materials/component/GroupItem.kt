package com.example.edusync.presentation.views.materials.component

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.eduHub.edusync.R
import com.example.edusync.data.remote.dto.ChatResponse
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography
import com.example.edusync.presentation.viewModels.materials.MaterialsScreenViewModel

@Composable
fun ChatItem(
    chat: ChatResponse,
    viewModel: MaterialsScreenViewModel,
    isTeacher: Boolean
) {

    val groupName = if (isTeacher) viewModel.getGroupNameById(chat.group_id) else null

    Log.d("ChatItem", "groupName: $groupName")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(AppColors.Background)
            .border(1.dp, AppColors.Primary, shape = RoundedCornerShape(10.dp))
            .clickable { viewModel.goToGroup(chat.id, chat.subject_name) },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 32.dp)
            ) {
                Text(
                    text = chat.subject_name,
                    color = AppColors.Secondary,
                    style = AppTypography.body1.copy(fontSize = 16.sp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isTeacher) "$groupName" else chat.owner_full_name,
                    color = AppColors.Secondary,
                    style = AppTypography.body1.copy(fontSize = 14.sp)
                )
            }

            Icon(
                painter = painterResource(R.drawable.ic_arrow_go),
                tint = AppColors.Primary,
                contentDescription = "Go",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(16.dp)
            )
        }
    }
}