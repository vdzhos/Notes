package com.example.notes

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.notes.database.Note
import com.example.notes.database.NotesDatabase
import com.example.notes.database.NotesDatabaseDAO
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class NoteDatabaseTest {

    private lateinit var notesDAO: NotesDatabaseDAO
    private lateinit var db: NotesDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, NotesDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        notesDAO = db.notesDatabaseDAO
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetNote() {
        val note = Note(title = "123")
        notesDAO.insert(note)
        val lastNote = notesDAO.getLastNote()
//        val notes = notesDAO.getAllNotesAsc().value
        Assert.assertEquals(lastNote?.title, "123")
    }
}