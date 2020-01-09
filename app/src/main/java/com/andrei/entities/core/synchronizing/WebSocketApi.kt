package com.andrei.entities.core.synchronizing

import com.andrei.entities.core.Api
import com.andrei.entities.core.synchronizing.notification.MyNotification
import com.google.gson.GsonBuilder
import io.reactivex.disposables.Disposable
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient

object WebSocketApi {

    private var stompClient: StompClient? = null

    private var topicSubscription: Disposable? = null

    private const val WEB_SOCKET_API = "ws://" + Api.HOST_IP + ":8099/ws/websocket"

    private const val TOPIC = "/topic/messages"

    private val gson = GsonBuilder().create()

    fun connectToWebSocket(messageWorker: MessageWorker) {

        disconnect()
        connect(messageWorker)
    }

    fun disconnect() {

        topicSubscription?.let {
            if (!it.isDisposed)
                it.dispose()
        }
        stompClient?.disconnect()
    }

    private fun connect(messageWorker: MessageWorker) {

        val nStompClient = Stomp.over(
            Stomp.ConnectionProvider.OKHTTP,
            WEB_SOCKET_API
        )
        nStompClient.connect()
        topicSubscription = nStompClient.topic(TOPIC)
            .subscribe { notification ->
                val myNotification = gson?.fromJson(notification.payload,MyNotification::class.java)
                myNotification?.let { messageWorker.onMessageArrived(it) }
            }
    }
}