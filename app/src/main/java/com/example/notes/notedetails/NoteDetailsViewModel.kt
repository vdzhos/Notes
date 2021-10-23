package com.example.notes.notedetails

import androidx.lifecycle.*
import com.example.notes.Reminder
import com.example.notes.database.Note
import com.example.notes.database.NotesDatabaseDAO
import kotlinx.coroutines.*
import java.util.*

class NoteDetailsViewModel(
    val noteId: Long,
    private val dataSource: NotesDatabaseDAO) : ViewModel() {

    val note: LiveData<Note> = dataSource.getLiveData(noteId)

    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean>
        get() = _showLoading

    fun updateNote(noteTitle: String, noteText: String){
        viewModelScope.launch {
            val note = note.value
            note!!.title = noteTitle
            note.note = noteText
            dataSource.update(note)
        }
    }

    fun updateNoteDate(reminder: Reminder?){
        reminder?.let {
            viewModelScope.launch {
                val result = async {
                    val note = getNoteById(noteId)
                    note.reminder = reminder
                    dataSource.update(note)
//                    withContext(Dispatchers.IO){
//                        Thread.sleep(5000)
//                    }
                }
                _showLoading.value = true
                result.await()
                _showLoading.value = false
            }
        }
    }

    fun makeACopy(){
        viewModelScope.launch {
            val result = async {
                val note = getNoteById(noteId)
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

    fun sendNote(startActivity: (note:Note) -> Unit){
        viewModelScope.launch {
            var note: Note? = null
            val result = async {
                note = getNoteById(noteId)
            }
            _showLoading.value = true
            result.await()
            _showLoading.value = false
            startActivity(note!!)
        }
    }

    private suspend fun getNoteById(noteId: Long) : Note {
        return dataSource.get(noteId)
    }


}