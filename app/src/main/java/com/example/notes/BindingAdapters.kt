package com.example.notes

import android.text.format.DateFormat
import android.view.View
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import com.example.notes.myviews.Tag
import com.example.notes.notedetails.NoteDetailsFragmentDirections
import com.google.android.flexbox.FlexboxLayout
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter(value = ["reminder","labels","size","noteId"], requireAll = false)
fun FlexboxLayout.setTags(reminder: Reminder?, labels: List<String>?, size: Boolean, noteId: Long){
    if(reminder!=null || labels!=null){
        visibility = View.VISIBLE
    }else {
        visibility = View.GONE
        return
    }
    removeAllViews()
    reminder?.let {
        val tag = Tag(context,size)

        if(noteId!=-1L){
            tag.setOnClickListener {
                val action = NoteDetailsFragmentDirections.actionNoteDetailsFragmentToDateTimePickerDialog()
                action.noteId = noteId
                action.reminder = reminder
                findNavController().navigate(action)
            }
        }

        val dayTime = formatDateAndTimeForReminderTag(it,DateFormat.is24HourFormat(context))
        tag.setText(dayTime)
        addView(tag,0)
    }

    labels?.let {
        it.forEach { label ->
            val tag = Tag(context,size)
            tag.setText(label)
            tag.setImageVisibility(false)
            addView(tag)
        }
    }
}
