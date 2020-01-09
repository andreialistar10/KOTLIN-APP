package com.andrei.entities.core.synchronizing

import com.andrei.entities.core.synchronizing.notification.MyNotification

interface MessageWorker {

    fun onMessageArrived(notification: MyNotification)
}