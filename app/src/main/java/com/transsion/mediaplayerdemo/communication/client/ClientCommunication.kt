package com.transsion.mediaplayerdemo.communication.client

import android.util.Log
import com.transsion.mediaplayerdemo.communication.inter.CommunicationInterface
import com.transsion.mediaplayerdemo.communication.strategy.MessageHandler
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
class ClientCommunication(
    private val serverIp: String,
    private val serverPort: Int,
    private val messageListener: (String) -> Unit
) : CommunicationInterface {

    private var clientSocket: Socket? = null
    private var clientThread: Thread? = null
    private var output: PrintWriter? = null
    private val messageHandler: MessageHandler = ClientMessageHandler()

    override fun startCommunication() {
        clientThread = Thread {
            try {
                Log.d("Client", "Attempting to connect to server at $serverIp:$serverPort")
                clientSocket = Socket(serverIp, serverPort)
                output = PrintWriter(clientSocket!!.getOutputStream(), true)
                Log.d("Client", "Connected to server at $serverIp on port $serverPort")
                val input = BufferedReader(InputStreamReader(clientSocket!!.getInputStream()))

                var message: String?
                while (input.readLine().also { message = it } != null) {
                    message?.let {
                        messageListener(it)
                        messageHandler.handleMessage(it)
                    }
                }
            } catch (e: IOException) {
                handleIOException(e, "Client")
            } finally {
                closeSocket()
            }
        }
        clientThread?.start()
    }

    override fun stopCommunication() {
        clientThread?.interrupt()
        closeSocket()
    }

    override fun sendMessage(message: String) {
        Thread {
            try {
                if (clientSocket?.isConnected == true && !clientSocket?.isClosed!!) {
                    output?.println(message)
                    Log.d("Client", "Sending message: $message")
                }
            } catch (e: IOException) {
                Log.e("Client", "Error sending message", e)
            }
        }.start()
    }

    override fun onMessageReceived(message: String) {
        messageHandler.handleMessage(message)
    }

    private fun handleIOException(e: IOException, tag: String) {
        if (clientSocket?.isClosed == true) {
            Log.e(tag, "Socket is closed", e)
        } else {
            Log.e(tag, "Failed to connect or read from server", e)
        }
    }

    private fun closeSocket() {
        try {
            clientSocket?.close()
        } catch (e: IOException) {
            Log.e("Client", "Error when closing the socket", e)
        }
    }
}
