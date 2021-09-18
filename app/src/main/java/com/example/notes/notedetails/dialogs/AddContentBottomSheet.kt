package com.example.notes.notedetails.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.notes.R
import com.example.notes.databinding.BottomSheetNoteDetailsAddContentBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddContentBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<BottomSheetNoteDetailsAddContentBinding>(inflater, R.layout.bottom_sheet_note_details_add_content, container, false)

        val noteId = AddContentBottomSheetArgs.fromBundle(requireArguments()).noteId

        return binding.root
    }

}