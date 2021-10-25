package com.example.notes.notedetails.dialogs

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.notes.R
import com.example.notes.Reminder
import com.example.notes.databinding.BottomSheetReminderMainBinding
import com.example.notes.notedetails.dialogs.datetimepicker.Repeat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.util.*

class ReminderMainBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetReminderMainBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<BottomSheetReminderMainBinding>(inflater, R.layout.bottom_sheet_reminder_main, container, false)

        val noteId = ReminderMainBottomSheetArgs.fromBundle(requireArguments()).noteId

        binding.tomorrowMorning.setOnClickListener {
            val action = ReminderMainBottomSheetDirections.actionReminderMainBottomSheetToNoteDetailsFragment()
            action.noteId = noteId
            action.reminder = Reminder(getDate(1,8,0,0), Repeat.DOES_NOT_REPEAT)
            findNavController().navigate(action)
        }

        binding.tomorrowEvening.setOnClickListener {
            val action = ReminderMainBottomSheetDirections.actionReminderMainBottomSheetToNoteDetailsFragment()
            action.noteId = noteId
            action.reminder = Reminder(getDate(1,18,0,0), Repeat.DOES_NOT_REPEAT)
            findNavController().navigate(action)
        }

        binding.nextWeekMorning.setOnClickListener {
            val action = ReminderMainBottomSheetDirections.actionReminderMainBottomSheetToNoteDetailsFragment()
            action.noteId = noteId
            action.reminder = Reminder(getDate(7,8,0,0), Repeat.DOES_NOT_REPEAT)
            findNavController().navigate(action)
        }

        binding.pickTime.setOnClickListener {
            val action = ReminderMainBottomSheetDirections.actionReminderMainBottomSheetToDateTimePickerDialog()
            action.noteId = noteId
            action.reminder = ReminderMainBottomSheetArgs.fromBundle(requireArguments()).reminder
            findNavController().navigate(action)
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val behavior = BottomSheetBehavior.from(requireView().parent as View)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        setDates()
    }

    private fun getDate(d:Int, h: Int, m: Int, s: Int): Date{
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR,d)
        cal.set(Calendar.HOUR_OF_DAY, h)
        cal.set(Calendar.MINUTE, m)
        cal.set(Calendar.SECOND, s)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
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
            binding.nextWeekMorningTime.text = getString(R.string.nextWeekMorningTime24, dayOfWeek.substring(0, 3))
        }else{
            binding.tomorrowMorningTime.text = getString(R.string.tomorrowMorningTime)
            binding.tomorrowEveningTime.text = getString(R.string.tomorrowEveningTime)
            binding.nextWeekMorningTime.text = getString(R.string.nextWeekMorningTime, dayOfWeek.substring(0, 3))
        }

    }

}