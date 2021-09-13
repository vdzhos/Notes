package com.example.notes.notedetails

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.notes.MainActivity
import com.example.notes.R
import com.example.notes.database.NotesDatabase
import com.example.notes.databinding.FragmentNoteDetailsBinding
import com.example.notes.notesmain.NotesMainFragmentDirections
import com.example.notes.removeFocusAndKeyBoard
import com.example.notes.setFocusAndKeyBoardOnView
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class NoteDetailsFragment : Fragment() {

    private lateinit var binding: FragmentNoteDetailsBinding
    private lateinit var viewModel: NoteDetailsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = ""

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_note_details, container, false)

        val dataSource = NotesDatabase.getInstance(requireContext()).notesDatabaseDAO

        val viewModelFactory = NoteDetailsViewModelFactory(NoteDetailsFragmentArgs.fromBundle(requireArguments()).noteId, dataSource)
        viewModel = ViewModelProvider(this, viewModelFactory).get(NoteDetailsViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)

        if(NoteDetailsFragmentArgs.fromBundle(requireArguments()).openKeyboard) {
            requireActivity().setFocusAndKeyBoardOnView(binding.noteDetails)
        }

        setActionsForBottomAppBarIcons()

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_note_details_menu, menu)
    }

    private fun setActionsForBottomAppBarIcons(){
        binding.options.setOnClickListener {
            val action = NoteDetailsFragmentDirections.actionNoteDetailsFragmentToMoreOptionsBottomSheet()
            action.noteId = viewModel.noteId
            findNavController().navigate(action)
        }

        binding.addBox.setOnClickListener {
            val action = NoteDetailsFragmentDirections.actionNoteDetailsFragmentToAddContentBottomSheet()
            action.noteId = viewModel.noteId
            findNavController().navigate(action)
        }
    }

    override fun onStop() {
        viewModel.updateNote(binding.noteTitle.text.toString(), binding.noteDetails.text.toString())
        super.onStop()
    }

}

enum class Operation {
    DISPLAY, DELETE
}