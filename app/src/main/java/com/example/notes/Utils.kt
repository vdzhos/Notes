package com.example.notes

import androidx.room.TypeConverter
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
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