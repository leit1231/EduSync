package com.example.edusync.presentation.views.favorities

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edusync.common.Constants
import com.example.edusync.presentation.components.custom_text_field.search_field.SearchField
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography
import com.example.edusync.presentation.viewModels.favorite.FavoritesViewModel
import com.example.edusync.presentation.views.favorities.component.FileItemView
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoritiesScreen() {
    val context = LocalContext.current
    val viewModel: FavoritesViewModel = koinViewModel()
    val searchQuery = remember { mutableStateOf("") }
    val displayedFiles by viewModel.displayedFiles.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadFavorites(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Избранное",
            style = AppTypography.title.copy(fontSize = 24.sp),
            color = AppColors.Secondary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))
        SearchField(
            label = "Поиск файла по названию",
            value = searchQuery.value,
            onValueChange = { newValue ->
                searchQuery.value = newValue
                viewModel.filterFavorites(newValue)
            },
            onSearch = {
            },
            imeAction = ImeAction.Search,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.height(24.dp))
        if (displayedFiles.isEmpty()) {
            EmptyFavoritesScreen()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(displayedFiles, key = { it.id }) { file ->
                    FileItemView(
                        file = file,
                        onFavoriteToggle = {
                            coroutineScope.launch {
                                viewModel.toggleFavorite(file, context)
                            }
                        },
                        onDownloadToggle = { viewModel.toggleDownload(file, context) },
                        onFileOpen = {
                            viewModel.openFile(file, context)
                        }
                    )
                }
            }
        }
    }
}