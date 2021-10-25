package com.example.notes

import android.os.Parcelable
import androidx.room.TypeConverter
import com.example.notes.notedetails.dialogs.datetimepicker.Repeat
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: String?): Reminder? {
        return value?.let {
            val repeat = Repeat.values()[Integer.parseInt(it[0].toString())]
            val date = Date(it.substring(1).toLong())
            Reminder(date = date, repeat)
        }
    }

    @TypeConverter
    fun dateToTimestamp(reminder: Reminder?): String? {
        return reminder?.let {
            it.repeat.ordinal.toString() + it.date.time.toString()
        }
    }

    @TypeConverter
    fun listOfLabelsToString(labels: List<String>?) : String?{
        return labels?.joinToString("\n")
    }

    @TypeConverter
    fun stringToListOfLabels(stringOfLabels: String?) : List<String>?{
        return stringOfLabels?.split("\n")?.toList()
    }

}

@Parcelize
data class Reminder(
        var date: Date,
        var repeat: Repeat,
        var delete: Boolean = false
) : Parcelable

fun formatDateAndTimeForReminderTag(reminder: Reminder, hourFormat24: Boolean): String{
    val cal = Calendar.getInstance()
    cal.time = reminder.date

    val today = Calendar.getInstance()

    val tomorrow = Calendar.getInstance()
    tomorrow.add(Calendar.DAY_OF_MONTH, 1)

    val day = if (cal.get(Calendar.DAY_OF_MONTH)==today.get(Calendar.DAY_OF_MONTH) &&
                    cal.get(Calendar.MONTH)==today.get(Calendar.MONTH) &&
                    cal.get(Calendar.YEAR)==today.get(Calendar.YEAR))
                        "Today"
              else if(cal.get(Calendar.DAY_OF_MONTH)==tomorrow.get(Calendar.DAY_OF_MONTH) &&
                        cal.get(Calendar.MONTH)==tomorrow.get(Calendar.MONTH) &&
                        cal.get(Calendar.YEAR)==tomorrow.get(Calendar.YEAR))
                        "Tomorrow"
              else "${SimpleDateFormat("MMM",Locale.ENGLISH).format(cal.time)} ${cal.get(Calendar.DAY_OF_MONTH)}${if (cal.get(Calendar.YEAR)>today.get(Calendar.YEAR)) ", ${cal.get(Calendar.YEAR)}" else ""}"

    var min = cal.get(Calendar.MINUTE).toString()
    val hour = cal.get(Calendar.HOUR_OF_DAY)
    if(min.length<2) min = "0$min"
    val time = if(hourFormat24){
        "$hour:$min"
    }else{
        "${cal.get(Calendar.HOUR)}:$min ${if(hour>=12) "PM" else "AM"}"
    }

    return "$day, $time"
}