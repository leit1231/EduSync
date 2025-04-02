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
                    (Group("Экономика в профессиональной деятельности", "Григорьева Л.Ф.")),
                    (Group("Программирование", "Григорьева Л.Ф.")),
                    (Group("Тест", "Григорьева Л.Ф.")),
                    (Group("ПОПД", "Григорьева Л.Ф."))
                )
            )
        }
    }
}

data class Group(val name: String, val teacher: String)