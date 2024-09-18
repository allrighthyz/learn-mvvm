package com.transsion.mediaplayerdemo.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.transsion.mediaplayerdemo.communication.ClientCommunication
import com.transsion.mediaplayerdemo.communication.ServerCommunication

class CommunicationViewModel : ViewModel() {
    private val _messages = MutableLiveData<MutableList<String>>()
    val messages: LiveData<MutableList<String>> = _messages

    private var serverCommunication: ServerCommunication? = null
    private var clientCommunication: ClientCommunication? = null

    fun startServer(port: Int) {
        serverCommunication = ServerCommunication(port) { message ->
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
        if (serverCommunication != null) {
            serverCommunication?.sendMessage(message)
        } else if (clientCommunication != null) {
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
