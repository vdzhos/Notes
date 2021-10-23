package com.example.notes.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.notes.Reminder

@Entity(tableName = "notes_table")
data class Note(

    @Ignore
    var isSelected: Boolean = false,

    @PrimaryKey(autoGenerate = true)
    var noteId: Long = 0L,

    @ColumnInfo(name = "title")
    var title: String = "",

    @ColumnInfo(name = "note")
    var note: String = "",

    @ColumnInfo(name = "labels")
    var labels: List<String>? = null,

    @ColumnInfo(name = "reminder")
    var reminder: Reminder? = null
)