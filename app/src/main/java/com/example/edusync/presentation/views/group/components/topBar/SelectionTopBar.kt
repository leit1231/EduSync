package com.example.edusync.presentation.views.group.components.topBar

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.eduHub.edusync.R
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography
import com.example.edusync.presentation.viewModels.group.GroupViewModel
import com.example.edusync.presentation.views.group.components.fileAttachment.isImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionTopBar(viewModel: GroupViewModel) {
    val selectedFiles by viewModel.selectedFiles.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()

    val nonImageFiles = remember(selectedFiles) {
        selectedFiles.filterNot { it.isImage() }
    }

    val allSelectedFav = selectedFiles
        .filterNot { it.isImage() }
        .all { file -> file.id != null && favoriteIds.contains(file.id) }



    if (nonImageFiles.isNotEmpty()) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.files_selected, nonImageFiles.size),
                    style = AppTypography.body1.copy(fontSize = 16.sp)
                )
            },
            navigationIcon = {
                IconButton(onClick = { viewModel.exitSelectionMode() }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = "Закрыть"
                    )
                }
            },
            actions = {
                IconButton(onClick = { viewModel.addOrRemoveSelectedFavorites() }) {
                    Icon(
                        painter = painterResource(
                            if (allSelectedFav) R.drawable.ic_favorit_bold else R.drawable.ic_favorit_border
                        ),
                        contentDescription = "В избранное",
                        tint = AppColors.Error
                    )
                }
            }
        )
    }
}