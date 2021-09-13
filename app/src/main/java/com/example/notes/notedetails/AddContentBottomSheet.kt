package com.example.notes.notedetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.notes.R
import com.example.notes.database.NotesDatabase
import com.example.notes.databinding.BottomSheetNoteDetailsAddContentBinding
import com.example.notes.databinding.BottomSheetNoteDetailsMoreOptionsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddContentBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<BottomSheetNoteDetailsAddContentBinding>(inflater, R.layout.bottom_sheet_note_details_add_content, container, false)

        val dataSource = NotesDatabase.getInstance(requireContext()).notesDatabaseDAO

        val viewModelFactory = NoteDetailsViewModelFactory(AddContentBottomSheetArgs.fromBundle(requireArguments()).noteId, dataSource)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(NoteDetailsViewModel::class.java)


        return binding.root
    }

    companion object {
        const val TAG = "AddContentBottomSheet"
    }

}