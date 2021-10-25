package com.example.notes.notedetails.dialogs.datetimepicker

import androidx.lifecycle.ViewModel
import java.util.*
import kotlin.math.abs

class DateTimePickerViewModel: ViewModel() {

    val cal = Calendar.getInstance()

    var datePickerLocker = false
    var timePickerLocker = false
    var getReminderDateLocker = true
    var getReminderTimeLocker = true

    fun setDefaultDate(){
        val calendar = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
        cal.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
    }

    fun getDateInText(): String{
        val y = Calendar.getInstance().get(Calendar.YEAR)
        val year = if (cal.get(Calendar.YEAR)>y) ", ${cal.get(Calendar.YEAR)}" else ""
        return "${cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())} ${cal.get(Calendar.DAY_OF_MONTH)}$year"
    }

    fun setTime(hour: Int, minute: Int){
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
    }

    fun getTimeInText(format24: Boolean): String{
        val h = cal.get(Calendar.HOUR_OF_DAY)
        val m = cal.get(Calendar.MINUTE)
        val hour = if(format24) h
        else{ if(h>12 || h==0) abs(h - 12) else h }
        val hourString = if(format24 && hour<10) "0$hour" else hour
        val minute = if(m>=10) m else "0$m"
        val ampm = if(!format24) { if(h*60<720) "AM" else "PM"} else ""
        return "$hourString:$minute $ampm"
    }

}