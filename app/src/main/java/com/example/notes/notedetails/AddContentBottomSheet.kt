package com.example.notes.notedetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.notes.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddContentBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_note_details_add_content, container, false)
    }

    companion object {
        const val TAG = "AddContentBottomSheet"
    }

}