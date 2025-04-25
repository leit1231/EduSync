package com.example.edusync.data

object SelectedScheduleStorage {
    var selectedGroupId: Int? = null
    var selectedGroupName: String? = null

    var selectedTeacherId: Int? = null
    var selectedTeacherInitials: String? = null

    fun setGroup(id: Int, name: String) {
        selectedGroupId = id
        selectedGroupName = name

        selectedTeacherId = null
        selectedTeacherInitials = null
    }

    fun setTeacher(id: Int, initials: String) {
        selectedTeacherId = id
        selectedTeacherInitials = initials

        selectedGroupId = null
        selectedGroupName = null
    }

    fun clearGroup() {
        selectedGroupId = null
        selectedGroupName = null
    }

    fun clearTeacher() {
        selectedTeacherId = null
        selectedTeacherInitials = null
    }
}