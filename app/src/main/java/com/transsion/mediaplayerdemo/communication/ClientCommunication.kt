package com.transsion.mediaplayerdemo.communication

import android.util.Log
import com.transsion.mediaplayerdemo.common.CommunicationInterface
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class ClientCommunication(private val serverIp: String, private val serverPort: Int,
                          private val messageListener: (String) -> Unit) :
    CommunicationInterface {
    private var clientSocket: Socket? = null
    private var clientThread: Thread? = null

    override fun startCommunication() {
        clientThread = Thread {
            try {
                Log.d("Client", "Attempting to connect to server at $serverIp:$serverPort")
                clientSocket = Socket(serverIp, serverPort)
                Log.d("Client", "Connected to server at $serverIp on port $serverPort")
                val input = BufferedReader(InputStreamReader(clientSocket!!.getInputStream()))

                var message: String?
                while (input.readLine().also { message = it } != null) {
                    message?.let { messageListener(it) }
                }
            } catch (e: IOException) {
                if (clientSocket?.isClosed == true) {
                    Log.e("Client", "Socket is closed", e)
                } else {
                    Log.e("Client", "Failed to connect or read from server", e)
                }
            } finally {

                try {
                    clientSocket?.close()
                } catch (e: IOException) {
                    Log.e("Client", "Error when closing the socket", e)
                }

            }
        }
        clientThread?.start()
    }

    override fun stopCommunication() {
        clientThread?.interrupt()
        try {
            clientSocket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun sendMessage(message: String) {
        Thread {
            try {
                clientSocket?.getOutputStream()?.let { outputStream ->
                    PrintWriter(outputStream, true).use { writer ->
                        writer.println(message)
                    }
                }
                Log.d("Client", "Sending message: $message")
            } catch (e: IOException) {
                Log.e("Client", "Error sending message", e)
            }
        }.start()
    }

    override fun onMessageReceived(message: String) {

    }
}
