package com.transsion.mediaplayerdemo.communication.server

import android.util.Log
import com.transsion.mediaplayerdemo.communication.strategy.MessageHandler

// 服务器消息处理策略
class ServerMessageHandler(private val onMessageReceived: (String) -> Unit) : MessageHandler {
    override fun handleMessage(message: String) {
        onMessageReceived.invoke(message)
        Log.d("Server", "Received message from client: $message")
    }
}