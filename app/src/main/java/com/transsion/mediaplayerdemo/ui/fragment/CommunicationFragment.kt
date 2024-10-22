package com.transsion.mediaplayerdemo.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.transsion.mediaplayerdemo.R
import com.transsion.mediaplayerdemo.ui.ViewModel.CommunicationViewModel
import com.transsion.mediaplayerdemo.adapter.MessageAdapter
import com.transsion.mediaplayerdemo.databinding.FragmentCommunicationBinding

class CommunicationFragment : Fragment() {

    private lateinit var viewModel: CommunicationViewModel
    private var binding: FragmentCommunicationBinding? = null
    private val messageAdapter = MessageAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommunicationBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[CommunicationViewModel::class.java]

        // Setup RecyclerView with View Binding
        binding?.chatRecyclerView?.layoutManager = LinearLayoutManager(context)
        binding?.chatRecyclerView?.adapter = messageAdapter

        // Observe LiveData from ViewModel
        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            messages?.let {
                messageAdapter.updateMessages(it)
                binding?.chatRecyclerView?.scrollToPosition(it.size - 1)
            }
        }

        binding?.startServerButton?.setOnClickListener {
            val portStr = binding?.portNumber?.text.toString()
            val port = portStr.toIntOrNull()
            if (port == null || port !in 1..65535) {
                Toast.makeText(context, "Invalid port number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.startServer(port)
            binding?.ipAddress?.visibility = View.GONE
        }

        binding?.startClientButton?.setOnClickListener {
            val ip = binding?.ipAddress?.text.toString()
            val portStr = binding?.portNumber?.text.toString()
            val port = portStr.toIntOrNull()
            if (port == null || port !in 1..65535) {
                Toast.makeText(context, "Invalid port number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.startClient(ip, port)
            binding?.ipAddress?.visibility = View.VISIBLE
        }
//        binding?.radioGroup?.setOnCheckedChangeListener { group, checkedId ->
//            when (checkedId) {
//                binding?.radioClient?.id -> {
//                    val ip = binding?.ipAddress?.text.toString()
//                    val portStr = binding?.portNumber?.text.toString()
//                    val port = portStr.toIntOrNull()
//                    if (ip.isEmpty()) {
//                        Toast.makeText(context, "IP address cannot be empty", Toast.LENGTH_SHORT).show()
//                        return@setOnCheckedChangeListener
//                    }
//                    if (port == null || port !in 1..65535) {
//                        Toast.makeText(context, "Invalid port number", Toast.LENGTH_SHORT).show()
//                        return@setOnCheckedChangeListener
//                    }
//                    viewModel.startClient(ip, port)
//                    binding?.ipAddress?.visibility = View.VISIBLE
//                }
//                binding?.radioServer?.id -> {
//                    val portStr = binding?.portNumber?.text.toString()
//                    val port = portStr.toIntOrNull()
//                    if (port == null || port !in 1..65535) {
//                        Toast.makeText(context, "Invalid port number", Toast.LENGTH_SHORT).show()
//                        return@setOnCheckedChangeListener
//                    }
//                    viewModel.startServer(port)
//                    binding?.ipAddress?.visibility = View.GONE
//                }
//            }
//        }


        binding?.sendButton?.setOnClickListener {
            val message = binding?.messageEditText?.text.toString()
            if (message.isNotEmpty()) {
                viewModel.sendMessage(message)
                binding?.messageEditText?.text?.clear()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.stopServer()
        viewModel.stopClient()
        binding = null
    }
}


