package com.transsion.mediaplayerdemo.ui.fragment

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.transsion.mediaplayerdemo.R
import com.transsion.mediaplayerdemo.data.database.AppDatabase
import com.transsion.mediaplayerdemo.data.repository.UserRepository
import com.transsion.mediaplayerdemo.databinding.FragmentAddBinding
import com.transsion.mediaplayerdemo.ui.viewModel.AddViewModel

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AddViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        val repository = UserRepository(AppDatabase.getDatabase(requireContext()).userDao())
        viewModel = AddViewModel(repository)
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val selectImageLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                viewModel.avatarUri = it
                binding.avatarImageView.setImageURI(it)
            }
        }

        viewModel.selectImageEvent.observe(viewLifecycleOwner) {
            selectImageLauncher.launch("image/*")
        }

        viewModel.userAdded.observe(viewLifecycleOwner) { added ->
            if (added) {
                // 用户添加成功后清空输入框
                binding.nameEditText.text.clear()
                binding.emailEditText.text.clear()
                binding.phoneEditText.text.clear()
                viewModel.avatarUri = null
                binding.avatarImageView.setImageResource(android.R.drawable.ic_input_add)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}