package com.transsion.mediaplayerdemo.communication.factory

import com.transsion.mediaplayerdemo.communication.client.ClientCommunication
import com.transsion.mediaplayerdemo.communication.inter.CommunicationInterface
import com.transsion.mediaplayerdemo.communication.server.ServerCommunication

// 工厂类，用于创建通信对象
object CommunicationFactory {
    fun createClient(serverIp: String, serverPort: Int, messageListener: (String) -> Unit): CommunicationInterface {
        return ClientCommunication(serverIp, serverPort, messageListener)
    }

    fun createServer(serverPort: Int, onMessageReceived: (String) -> Unit): CommunicationInterface {
        return ServerCommunication(serverPort, onMessageReceived)
    }
}