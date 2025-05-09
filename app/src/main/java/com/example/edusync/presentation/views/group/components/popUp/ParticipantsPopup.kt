package com.example.edusync.presentation.views.group.components.popUp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.example.edusync.R
import com.example.edusync.domain.model.chats.ChatUser
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography

@Composable
fun ParticipantsPopup(
    onDismiss: () -> Unit,
    participants: List<ChatUser>,
    title: String,
    onRemove: (Int) -> Unit,
    isTeacher: Boolean,
    currentUserId: Int
) {
    Popup(
        alignment = Alignment.TopStart,
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 550.dp)
                .padding(16.dp)
                .wrapContentHeight(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(Modifier.background(AppColors.Background)) {
                Text(
                    text = title,
                    color = AppColors.Secondary,
                    style = AppTypography.body1.copy(fontSize = 16.sp),
                    modifier = Modifier.padding(20.dp)
                )
                Text(
                    text = "Участников: ${participants.size}",
                    color = Color.White.copy(alpha = 0.7f),
                    style = AppTypography.body1.copy(fontSize = 14.sp),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(Modifier.height(8.dp))

                LazyColumn(Modifier.fillMaxWidth()) {
                    items(participants) { participant ->
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = participant.fullName,
                                    color = AppColors.Secondary,
                                    fontSize = 16.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                if (isTeacher && participant.id != currentUserId) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_remove_person),
                                        contentDescription = "Удалить участника",
                                        tint = AppColors.Error,
                                        modifier = Modifier.clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) { onRemove(participant.id) }
                                    )
                                }
                            }
                            HorizontalDivider(
                                color = AppColors.OnBackground,
                                thickness = 4.dp
                            )
                        }
                    }
                }
            }
        }
    }
}