package com.example.notes.notedetails

import androidx.lifecycle.*
import com.example.notes.database.Note
import com.example.notes.database.NotesDatabaseDAO
import kotlinx.coroutines.*

class NoteDetailsViewModel(
    val noteId: Long,
    private val dataSource: NotesDatabaseDAO) : ViewModel() {

    init {
        println(noteId)
    }

    val note: LiveData<Note> = dataSource.getLiveData(noteId)

    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean>
        get() = _showLoading

    fun updateNote(noteTitle: String, noteText: String){
        viewModelScope.launch {
            val note = Note(noteId = noteId, title = noteTitle, note = noteText)
            dataSource.update(note)
        }
//        _showLoading.value = true
//        result.await()
//        _showLoading.value = false
    }

    fun makeACopy(){
        viewModelScope.launch {
            val result = async {
                val note = dataSource.get(noteId)
                note.noteId = 0L
                dataSource.insert(note)
//                withContext(Dispatchers.IO){
//                    Thread.sleep(5000)
//                }
            }
            _showLoading.value = true
            result.await()
            _showLoading.value = false
        }
    }


}