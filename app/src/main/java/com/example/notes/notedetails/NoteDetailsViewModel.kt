package com.example.notes.notedetails

import androidx.lifecycle.*
import com.example.notes.database.Note
import com.example.notes.database.NotesDatabaseDAO
import kotlinx.coroutines.*

class NoteDetailsViewModel(
        private val noteId: Long,
        private val dataSource: NotesDatabaseDAO) : ViewModel() {

    val note: LiveData<Note> = dataSource.get(noteId)

    private val _updateStatus = MutableLiveData<Boolean>()
    val updateStatus: LiveData<Boolean>
        get() = _updateStatus


    suspend fun updateNote(noteTitle: String, noteText: String){
        val result = viewModelScope.async {
            val note = Note(noteId = noteId, title = noteTitle, note = noteText)
            dataSource.update(note)
//            withContext(Dispatchers.IO){
//                Thread.sleep(5000)
//            }
        }
        _updateStatus.value = true
        result.await()
        _updateStatus.value = false
    }

}