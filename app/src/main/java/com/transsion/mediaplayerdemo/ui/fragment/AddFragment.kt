package com.transsion.mediaplayerdemo.ui.fragment

import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.transsion.mediaplayerdemo.data.database.AppDatabase
import com.transsion.mediaplayerdemo.data.repository.UserRepository
import com.transsion.mediaplayerdemo.databinding.FragmentAddBinding
import com.transsion.mediaplayerdemo.ui.ViewModel.AddViewModel
import com.transsion.mediaplayerdemo.ui.ViewModel.UserViewModelFactory
class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    private var avatarUri: Uri? = null

    private val viewModel: AddViewModel by viewModels {
        UserViewModelFactory(UserRepository(AppDatabase.getDatabase(requireContext()).userDao()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val selectImageLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                avatarUri = it
                binding.avatarImageView.setImageURI(it)
            }
        }

        binding.selectImageButton.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        binding.addUserButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val phone = binding.phoneEditText.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty()) {
                viewModel.insertUser(name, email, phone, avatarUri)
            }
        }

        // 观察用户添加状态
        viewModel.userAdded.observe(viewLifecycleOwner) { added ->
            if (added) {
                // 用户添加成功后清空输入框
                binding.nameEditText.text.clear()
                binding.emailEditText.text.clear()
                binding.phoneEditText.text.clear()
                avatarUri = null
                binding.avatarImageView.setImageResource(android.R.drawable.ic_input_add)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}