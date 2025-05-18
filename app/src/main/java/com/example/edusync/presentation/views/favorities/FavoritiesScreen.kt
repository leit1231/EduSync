package com.example.edusync.presentation.views.favorities

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.eduHub.edusync.R
import com.example.edusync.presentation.components.custom_text_field.search_field.SearchField
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography
import com.example.edusync.presentation.viewModels.favorite.FavoritesViewModel
import com.example.edusync.presentation.views.favorities.component.FileItemView
import com.example.edusync.presentation.views.shared.StateScreen
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoritiesScreen() {
    val context = LocalContext.current
    val viewModel: FavoritesViewModel = koinViewModel()
    val searchQuery = remember { mutableStateOf("") }
    val displayedFiles by viewModel.displayedFiles.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val isLoading = viewModel.isLoading
    val hasError = viewModel.hasError

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
            text = stringResource(R.string.favorites),
            style = AppTypography.title.copy(fontSize = 24.sp),
            color = AppColors.Secondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        SearchField(
            label = stringResource(R.string.search_file),
            value = searchQuery.value,
            onValueChange = {
                searchQuery.value = it
                viewModel.filterFavorites(it)
            },
            onSearch = {},
            imeAction = ImeAction.Search
        )

        Spacer(modifier = Modifier.height(24.dp))

        Log.d("FavoritiesScreen", "isLoading = $isLoading, hasError = $hasError, displayedFiles = ${displayedFiles.size}")

        when {
            isLoading -> {
                StateScreen(isLoading = true)
            }
            hasError -> {
                StateScreen(
                    isError = true,
                    errorText = stringResource(R.string.no_favorite),
                    retryButtonText = stringResource(R.string.update),
                    onRetry = { viewModel.loadFavorites(context) }
                )
            }
            displayedFiles.isEmpty() -> {
                EmptyFavoritesScreen()
            }
            else -> {
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
                            onFileOpen = { viewModel.openFile(file, context) }
                        )
                    }
                }
            }
        }
    }
}