package com.example.notes

import android.text.format.DateFormat
import android.view.View
import androidx.databinding.BindingAdapter
import com.example.notes.myviews.Tag
import com.google.android.flexbox.FlexboxLayout
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter(value = ["reminder","labels","size"], requireAll = false)
fun FlexboxLayout.setTags(reminder: Reminder?, labels: List<String>?, size: Boolean){
    if(reminder!=null || labels!=null){
        visibility = View.VISIBLE
    }else {
        visibility = View.GONE
        return
    }
    removeAllViews()
    reminder?.let {
        val tag = Tag(context,size)
        val cal = Calendar.getInstance()
        val today = Calendar.getInstance()
        val tomorrow = Calendar.getInstance()
        tomorrow.add(Calendar.DAY_OF_MONTH, 1)
        cal.time = it.date
        val day = when(cal.get(Calendar.DATE)){
            today.get(Calendar.DATE) -> "Today"
            tomorrow.get(Calendar.DATE) -> "Tomorrow"
            else -> "${SimpleDateFormat("MMM",Locale.ENGLISH).format(cal.time)} ${cal.get(Calendar.DAY_OF_MONTH)}"
        }

        var min = cal.get(Calendar.MINUTE).toString()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        if(min.length<2) min = "0$min"
        val time = if(DateFormat.is24HourFormat(context)){
            "$hour:$min"
        }else{
            "${cal.get(Calendar.HOUR)}:$min ${if(hour>=12) "PM" else "AM"}"
        }

        val dayTime = "$day, $time"
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
