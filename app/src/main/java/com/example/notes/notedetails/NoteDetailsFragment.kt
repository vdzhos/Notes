package com.example.notes.notedetails

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.notes.MainActivity
import com.example.notes.R
import com.example.notes.database.Note
import com.example.notes.database.NotesDatabase
import com.example.notes.databinding.FragmentNoteDetailsBinding
import com.example.notes.myviews.Tag
import com.example.notes.setFocusAndKeyBoardOnView
import com.google.android.flexbox.FlexboxLayout
import java.util.*

class NoteDetailsFragment : Fragment() {

    private lateinit var binding: FragmentNoteDetailsBinding
    private lateinit var viewModel: NoteDetailsViewModel
    private var onBackPressed: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressed = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = ""

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_note_details, container, false)

        val dataSource = NotesDatabase.getInstance(requireContext()).notesDatabaseDAO

        val viewModelFactory = NoteDetailsViewModelFactory(NoteDetailsFragmentArgs.fromBundle(requireArguments()).noteId, dataSource)
        viewModel = ViewModelProvider(this, viewModelFactory).get(NoteDetailsViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.showLoading.observe(viewLifecycleOwner, Observer {
            when (it) {
                true -> {
                    binding.noteTitle.isFocusable = false
                    binding.noteDetails.isFocusable = false
                    (requireActivity() as MainActivity).setLoadingPanelVisibility(true)
                }
                false -> {
                    binding.noteTitle.isFocusable = true
                    binding.noteDetails.isFocusable = true
                    (requireActivity() as MainActivity).setLoadingPanelVisibility(false)
                }
            }
        })
        viewModel.updateNoteDate(NoteDetailsFragmentArgs.fromBundle(requireArguments()).reminder)

        setHasOptionsMenu(true)

        if(NoteDetailsFragmentArgs.fromBundle(requireArguments()).openKeyboard) {
            requireActivity().setFocusAndKeyBoardOnView(binding.noteDetails)
        }

        setActionsForBottomAppBarIcons()

        if(!onBackPressed) executeOperation()

        return binding.root
    }

    private fun executeOperation(){
        val operation = NoteDetailsFragmentArgs.fromBundle(requireArguments()).operation

        if(operation == Operation.MAKE_A_COPY){
            viewModel.makeACopy()
        } else if(operation == Operation.SEND){
            viewModel.sendNote { startActivitySendIntent(it) }
        }

        onBackPressed = true
    }

    private fun startActivitySendIntent(note: Note){
        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.type = "text/plain"
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, note.title)
        sendIntent.putExtra(Intent.EXTRA_TITLE, note.title)
        sendIntent.putExtra(Intent.EXTRA_TEXT, note.note)
        startActivity(sendIntent)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_note_details_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.pin -> {
                return true
            }
            R.id.reminder -> {
                val action = NoteDetailsFragmentDirections.actionNoteDetailsFragmentToReminderMainBottomSheet()
                action.noteId = viewModel.noteId
                findNavController().navigate(action)
                return true
            }
            R.id.label -> {
                return true
            }
        }
        return false
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.pin).icon.setTint(ResourcesCompat.getColor(resources, R.color.gray, null))
        menu.findItem(R.id.reminder).icon.setTint(ResourcesCompat.getColor(resources, R.color.gray, null))
        menu.findItem(R.id.label).icon.setTint(ResourcesCompat.getColor(resources, R.color.gray, null))
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
    DISPLAY, DELETE, MAKE_A_COPY, SEND
}