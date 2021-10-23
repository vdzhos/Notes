package com.example.notes

import android.os.Parcelable
import androidx.room.TypeConverter
import com.example.notes.notedetails.dialogs.Repeat
import kotlinx.parcelize.Parcelize
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
        var repeat: Repeat
) : Parcelable