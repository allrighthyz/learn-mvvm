package com.transsion.mediaplayerdemo.ui.fragment

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.transsion.mediaplayerdemo.R
import com.transsion.mediaplayerdemo.ui.ViewModel.AddViewModel

class AddFragment : Fragment() {

    companion object {
        fun newInstance() = AddFragment()
    }

    private val viewModel: AddViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_add, container, false)
    }
}