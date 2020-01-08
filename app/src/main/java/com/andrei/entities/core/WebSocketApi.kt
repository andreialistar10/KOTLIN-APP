package com.andrei.entities.core

import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient

object WebSocketApi {

    private var stompClient: StompClient? = null

    private const val WEB_SOCKET_API = "ws://" + Api.HOST_IP + ":8099/ws/websocket"

    private const val TOPIC = "/topic/messages"

    fun connectToWebSocket() {

        disconnect()
        connect()
    }

    fun disconnect() {

        stompClient?.disconnect()
    }

    private fun connect() {

        val nStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, WEB_SOCKET_API)
        nStompClient.connect()
        val disposable = nStompClient.topic(TOPIC).subscribe { t -> println(t.payload) }
    }
}