package com.example.edusync.presentation.views.group.components.topBar

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.edusync.R
import com.example.edusync.presentation.theme.ui.AppTypography
import com.example.edusync.presentation.viewModels.group.GroupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionTopBar(viewModel: GroupViewModel) {

    val selectedCount by viewModel.selectedFiles.collectAsStateWithLifecycle(emptySet())

    TopAppBar(
        title = {
            Text(
                text = "Выбрано ${selectedCount.size} файлов",
                style = AppTypography.body1.copy(fontSize = 16.sp)
            )
        },
        navigationIcon = {
            IconButton(onClick = { viewModel.exitSelectionMode() }) {
                Icon(painter = painterResource(R.drawable.ic_close), contentDescription = "Закрыть")
            }
        },
        actions = {
            IconButton(onClick = { /* Логика избранного */ }) {
                Icon(painter = painterResource(R.drawable.ic_favorit_border), contentDescription = "В избранное")
            }
        }
    )
}