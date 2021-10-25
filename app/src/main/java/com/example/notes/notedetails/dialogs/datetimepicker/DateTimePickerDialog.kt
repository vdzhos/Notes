package com.example.notes.notedetails.dialogs.datetimepicker

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.notes.R
import com.example.notes.Reminder
import com.example.notes.databinding.DialogDateTimePickerBinding
import java.text.SimpleDateFormat
import java.util.*


class DateTimePickerDialog: DialogFragment() {

    private lateinit var binding: DialogDateTimePickerBinding
    private lateinit var timePickerDialog: TimePickerDialog
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var viewModel: DateTimePickerViewModel
    private var format24 = true

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_date_time_picker, container, false)

        format24 = DateFormat.is24HourFormat(requireContext())

        val noteId = DateTimePickerDialogArgs.fromBundle(requireArguments()).noteId

        viewModel = ViewModelProvider(this).get(DateTimePickerViewModel::class.java)

        setDateSpinnerEntries()

        createDatePicker()
        createTimePicker()
        setupSpinners()

        binding.save.setOnClickListener {
            val selectedDate = binding.dateSpinner.selectedItemPosition
            val selectedTime = binding.timeSpinner.selectedItemPosition

            val now = Calendar.getInstance()
            now.set(Calendar.MILLISECOND,0)
            now.set(Calendar.SECOND,0)

            if(selectedDate!=0 && selectedTime!=0 && viewModel.cal.time>now.time){
                val action = DateTimePickerDialogDirections.actionDateTimePickerDialogToNoteDetailsFragment()
                action.noteId = noteId
                action.reminder = Reminder(viewModel.cal.time, Repeat.values()[binding.repeatSpinner.selectedItemPosition])
                findNavController().navigate(action)
            }
            if(selectedDate==0){
                binding.dateSpinner.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red))
            }else{
                binding.dateSpinner.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.super_light_gray))
            }
            if(selectedTime==0 || viewModel.cal.time<=now.time){
                binding.timeSpinner.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red))
            }else{
                binding.timeSpinner.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.super_light_gray))
            }
        }

        binding.cancel.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.delete.setOnClickListener {
            val action = DateTimePickerDialogDirections.actionDateTimePickerDialogToNoteDetailsFragment()
            action.noteId = noteId
            action.reminder = Reminder(Date(), Repeat.DOES_NOT_REPEAT,true)
            findNavController().navigate(action)
        }

        setExistingReminder()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val prevValue = format24
        format24 = DateFormat.is24HourFormat(requireContext())
        if(format24 != prevValue) createTimePicker()
    }

    private fun setExistingReminder() {
        val reminder = DateTimePickerDialogArgs.fromBundle(requireArguments()).reminder
        if(reminder!=null) binding.delete.visibility = View.VISIBLE
        if(viewModel.getReminderDateLocker && viewModel.getReminderTimeLocker){
            if(reminder!=null){
                viewModel.cal.time = reminder.date
                binding.dateSpinner.setSelection(4)
                binding.timeSpinner.setSelection(5)
                binding.repeatSpinner.setSelection(reminder.repeat.ordinal)
            }else{
                viewModel.getReminderDateLocker = false
                viewModel.getReminderTimeLocker = false
            }
        }
    }

    private fun setDateSpinnerEntries() {
        val arr = resources.getStringArray(R.array.reminderDates)
        val next = String.format(arr[3], SimpleDateFormat("EEEE", Locale.ENGLISH).format(Date()))
        arr[3] = next
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, arr)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.dateSpinner.adapter = adapter
    }
    
    private fun setupSpinners(){
        setupDatePicker()
        setupTimePicker()
        setupRepeatPicker()
    }

    private fun setupDatePicker(){
        binding.dateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                binding.dateSpinner.selectedView?.let {
                    val oldCal = Calendar.getInstance()
                    oldCal.time = viewModel.cal.time
                    when (position) {
                        1 -> {
                            viewModel.setDefaultDate()
                        }
                        2 -> {
                            viewModel.setDefaultDate()
                            viewModel.cal.add(Calendar.DAY_OF_YEAR, 1)
                        }
                        3 -> {
                            viewModel.setDefaultDate()
                            viewModel.cal.add(Calendar.DAY_OF_YEAR, 7)
                        }
                        4 -> {
                            if (!viewModel.datePickerLocker && !viewModel.getReminderDateLocker) {
                                datePickerDialog.updateDate(oldCal.get(Calendar.YEAR),
                                        oldCal.get(Calendar.MONTH), oldCal.get(Calendar.DAY_OF_MONTH))
                                datePickerDialog.show()
                            } else {
                                (it as TextView).text = viewModel.getDateInText()
                                binding.dateSpinner.lastPosition = 4
                            }
                        }
                    }
                    if (position != 4 && position !=0) {
                        (it as TextView).text = viewModel.getDateInText()
                        binding.dateSpinner.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.super_light_gray))
                    }
                    viewModel.datePickerLocker = false
                    viewModel.getReminderDateLocker = false
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    private fun createDatePicker() {
        val listener = DatePickerDialog.OnDateSetListener { _, y, m, d ->
            viewModel.cal.set(Calendar.DAY_OF_MONTH, d)
            viewModel.cal.set(Calendar.MONTH, m)
            viewModel.cal.set(Calendar.YEAR, y)
            (binding.dateSpinner.selectedView as TextView).text = viewModel.getDateInText()
            binding.dateSpinner.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.super_light_gray))
        }
        datePickerDialog = DatePickerDialog(requireContext(), R.style.DialogTheme,
                listener, viewModel.cal.get(Calendar.YEAR), viewModel.cal.get(Calendar.MONTH),
                viewModel.cal.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.setOnCancelListener {
            (binding.dateSpinner.selectedView as TextView).text = viewModel.getDateInText()
        }
        datePickerDialog.datePicker.minDate = Date().time
    }

    private fun setupTimePicker() {
        binding.timeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                binding.timeSpinner.selectedView?.let {
                    val oldCal = Calendar.getInstance()
                    oldCal.time = viewModel.cal.time
                    when (position) {
                        1 -> {
                            setTimeFormatted(R.string.tomorrowMorningTime, R.string.tomorrowMorningTime24)
                            viewModel.setTime(8,0)
                        }
                        2 -> {
                            setTimeFormatted(R.string.afternoonTime, R.string.afternoonTime24)
                            viewModel.setTime(13,0)
                        }
                        3 -> {
                            setTimeFormatted(R.string.tomorrowEveningTime, R.string.tomorrowEveningTime24)
                            viewModel.setTime( 18,0)
                        }
                        4 -> {
                            setTimeFormatted(R.string.nightTime, R.string.nightTime24)
                            viewModel.setTime(20,0)
                        }
                        5 -> {
                            if (!viewModel.timePickerLocker && !viewModel.getReminderTimeLocker) {
                                timePickerDialog.updateTime(oldCal.get(Calendar.HOUR_OF_DAY),
                                        oldCal.get(Calendar.MINUTE))
                                timePickerDialog.show()
                            } else {
                                (it as TextView).text = viewModel.getTimeInText(format24)
                                binding.timeSpinner.lastPosition = 5
                            }
                        }
                    }
                    if(position!=0 && position!=5){
                        binding.timeSpinner.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.super_light_gray))
                    }
                    viewModel.timePickerLocker = false
                    viewModel.getReminderTimeLocker = false
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    private fun createTimePicker() {
        val listener = TimePickerDialog.OnTimeSetListener{ _, h, m ->
            viewModel.setTime(h,m)
            (binding.timeSpinner.selectedView as TextView).text = viewModel.getTimeInText(format24)
            binding.timeSpinner.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.super_light_gray))
        }
        timePickerDialog = TimePickerDialog(requireContext(), R.style.DialogTheme,
                listener, viewModel.cal.get(Calendar.HOUR_OF_DAY), viewModel.cal.get(Calendar.MINUTE), format24)
        timePickerDialog.setOnCancelListener {
            (binding.timeSpinner.selectedView as TextView).text = viewModel.getTimeInText(format24)
        }
    }

    private fun setTimeFormatted(id: Int, id24: Int){
        if(format24) (binding.timeSpinner.selectedView as TextView).text = getText(id24)
        else (binding.timeSpinner.selectedView as TextView).text = getText(id)
    }

    private fun setupRepeatPicker() {
        binding.repeatSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                binding.repeatSpinner.selectedView?.let {
                    when(position){
                        1 -> (it as TextView).text = getString(R.string.reminder_spinner_repeat_placeholders, "daily")
                        2 -> (it as TextView).text = getString(R.string.reminder_spinner_repeat_placeholders, "weekly")
                        3 -> (it as TextView).text = getString(R.string.reminder_spinner_repeat_placeholders, "monthly")
                        4 -> (it as TextView).text = getString(R.string.reminder_spinner_repeat_placeholders, "yearly")
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.datePickerLocker = binding.dateSpinner.selectedItemPosition == 4
        viewModel.timePickerLocker = binding.timeSpinner.selectedItemPosition == 5
    }

}

enum class Repeat{
    DOES_NOT_REPEAT, DAILY, WEEKLY, MONTHLY, YEARLY
}