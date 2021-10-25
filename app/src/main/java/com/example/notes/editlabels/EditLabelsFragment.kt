package com.example.notes.editlabels

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.notes.R
import com.example.notes.databinding.FragmentCreateNewLabelBinding

class EditLabelsFragment : Fragment() {

    private lateinit var binding: FragmentCreateNewLabelBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Edit labels"
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_create_new_label,container,false)

        return binding.root
    }

}