package com.example.edusync.presentation.views.favorities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.edusync.presentation.components.custom_text_field.search_field.SearchField
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography
import com.example.edusync.presentation.viewModels.favorite.FavoritesViewModel
import com.example.edusync.presentation.views.favorities.component.FileItemView
import com.example.edusync.presentation.views.navigation_menu.NavigationMenu
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoritiesScreen(navController: NavHostController) {
    val isTeacher = true
    val viewModel: FavoritesViewModel = koinViewModel()
    val currentRoute =
        navController.currentBackStackEntry?.destination?.route ?: "favorities_screen"
    val favoriteFiles by remember { derivedStateOf { viewModel.favoriteFiles } }
    val searchQuery = remember { mutableStateOf("") }
    val displayedFiles by remember { derivedStateOf { viewModel.displayedFiles } }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = AppColors.Background)
            )
        },
        bottomBar = {
            NavigationMenu(navController, currentRoute)
        },
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(paddingValues)
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
                    LazyColumn(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)) {
                        items(favoriteFiles, key = { it.id }) { file ->
                            FileItemView(
                                file = file,
                                onFavoriteToggle = { viewModel.toggleFavorite(file) },
                                onDownloadToggle = { viewModel.toggleDownload(file) },
                                isTeacher = isTeacher
                            )
                        }
                    }
                }
                if (isTeacher == true) {
                    Button(
                        onClick = {
                            //TODO
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.Primary,
                            disabledContainerColor = AppColors.SecondaryTransparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = "Загрузить файл",
                            style = AppTypography.body1.copy(fontSize = 14.sp),
                            color = AppColors.Background
                        )
                    }
                }
            }
        })
}