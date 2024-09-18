package com.transsion.mediaplayerdemo.common

interface CommunicationInterface {

    fun startCommunication()
    fun stopCommunication()
    fun sendMessage(message: String)
    fun onMessageReceived(message: String)

}