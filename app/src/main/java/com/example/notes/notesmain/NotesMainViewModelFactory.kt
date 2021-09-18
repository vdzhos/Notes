package com.example.notes.notesmain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.notes.database.NotesDatabaseDAO

class NotesMainViewModelFactory(
    private val listType: ListType,
    private val dataSource: NotesDatabaseDAO) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotesMainViewModel::class.java)) {
            return NotesMainViewModel(listType, dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}