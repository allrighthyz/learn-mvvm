package com.transsion.mediaplayerdemo.communication.client

import android.util.Log
import com.transsion.mediaplayerdemo.communication.strategy.MessageHandler

// 客户端消息处理策略
class ClientMessageHandler : MessageHandler {
    override fun handleMessage(message: String) {
        Log.d("Client", "Received message: $message")
    }
}