package com.example.edusync.presentation.views.search_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.edusync.presentation.components.custom_text_field.search_field.SearchField
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.viewModels.search.SearchViewModel
import com.example.edusync.presentation.views.search_screen.components.SearchResultItem

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onGroupSelected: (String) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchField(
            value = searchQuery,
            onValueChange = {
                viewModel.onQueryChanged(it)
                viewModel.performSearch()
            },
            onSearch = { viewModel.performSearch() },
            imeAction = ImeAction.Search,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isSearching) {
            CircularProgressIndicator(
                color = AppColors.Primary,
                modifier = Modifier.size(48.dp)
            )
        } else {
            LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                items(searchResults) { name ->
                    SearchResultItem(
                        name = name,
                        onClick = { onGroupSelected(name) }
                    )
                }
            }
        }
    }
}