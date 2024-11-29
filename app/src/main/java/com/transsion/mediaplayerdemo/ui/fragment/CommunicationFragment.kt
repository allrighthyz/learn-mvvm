package com.transsion.mediaplayerdemo.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.transsion.mediaplayerdemo.R
import com.transsion.mediaplayerdemo.ui.viewModel.CommunicationViewModel
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_communication, container, false)
        viewModel = ViewModelProvider(this)[CommunicationViewModel::class.java]
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = viewLifecycleOwner
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.chatRecyclerView?.layoutManager = LinearLayoutManager(context)
        binding?.chatRecyclerView?.adapter = messageAdapter

        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            messages?.let {
                messageAdapter.updateMessages(it)
                binding?.chatRecyclerView?.scrollToPosition(it.size - 1)
            }
        }
    }

    fun onServerButtonClicked() {
        val portStr = binding?.portNumber?.text.toString()
        val port = portStr.toIntOrNull()
        if (port == null || port !in 1..65535) {
            Toast.makeText(context, "Invalid port number", Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.startServer(port)
        binding?.ipAddress?.visibility = View.GONE
    }

    fun onClientButtonClicked() {
        val ip = binding?.ipAddress?.text.toString()
        val portStr = binding?.portNumber?.text.toString()
        val port = portStr.toIntOrNull()
        if (port == null || port !in 1..65535) {
            Toast.makeText(context, "Invalid port number", Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.startClient(ip, port)
        binding?.ipAddress?.visibility = View.VISIBLE
    }

    fun onSendButtonClicked() {
        val message = binding?.messageEditText?.text.toString()
        if (message.isNotEmpty()) {
            viewModel.sendMessage(message)
            binding?.messageEditText?.text?.clear()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.stopServer()
        viewModel.stopClient()
        binding = null
    }
}


