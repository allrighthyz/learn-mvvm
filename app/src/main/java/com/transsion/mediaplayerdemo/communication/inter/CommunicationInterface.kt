package com.transsion.mediaplayerdemo.communication.inter

interface CommunicationInterface {

    fun startCommunication()
    fun stopCommunication()
    fun sendMessage(message: String)
    fun onMessageReceived(message: String)

}