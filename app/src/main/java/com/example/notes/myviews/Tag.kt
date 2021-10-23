package com.example.notes.myviews

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.notes.R

class Tag(context: Context, size: Boolean): ConstraintLayout(context) {

    constructor(context: Context): this(context,true)

    private var imageView: ImageView
    private var textView: TextView

    init {
        inflate(getContext(), R.layout.tag_item,this)
        imageView = findViewById(R.id.alarm)
        textView = findViewById(R.id.text)
        if(!size){
            imageView.setBackgroundResource(R.drawable.ic_baseline_access_alarm_14)
            textView.textSize = 11f
        }else{
            imageView.setBackgroundResource(R.drawable.ic_baseline_access_alarm_17)
            textView.textSize = 14f
        }
    }

    fun setImageVisibility(visible: Boolean) {
        if(visible) imageView.visibility = VISIBLE
        else imageView.visibility = GONE
    }

    fun setText(text: String) {
        textView.text = text
    }

}