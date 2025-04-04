package com.example.edusync.presentation.viewModels.materials

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusync.presentation.navigation.Destination
import com.example.edusync.presentation.navigation.Navigator
import kotlinx.coroutines.launch

class MaterialsScreenViewModel(private val navigator: Navigator) : ViewModel() {
    private val _groups = mutableStateListOf<Group>()
    val groups: List<Group> get() = _groups

    init {
        loadGroups()
    }

    fun goToCreateGroup(){
        viewModelScope.launch {
            navigator.navigate(
                destination = Destination.CreateGroupScreen
            )
        }
    }

    fun goToGroup(group: String){
        viewModelScope.launch {
            navigator.navigate(
                destination = Destination.GroupScreen(group)
            )
        }
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