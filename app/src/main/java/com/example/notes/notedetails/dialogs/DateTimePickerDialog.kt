package com.example.notes.notedetails.dialogs

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
import androidx.navigation.fragment.findNavController
import com.example.notes.R
import com.example.notes.Reminder
import com.example.notes.databinding.DialogDateTimePickerBinding
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.util.*
import kotlin.math.abs


class DateTimePickerDialog: DialogFragment() {

    private lateinit var binding: DialogDateTimePickerBinding
    private lateinit var timePickerDialog: TimePickerDialog
    private lateinit var datePickerDialog: DatePickerDialog
    private val cal = Calendar.getInstance()
    private var format24 = true
    private var datePickerLocker = false
    private var timePickerLocker = false

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_date_time_picker, container, false)

        format24 = DateFormat.is24HourFormat(requireContext())

        val noteId = DateTimePickerDialogArgs.fromBundle(requireArguments()).noteId

        setDateSpinnerEntries()

        savedInstanceState?.let { bundle ->
            val date = bundle.getSerializable("Date")
            date?.let {
                cal.time = it as Date
            }
            datePickerLocker = bundle.getBoolean("dateLocker")
            timePickerLocker = bundle.getBoolean("timeLocker")
        }

        createDatePicker()
        createTimePicker()
        setupSpinners()

        binding.save.setOnClickListener {
            val selectedDate = binding.dateSpinner.selectedItemPosition
            val selectedTime = binding.timeSpinner.selectedItemPosition
            if(selectedDate!=0 && selectedTime!=0){
                val action = DateTimePickerDialogDirections.actionDateTimePickerDialogToNoteDetailsFragment()
                action.noteId = noteId
                action.reminder = Reminder(cal.time, Repeat.values()[binding.repeatSpinner.selectedItemPosition])
                findNavController().navigate(action)
            }
            if(selectedDate==0){
                binding.dateSpinner.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red))
            }else{
                binding.dateSpinner.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.super_light_gray))
            }
            if(selectedTime==0){
                binding.timeSpinner.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red))
            }else{
                binding.timeSpinner.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.super_light_gray))
            }
        }

        binding.cancel.setOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

    private fun setDateSpinnerEntries() {
        val arr = resources.getStringArray(R.array.reminderDates)
        val next = String.format(arr[3], SimpleDateFormat("EEEE", Locale.ENGLISH).format(Date()))
        arr[3] = next
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, arr)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.dateSpinner.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        val prevValue = format24
        format24 = DateFormat.is24HourFormat(requireContext())
        if(format24 != prevValue) createTimePicker()
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
                    val calendar = Calendar.getInstance()
                    val oldCal = Calendar.getInstance()
                    oldCal.time = cal.time
                    when (position) {
                        2 -> {
                            setDefaultDate(calendar)
                            cal.add(Calendar.DAY_OF_YEAR, 1)
                        }
                        3 -> {
                            setDefaultDate(calendar)
                            cal.add(Calendar.DAY_OF_YEAR, 7)
                        }
                        4 -> {
                            if (!datePickerLocker) {
                                datePickerDialog.updateDate(oldCal.get(Calendar.YEAR),
                                        oldCal.get(Calendar.MONTH), oldCal.get(Calendar.DAY_OF_MONTH))
                                datePickerDialog.show()
                            } else {
                                (it as TextView).text = getDateInText(calendar.get(Calendar.YEAR))
                                binding.dateSpinner.lastPosition = 4
                            }
                        }
                    }
                    if (position != 4 && position !=0) {
                        (it as TextView).text = getString(R.string.reminder_spinner_date_placeholders,
                                cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()), cal.get(Calendar.DAY_OF_MONTH), "")
                        binding.dateSpinner.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.super_light_gray))
                    }
                    datePickerLocker = false
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    private fun setDefaultDate(calendar: Calendar){
        cal.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
        cal.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
    }

    private fun createDatePicker() {
        val listener = DatePickerDialog.OnDateSetListener { _, y, m, d ->
            val prevYear = cal.get(Calendar.YEAR)
            cal.set(Calendar.DAY_OF_MONTH, d)
            cal.set(Calendar.MONTH, m)
            cal.set(Calendar.YEAR, y)
            (binding.dateSpinner.selectedView as TextView).text = getDateInText(prevYear)
            binding.dateSpinner.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.super_light_gray))
        }
        datePickerDialog = DatePickerDialog(requireContext(), R.style.DialogTheme,
                listener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.setOnCancelListener {
            binding.dateSpinner.setSelection(0)
        }
        datePickerDialog.datePicker.minDate = Date().time
    }

    private fun getDateInText(y: Int): String{
        val year = if (cal.get(Calendar.YEAR)>y) ", ${cal.get(Calendar.YEAR)}" else ""
        return getString(R.string.reminder_spinner_date_placeholders,
                cal.getDisplayName(Calendar.MONTH, Calendar.LONG,
                        Locale.getDefault()), cal.get(Calendar.DAY_OF_MONTH), year)
    }

    private fun setupTimePicker() {
        binding.timeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                binding.timeSpinner.selectedView?.let {
                    val oldCal = Calendar.getInstance()
                    oldCal.time = cal.time
                    when (position) {
                        1 -> setTime(R.string.tomorrowMorningTime, R.string.tomorrowMorningTime24, 8)
                        2 -> setTime(R.string.afternoonTime, R.string.afternoonTime24, 13)
                        3 -> setTime(R.string.tomorrowEveningTime, R.string.tomorrowEveningTime24, 18)
                        4 -> setTime(R.string.nightTime, R.string.nightTime24, 20)
                        5 -> {
                            if (!timePickerLocker) {
                                timePickerDialog.updateTime(oldCal.get(Calendar.HOUR_OF_DAY),
                                        oldCal.get(Calendar.MINUTE))
                                timePickerDialog.show()
                            } else {
                                (it as TextView).text = getTimeInText(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
                                binding.timeSpinner.lastPosition = 5
                            }
                        }
                    }
                    if(position!=0 && position!=5){
                        binding.timeSpinner.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.super_light_gray))
                    }
                    timePickerLocker = false
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    private fun createTimePicker() {
        val listener = TimePickerDialog.OnTimeSetListener{ _, h, m ->
            cal.set(Calendar.HOUR_OF_DAY, h)
            cal.set(Calendar.MINUTE, m)
            (binding.timeSpinner.selectedView as TextView).text = getTimeInText(h, m)
            binding.timeSpinner.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.super_light_gray))
        }
        timePickerDialog = TimePickerDialog(requireContext(), R.style.DialogTheme,
                listener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), format24)
        timePickerDialog.setOnCancelListener {
            binding.timeSpinner.setSelection(0)
        }
    }

    private fun setTime(id: Int, id24: Int, hour: Int){
        if(format24) (binding.timeSpinner.selectedView as TextView).text = getText(id24)
        else          (binding.timeSpinner.selectedView as TextView).text = getText(id)
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
    }

    private fun getTimeInText(h: Int, m: Int): String{
        val hour = if(format24) h
        else{ if(h>12 || h==0) abs(h - 12) else h }
        val hourString = if(format24 && hour<10) "0$hour" else hour
        val minute = if(m>=10) m else "0$m"
        val ampm = if(!format24) { if(h*60<720) "AM" else "PM"} else ""
        return getString(R.string.reminder_spinner_time_placeholders, hourString, minute, ampm)
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("Date", cal.time)
        outState.putBoolean("dateLocker", binding.dateSpinner.selectedItemPosition == 4)
        outState.putBoolean("timeLocker", binding.timeSpinner.selectedItemPosition == 5)
    }

}

enum class Repeat{
    DOES_NOT_REPEAT, DAILY, WEEKLY, MONTHLY, YEARLY
}