package com.example.notes.notesmain

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.notes.MainActivity
import com.example.notes.R
import com.example.notes.database.Note
import com.example.notes.database.NotesDatabase
import com.example.notes.databinding.FragmentNotesMainBinding
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class NotesMainFragment : Fragment() {

    private lateinit var binding: FragmentNotesMainBinding

    private var actionMode: ActionMode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notes_main, container, false)

        val dataSource = NotesDatabase.getInstance(requireContext()).notesDatabaseDAO

        val viewModelFactory = NotesMainViewModelFactory(dataSource)

        val viewModel = ViewModelProvider(this, viewModelFactory).get(NotesMainViewModel::class.java)

        val adapter = createAdapter(viewModel)

        binding.notesList.adapter = adapter

        viewModel.notes.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        viewModel.createStatus.observe(viewLifecycleOwner, Observer {
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
                findNavController().navigate(action)
            }
        }

        binding.lifecycleOwner = this

        return binding.root
    }

    private fun createActionModeCallback(viewModel: NotesMainViewModel, adapter: NotesAdapter): ActionMode.Callback? {
        return object : ActionMode.Callback {

            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                val inflater: MenuInflater = mode.menuInflater
                inflater.inflate(R.menu.action_mode_menu, menu)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                menu.findItem(R.id.pin).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                menu.findItem(R.id.reminder).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                menu.findItem(R.id.label).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
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

        viewModel.actionModeItemsSelected.observe(viewLifecycleOwner, Observer {
            actionMode?.title = viewModel.actionModeItemsSelected.value.toString()
            if(it>0){
                rvClickListener.setActionMode(true)
            }else{
                rvClickListener.setActionMode(false)
                actionMode?.finish()
            }
        })

        val adapter = NotesAdapter(rvClickListener)
        adapter.setHasStableIds(true)

        val actionModeCallback = createActionModeCallback(viewModel, adapter)

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

//    private fun setItemLongPressedMenuItems(menu: Menu){
//        (requireActivity() as MainActivity).setActionMenuViewVisibility(state)
//        menu.findItem(R.id.search).isVisible = !state
//        menu.findItem(R.id.cancel_button).isVisible = state
//        val tf = menu.findItem(R.id.notes_selected_tf)
//        tf.isVisible = state
//        (tf.actionView as TextView).text = "1"
//        val param = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                1.0f
//        )
//        (tf.actionView as TextView).layoutParams = param
//        menu.findItem(R.id.pin).isVisible = state
//        menu.findItem(R.id.reminder).isVisible = state
//        menu.findItem(R.id.label).isVisible = state
//    }

}