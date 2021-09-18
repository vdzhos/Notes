package com.example.notes.notedetails.dialogs

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.notes.R
import com.example.notes.databinding.BottomSheetReminderMainBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*

class ReminderMainBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetReminderMainBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<BottomSheetReminderMainBinding>(inflater, R.layout.bottom_sheet_reminder_main, container, false)

        val noteId = ReminderMainBottomSheetArgs.fromBundle(requireArguments()).noteId

        setDates()

        return binding.root
    }

    private fun setDates(){

        val dayOfWeek = when(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)){
            1 -> "Sunday"
            2 -> "Monday"
            3 -> "Tuesday"
            4 -> "Wednesday"
            5 -> "Thursday"
            6 -> "Friday"
            else -> "Saturday"
        }

        binding.nextWeekMorningFull.text = getString(R.string.nextWeekMorning, dayOfWeek)

        if(DateFormat.is24HourFormat(context)){
            binding.tomorrowMorningTime.text = getString(R.string.tomorrowMorningTime24)
            binding.tomorrowEveningTime.text = getString(R.string.tomorrowEveningTime24)
            binding.nextWeekMorningTime.text = getString(R.string.nextWeekMorningTime24, dayOfWeek.substring(0,3))
        }else{
            binding.tomorrowMorningTime.text = getString(R.string.tomorrowMorningTime)
            binding.tomorrowEveningTime.text = getString(R.string.tomorrowEveningTime)
            binding.nextWeekMorningTime.text = getString(R.string.nextWeekMorningTime, dayOfWeek.substring(0,3))
        }

    }

}