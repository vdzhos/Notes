package com.example.notes.notesmain

import androidx.lifecycle.*
import com.example.notes.database.Note
import com.example.notes.database.NotesDatabaseDAO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class NotesMainViewModel(
    private val dataSource: NotesDatabaseDAO) : ViewModel() {

    val notes = dataSource.getAllNotesDesc()

    private val _createStatus = MutableLiveData<Boolean>()
    val createStatus: LiveData<Boolean>
        get() = _createStatus

    private val _actionModeItemsSelected = MutableLiveData<Long>(0)
    val actionModeItemsSelected: LiveData<Long>
        get() = _actionModeItemsSelected

    private val selectedItems : MutableList<Note> = mutableListOf()

    suspend fun createEmptyNote(): Long {
        val result = viewModelScope.async {
            var note = Note()
            dataSource.insert(note)
            note = dataSource.getLastNote()!!
//            withContext(Dispatchers.IO){
//                Thread.sleep(5000)
//            }
            note.noteId
        }
        _createStatus.value = true
        val noteId = result.await()
        _createStatus.value = false
        return noteId
    }

    fun deleteNotes(){
        viewModelScope.launch {
            val notes = selectedItems.toList()
            clearSelectedNotes()
            dataSource.deleteNotes(notes)
        }
    }

    fun addToSelectedItemsList(note: Note){
        selectedItems.add(note)
        _actionModeItemsSelected.value = _actionModeItemsSelected.value?.plus(1)
    }

    fun removeFromSelectedItemsList(note: Note){
        selectedItems.remove(note)
        _actionModeItemsSelected.value = _actionModeItemsSelected.value?.minus(1)
    }

    fun getSelectedNotes(): List<Note>{
        return selectedItems
    }

    fun clearSelectedNotes(){
        selectedItems.clear()
        _actionModeItemsSelected.value = 0
    }

    fun getNoteById(noteId: Long): Note? {
//        val index = notes.value?.binarySearchDA(key = noteId, comparator = false) {it.noteId} ?: return null
//        return notes.value?.get(index)
        //TODO: change the N search to logN search (binary)
        if(notes.value==null) return null
        for (note in notes.value!!){
            if(note.noteId==noteId) return note
        }
        return null
    }

}