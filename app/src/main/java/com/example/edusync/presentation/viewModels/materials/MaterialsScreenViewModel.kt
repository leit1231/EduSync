package com.example.edusync.presentation.viewModels.materials

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class MaterialsScreenViewModel : ViewModel() {
    private val _groups = mutableStateListOf<Group>()
    val groups: List<Group> get() = _groups

    init {
        loadGroups()
    }

    private fun loadGroups() {
        val hasGroups = true
        if (hasGroups) {
            _groups.addAll(
                listOf(

                )
            )
        }
    }
}

data class Group(val name: String, val teacher: String)