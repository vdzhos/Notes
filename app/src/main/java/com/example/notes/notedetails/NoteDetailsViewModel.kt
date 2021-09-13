package com.example.notes.notedetails

import androidx.lifecycle.*
import com.example.notes.database.Note
import com.example.notes.database.NotesDatabaseDAO
import kotlinx.coroutines.*

class NoteDetailsViewModel(
    val noteId: Long,
    private val dataSource: NotesDatabaseDAO) : ViewModel() {

    val note: LiveData<Note> = dataSource.get(noteId)

    fun updateNote(noteTitle: String, noteText: String){
        viewModelScope.launch {
            val note = Note(noteId = noteId, title = noteTitle, note = noteText)
            dataSource.update(note)
        }
//        _updateStatus.value = true
//        result.await()
//        _updateStatus.value = false
    }


}