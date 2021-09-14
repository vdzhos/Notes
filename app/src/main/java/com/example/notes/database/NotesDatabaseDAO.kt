package com.example.notes.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NotesDatabaseDAO {

    @Insert
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun deleteNotes(notes: List<Note>)

    @Query("DELETE FROM notes_table WHERE noteId = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM notes_table WHERE noteId = :id")
    suspend fun get(id: Long) : Note

    @Query("SELECT * FROM notes_table WHERE noteId = :id")
    fun getLiveData(id: Long) : LiveData<Note>

    @Query("SELECT * FROM notes_table ORDER BY noteId DESC LIMIT 1")
    suspend fun getLastNote(): Note?

//    @Query("SELECT * FROM notes_table WHERE reminder IS NOT NULL")
//    fun getAllNotesWithReminder(): LiveData<List<Note>>
//
//    @Query("SELECT * FROM notes_table WHERE labels IS NOT NULL")
//    fun getAllNotesWithLabels(): LiveData<List<Note>>

    @Query("SELECT * FROM notes_table ORDER BY noteId DESC")
    fun getAllNotesDesc(): LiveData<List<Note>>

    @Query("SELECT * FROM notes_table ORDER BY noteId ASC")
    fun getAllNotesAsc(): LiveData<List<Note>>

}