package com.transsion.mediaplayerdemo.communication.strategy

interface MessageHandler {
    fun handleMessage(message: String)
}