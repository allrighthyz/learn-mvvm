package com.transsion.mediaplayerdemo.communication

import android.util.Log
import com.transsion.mediaplayerdemo.common.CommunicationInterface
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.Collections

class ServerCommunication(private val serverPort: Int, param: (Any) -> Unit) : CommunicationInterface {
    private var serverSocket: ServerSocket? = null
    private val clients = Collections.synchronizedList(mutableListOf<Socket>())
    private val clientHandlers = Collections.synchronizedList(mutableListOf<ClientHandler>())
    private var serverThread: Thread? = null

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
                    // 使用已经创建的 clientHandler 实例启动线程
                    Thread(clientHandler).start()
                }
            } catch (e: IOException) {
                Log.e("Server", "Server failed to start on port $serverPort", e)
                e.printStackTrace()
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
        // Broadcast message to all clients
        clientHandlers.forEach { clientHandler ->
            clientHandler.sendMessage(message)
        }
    }

    override fun onMessageReceived(message: String) {
        // Handle received message
    }

    // ClientHandler 内部类，处理客户端连接的线程
    private inner class ClientHandler(private val clientSocket: Socket) : Runnable {
        private val input = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
        private val output = PrintWriter(clientSocket.getOutputStream(), true)

        override fun run() {
            try {
                var message: String?
                while (input.readLine().also { message = it } != null) {
                    message?.let { broadcastMessage(it) }
                }
            } catch (e: IOException) {
                Log.e("Server", "Error handling client connection", e)
                e.printStackTrace()
            } finally {
                stopClientHandler()
            }
        }

        fun sendMessage(message: String) {
            if (!clientSocket.isClosed) {
                PrintWriter(clientSocket.getOutputStream(), true).use { writer ->
                    writer.println(message)
                }
            }
        }

        fun stopClientHandler() {
            try {
                input.close()
                output.close()
                clientSocket.close()
                clientHandlers.remove(this)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        private fun broadcastMessage(message: String) {
            clientHandlers.forEach { it.sendMessage(message) }
        }
    }

}
