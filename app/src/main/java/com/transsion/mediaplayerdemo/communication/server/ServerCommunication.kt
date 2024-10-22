package com.transsion.mediaplayerdemo.communication.server

import android.util.Log
import com.transsion.mediaplayerdemo.communication.inter.CommunicationInterface
import com.transsion.mediaplayerdemo.communication.strategy.MessageHandler
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.Collections

class ServerCommunication(
    private val serverPort: Int,
    private val onMessageReceived: (String) -> Unit
) : CommunicationInterface {

    private var serverSocket: ServerSocket? = null
    private val clientHandlers = Collections.synchronizedList(mutableListOf<ClientHandler>())
    private var serverThread: Thread? = null
    private val messageHandler: MessageHandler = ServerMessageHandler(onMessageReceived)

    override fun startCommunication() {
        serverThread = Thread {
            try {
                serverSocket = ServerSocket(serverPort)
                Log.d("Server", "Server started on port $serverPort")
                while (!Thread.currentThread().isInterrupted) {
                    val clientSocket = serverSocket!!.accept()
                    Log.d("Server", "Client connected from ${clientSocket.inetAddress.hostAddress}")
                    val clientHandler = ClientHandler(clientSocket)
                    clientHandlers.add(clientHandler)
                    Thread(clientHandler).start()
                }
            } catch (e: IOException) {
                Log.e("Server", "Server failed to start on port $serverPort", e)
            }
        }
        serverThread?.start()
    }

    override fun stopCommunication() {
        serverThread?.interrupt()
        serverSocket?.close()
        clientHandlers.forEach { it.stopClientHandler() }
    }

    override fun sendMessage(message: String) {
        Log.d("Server", "Broadcasting message to clients: $message")
        clientHandlers.forEach { it.sendMessage(message) }
    }

    override fun onMessageReceived(message: String) {
        messageHandler.handleMessage(message)
    }

    // ClientHandler handles individual client connections
    private inner class ClientHandler(private val clientSocket: Socket) : Runnable {
        private val input = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
        private val output = PrintWriter(clientSocket.getOutputStream(), true)

        override fun run() {
            try {
                var message: String?
                while (input.readLine().also { message = it } != null) {
                    message?.let {
                        messageHandler.handleMessage(it)
                        broadcastMessage(it)
                    }
                    Log.d("Server", "Message from client: $message")
                }
            } catch (e: IOException) {
                Log.e("Server", "Error handling client connection", e)
            } finally {
                stopClientHandler()
            }
        }

        fun sendMessage(message: String) {
            if (!clientSocket.isClosed) {
                Log.d("Server", "Sending message to client: $message")
                output.println(message)
            }
        }

        fun stopClientHandler() {
            try {
                input.close()
                output.close()
                clientSocket.close()
                clientHandlers.remove(this)
            } catch (e: IOException) {
                Log.e("Server", "Error closing client handler", e)
            }
        }

        private fun broadcastMessage(message: String) {
            Log.d("Server", "Broadcasting message: $message")
            clientHandlers.forEach { it.sendMessage(message) }
        }
    }

}