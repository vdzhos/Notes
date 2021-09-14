package com.example.notes.notedetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.notes.R
import com.example.notes.databinding.BottomSheetNoteDetailsMoreOptionsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MoreOptionsBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<BottomSheetNoteDetailsMoreOptionsBinding>(inflater, R.layout.bottom_sheet_note_details_more_options, container, false)

        val noteId = MoreOptionsBottomSheetArgs.fromBundle(requireArguments()).noteId;

        binding.delete.setOnClickListener {
            val action = MoreOptionsBottomSheetDirections.actionMoreOptionsBottomSheetToNotesMainFragment()
            action.operation = Operation.DELETE
            action.noteId = noteId
            findNavController().navigate(action)
        }

        binding.makeACopy.setOnClickListener {
            val action = MoreOptionsBottomSheetDirections.actionMoreOptionsBottomSheetToNoteDetailsFragment()
            action.operation = Operation.MAKE_A_COPY
            action.noteId = noteId
            findNavController().navigate(action)
        }

        binding.send.setOnClickListener {
            val action = MoreOptionsBottomSheetDirections.actionMoreOptionsBottomSheetToNoteDetailsFragment()
            action.operation = Operation.SEND
            action.noteId = noteId
            findNavController().navigate(action)
        }

        return binding.root
    }

    companion object {
        const val TAG = "MoreOptionsBottomSheet"
    }

}