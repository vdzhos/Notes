package com.example.notes.notesmain

import androidx.lifecycle.*
import com.example.notes.database.Note
import com.example.notes.database.NotesDatabaseDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotesMainViewModel(
    private val listType: ListType,
    private val dataSource: NotesDatabaseDAO) : ViewModel() {

    val notes: LiveData<List<Note>> = when(listType){
        ListType.ALL -> dataSource.getAllNotesDesc()
        ListType.REMINDERS -> dataSource.getAllNotesWithReminder()
        else -> dataSource.getAllNotesWithLabels()
    }

    val notesWithLabels: LiveData<List<Note>> = MediatorLiveData<List<Note>>().apply {

        fun update(){
            val list = notes.value?.filter { note ->
                note.labels!!.contains(listType.info)
            }
            value = list
        }

        addSource(notes) { if(listType==ListType.LABEL) update() }
    }

    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean>
        get() = _showLoading

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
        _showLoading.value = true
        val noteId = result.await()
        _showLoading.value = false
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
        //TODO: change the N search to logN search (binary)
        if(notes.value==null) return null
        for (note in notes.value!!){
            if(note.noteId==noteId) return note
        }
        return null
    }

    fun deleteNote(noteId: Long){
        viewModelScope.launch {
            val result = async {
                dataSource.deleteById(noteId)
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