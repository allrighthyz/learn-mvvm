package com.transsion.mediaplayerdemo.ui.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.transsion.mediaplayerdemo.communication.client.ClientCommunication
import com.transsion.mediaplayerdemo.communication.server.ServerCommunication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CommunicationViewModel : ViewModel() {

    private val _messages = MutableLiveData<MutableList<String>>()
    val messages: LiveData<MutableList<String>> = _messages

    private var serverCommunication: ServerCommunication? = null
    private var clientCommunication: ClientCommunication? = null

    fun startServer(port: Int) {
        serverCommunication = ServerCommunication(port) { message ->
            Log.d("ServerComm", "Callback received message: $message")
            receiveMessage(message.toString())
        }.apply {
            startCommunication()
        }
    }

    fun startClient(ip: String, port: Int) {
        clientCommunication = ClientCommunication(ip, port) { message ->
            receiveMessage(message)
        }.apply {
            startCommunication()
        }
    }

    fun sendMessage(message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            serverCommunication?.sendMessage(message)
            clientCommunication?.sendMessage(message)
        }
        addMessageToLiveData(message)
    }

    private fun addMessageToLiveData(message: String) {
        val currentMessages = _messages.value ?: mutableListOf()
        currentMessages.add(message)
        _messages.postValue(currentMessages)
    }

    fun receiveMessage(message: String) {
        Log.d("viewModel", "Received message: $message")
        addMessageToLiveData(message)
    }

    override fun onCleared() {
        super.onCleared()
        serverCommunication?.stopCommunication()
        clientCommunication?.stopCommunication()
    }

    fun stopServer() {
        serverCommunication?.stopCommunication()
        serverCommunication = null
    }

    fun stopClient() {
        clientCommunication?.stopCommunication()
        clientCommunication = null
    }
}
