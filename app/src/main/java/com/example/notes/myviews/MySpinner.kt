package com.example.notes.myviews

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatSpinner

class MySpinner(context: Context, attrs: AttributeSet): AppCompatSpinner(context, attrs) {

    var lastPosition = 0

    override fun setSelection(position: Int) {
        super.setSelection(position)

        val onItemSelectedListener = onItemSelectedListener
        if (lastPosition == selectedItemPosition && onItemSelectedListener != null) {
            onItemSelectedListener.onItemSelected(this, selectedView, position, selectedItemId)
        }
        lastPosition = position
    }

}