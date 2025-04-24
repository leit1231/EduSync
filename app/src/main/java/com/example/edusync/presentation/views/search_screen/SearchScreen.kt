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
import com.example.edusync.data.remote.dto.TeacherInitialsResponse
import com.example.edusync.domain.model.group.Group
import com.example.edusync.presentation.components.custom_text_field.search_field.SearchField
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.viewModels.search.SearchViewModel
import com.example.edusync.presentation.views.search_screen.components.SearchResultItem

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onGroupSelected: (Group) -> Unit,
    onTeacherSelected: (TeacherInitialsResponse) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val filteredGroups by viewModel.filteredGroups.collectAsState()
    val filteredTeachers by viewModel.filteredTeachers.collectAsState()
    val isTeacherMode = viewModel.isTeacherMode

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchField(
            value = searchQuery,
            onValueChange = { query ->
                viewModel.updateSearchQuery(query)
            },
            onSearch = {
                viewModel.performSearch()
            },
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
            if (isTeacherMode) {
                LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                    items(filteredTeachers) { teacher ->
                        SearchResultItem(
                            name = teacher.initials,
                            onClick = { onTeacherSelected(teacher) }
                        )
                    }
                }
            } else {
                LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                    items(filteredGroups) { group ->
                        SearchResultItem(
                            name = group.name,
                            onClick = { onGroupSelected(group) }
                        )
                    }
                }
            }
        }
    }
}
