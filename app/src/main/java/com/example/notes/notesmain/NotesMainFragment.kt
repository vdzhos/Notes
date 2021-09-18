package com.example.notes.notesmain

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.notes.MainActivity
import com.example.notes.R
import com.example.notes.database.NotesDatabase
import com.example.notes.databinding.FragmentNotesMainBinding
import com.example.notes.notedetails.NoteDetailsFragmentArgs
import com.example.notes.notedetails.Operation
import com.example.notes.removeFocusAndKeyBoard
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class NotesMainFragment : Fragment() {

    private lateinit var binding: FragmentNotesMainBinding
    private lateinit var viewModel: NotesMainViewModel
    private var actionMode: ActionMode? = null
    private var onBackPressed: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressed = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notes_main, container, false)

        val dataSource = NotesDatabase.getInstance(requireContext()).notesDatabaseDAO

        val listType = NotesMainFragmentArgs.fromBundle(requireArguments()).listType
        setTitle(listType)

        val viewModelFactory = NotesMainViewModelFactory(listType, dataSource)

        viewModel = ViewModelProvider(this, viewModelFactory).get(NotesMainViewModel::class.java)

        val adapter = createAdapter(viewModel)

        binding.notesList.adapter = adapter

        if(listType != ListType.LABEL) {
            viewModel.notes.observe(viewLifecycleOwner, Observer {
                it?.let {
                    adapter.submitList(it)
                }
            })
        } else {
            viewModel.notesWithLabels.observe(viewLifecycleOwner, Observer {
                it?.let {
                    adapter.submitList(it)
                }
            })
        }

        viewModel.showLoading.observe(viewLifecycleOwner, Observer {
            when (it) {
                true -> {
                    (requireActivity() as MainActivity).setLoadingPanelVisibility(true)
                }
                false -> {
                    (requireActivity() as MainActivity).setLoadingPanelVisibility(false)
                }
            }
        })

        binding.fab.setOnClickListener {
            lifecycleScope.launch {
                val result = async {
                    viewModel.createEmptyNote()
                }
                val action = NotesMainFragmentDirections.actionNotesMainFragmentToNoteDetailsFragment()
                action.noteId = result.await()
                action.openKeyboard = true
                findNavController().navigate(action)
            }
        }

        requireActivity().removeFocusAndKeyBoard()

        binding.lifecycleOwner = this

        setHasOptionsMenu(true)

        if(!onBackPressed) executeOperation()

        return binding.root
    }

    private fun executeOperation(){
        val operation = NotesMainFragmentArgs.fromBundle(requireArguments()).operation

        if(operation == Operation.DELETE){
            val noteId = NotesMainFragmentArgs.fromBundle(requireArguments()).noteId
            viewModel.deleteNote(noteId)
        }

        onBackPressed = true
    }

    private fun setTitle(listType: ListType){
        when(listType){
            ListType.REMINDERS -> (requireActivity() as AppCompatActivity).supportActionBar?.title = "Reminders"
            ListType.LABEL -> (requireActivity() as AppCompatActivity).supportActionBar?.title = listType.info
            else -> (requireActivity() as AppCompatActivity).supportActionBar?.title = "Notes"
        }
    }

    private fun createActionModeCallback(viewModel: NotesMainViewModel, adapter: NotesAdapter): ActionMode.Callback? {
        return object : ActionMode.Callback {

            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                val inflater: MenuInflater = mode.menuInflater
                inflater.inflate(R.menu.action_mode_menu, menu)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                var item = menu.findItem(R.id.pin)
                item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                item.icon.setTint(ResourcesCompat.getColor(resources, R.color.action_mode_blue, null))

                item = menu.findItem(R.id.reminder)
                item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                item.icon.setTint(ResourcesCompat.getColor(resources, R.color.action_mode_blue, null))

                item = menu.findItem(R.id.label)
                item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                item.icon.setTint(ResourcesCompat.getColor(resources, R.color.action_mode_blue, null))

                item = menu.findItem(R.id.delete)
                item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
                return false
            }

            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                return when (item.itemId) {
                    R.id.pin -> {
                        //TODO: implement action
                        mode.finish()
                        true
                    }
                    R.id.reminder -> {
                        //TODO: implement action
                        mode.finish()
                        true
                    }
                    R.id.label -> {
                        //TODO: implement action
                        mode.finish()
                        true
                    }
                    R.id.delete -> {
                        viewModel.deleteNotes()
                        mode.finish()
                        true
                    }
                    else -> false
                }
            }

            override fun onDestroyActionMode(mode: ActionMode) {
                actionMode = null
                val selectedNotesAmount = viewModel.actionModeItemsSelected.value
                if(selectedNotesAmount!!!=0L){
                    val selectedNotes = viewModel.getSelectedNotes()
                    for (note in selectedNotes){
                        note.isSelected = false
                        adapter.notifyItemChanged(binding.notesList.findViewHolderForItemId(note.noteId).adapterPosition)
                    }
                    viewModel.clearSelectedNotes()
                }
            }
        }
    }

    private fun createAdapter(viewModel: NotesMainViewModel): NotesAdapter{
        val onClick: (noteId: Long) -> Unit = { noteId ->
            val action = NotesMainFragmentDirections.actionNotesMainFragmentToNoteDetailsFragment()
            action.noteId = noteId
            findNavController().navigate(action)
        }

        val rvClickListener = NoteListener(onClick, null, null)

        val adapter = NotesAdapter(rvClickListener)
        adapter.setHasStableIds(true)

        val actionModeCallback = createActionModeCallback(viewModel, adapter)

        viewModel.actionModeItemsSelected.observe(viewLifecycleOwner, Observer {
            if(it>0 && actionMode==null) {
                actionMode = requireActivity().startActionMode(actionModeCallback)
            }
            actionMode?.title = viewModel.actionModeItemsSelected.value.toString()
            if(it>0){
                rvClickListener.setActionMode(true)
            }else{
                rvClickListener.setActionMode(false)
                actionMode?.finish()
            }
        })

        rvClickListener.selectClickListener = { noteId ->
            val note = viewModel.getNoteById(noteId)!!
            note.isSelected = !note.isSelected
            if(note.isSelected) viewModel.addToSelectedItemsList(note)
            else viewModel.removeFromSelectedItemsList(note)
            adapter.notifyItemChanged(binding.notesList.findViewHolderForItemId(note.noteId).adapterPosition)
        }

        rvClickListener.longClickListener = { noteId ->
            when (actionMode) {
                null -> {
                    actionMode = requireActivity().startActionMode(actionModeCallback)
                    actionMode?.title = "1"
                    val note = viewModel.getNoteById(noteId)!!
                    note.isSelected = true
                    viewModel.addToSelectedItemsList(note)
                    adapter.notifyItemChanged(binding.notesList.findViewHolderForItemId(note.noteId).adapterPosition)
                    true
                }
                else -> false
            }
        }
        return adapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        binding.bottomAppBar.replaceMenu(R.menu.bottomappbar_menu)
        bottomAppBarItemClick(binding.bottomAppBar.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    private fun bottomAppBarItemClick(bottomMenu: Menu, toolbarMenu: Menu) {
        bottomMenu.findItem(R.id.checkbox_menu)?.setOnMenuItemClickListener {
            Toast.makeText(requireContext(), "Message", Toast.LENGTH_SHORT).show()
            true
        }
    }
    

}

enum class ListType(var info: String?){
    ALL(null),
    REMINDERS(null),
    LABEL(null)
}