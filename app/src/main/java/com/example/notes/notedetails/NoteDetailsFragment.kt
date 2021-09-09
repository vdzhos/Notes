package com.example.notes.notedetails

import android.os.Bundle
import android.view.*
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
import com.example.notes.removeFocusAndKeyBoard
import com.example.notes.setFocusAndKeyBoardOnView
import kotlinx.coroutines.async
import kotlinx.coroutines.launch




class NoteDetailsFragment : Fragment() {

    private lateinit var binding: FragmentNoteDetailsBinding
    private lateinit var viewModel: NoteDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
                this,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        updateNoteWhenBackPressed()
                    }
                })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_note_details, container, false)

        val dataSource = NotesDatabase.getInstance(requireContext()).notesDatabaseDAO

        val viewModelFactory = NoteDetailsViewModelFactory(NoteDetailsFragmentArgs.fromBundle(requireArguments()).noteId, dataSource)
        viewModel = ViewModelProvider(this, viewModelFactory).get(NoteDetailsViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.updateStatus.observe(viewLifecycleOwner, Observer {
            when(it){
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

        setHasOptionsMenu(true)

        requireActivity().setFocusAndKeyBoardOnView(binding.noteDetails)

        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            updateNoteWhenBackPressed()
        }
        return true
    }

    private fun updateNoteWhenBackPressed(){
        requireActivity().removeFocusAndKeyBoard()
        lifecycleScope.launch {
            async {
                viewModel.updateNote(binding.noteTitle.text.toString(), binding.noteDetails.text.toString())
            }.await()
            findNavController().navigateUp()
        }
    }

}