package com.example.notes.notesmain

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.database.Note
import com.example.notes.databinding.NoteItemBinding
import com.example.notes.setTags

class NotesAdapter(private val clickListener: NoteListener): ListAdapter<Note, NotesAdapter.ViewHolder>(NotesDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).noteId
    }

    class ViewHolder private constructor(private val binding: NoteItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Note, clickListener: NoteListener) {
            binding.note = item
            binding.clickListener = clickListener

            if (item.title.isNotEmpty()) {
                binding.titleField.text = item.title
                binding.titleField.visibility = View.VISIBLE
            }else{
                binding.titleField.visibility = View.GONE
            }
            if (item.note.isNotEmpty()) {
                binding.noteField.text = item.note
                binding.noteField.visibility = View.VISIBLE
            }else{
                binding.noteField.visibility = View.GONE
                //TODO: remove bottom margin
            }
            if(item.isSelected){
                binding.layout.background = ResourcesCompat.getDrawable(itemView.context.resources, R.drawable.note_item_bg_selected, null)
            }else{
                binding.layout.background = ResourcesCompat.getDrawable(itemView.context.resources, R.drawable.note_item_bg, null)
            }
            itemView.setOnLongClickListener {
                clickListener.onLongClick(item)!!
            }
            binding.tagsFlexbox.setTags(item.reminder, item.labels,false)
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = NoteItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }

}

class NoteListener(val clickListener:  (noteId: Long) -> Unit,
                   var longClickListener: ((noteId: Long) -> Boolean?)?,
                   var selectClickListener:  ((noteId: Long) -> Unit)?) {

    var actionMode = false
        private set

    fun onClick(note: Note) {
        if(actionMode) {
            selectClickListener?.let { it(note.noteId) }
        } else {
            clickListener(note.noteId)
        }
    }

    fun onLongClick(note: Note) = longClickListener?.let { it(note.noteId) }

    fun setActionMode(mode: Boolean){
        actionMode = mode
    }
}

class NotesDiffCallback : DiffUtil.ItemCallback<Note>(){

    override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem.noteId == newItem.noteId
    }

    override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem == newItem
    }

}