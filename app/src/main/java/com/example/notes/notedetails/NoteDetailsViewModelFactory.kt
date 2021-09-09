package com.example.notes.notedetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.notes.database.NotesDatabaseDAO

class NoteDetailsViewModelFactory(
        private val noteId: Long = -1,
        private val dataSource: NotesDatabaseDAO) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteDetailsViewModel::class.java)) {
            return NoteDetailsViewModel(noteId,dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}