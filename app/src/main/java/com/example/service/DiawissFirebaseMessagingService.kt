package com.example.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class DiawissFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Nouveau FCM Token reçu : $token")
        DiawissNotificationHelper.saveFcmToken(applicationContext, token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Message FCM reçu de : ${remoteMessage.from}")

        val data = remoteMessage.data
        val orderNumber = data["order_number"]
            ?: data["orderNumber"]
            ?: remoteMessage.notification?.title?.substringAfter("•")?.trim()
            ?: "DIA-2026-SOUTENANCE"

        val status = data["status"]
            ?: "Prête à récupérer"

        val customMessage = data["message"]
            ?: remoteMessage.notification?.body

        DiawissNotificationHelper.showOrderStatusNotification(
            context = applicationContext,
            orderNumber = orderNumber,
            status = status,
            customMessage = customMessage
        )
    }

    companion object {
        private const val TAG = "DiawissFCM"
    }
}
