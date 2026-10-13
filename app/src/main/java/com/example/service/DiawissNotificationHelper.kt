package com.example.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

object DiawissNotificationHelper {
    const val CHANNEL_ID = "diawiss_order_status_channel"
    const val CHANNEL_NAME = "Suivi des Commandes DIAWISS"
    const val PREFS_NAME = "diawiss_notifications_prefs"
    const val KEY_FCM_TOKEN = "fcm_token"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val descriptionText = "Notifications concernant le changement de statut de votre tenue de soutenance"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = descriptionText
                enableLights(true)
                enableVibration(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showOrderStatusNotification(
        context: Context,
        orderNumber: String,
        status: String,
        customMessage: String? = null
    ) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("EXTRA_ORDER_NUMBER", orderNumber)
            putExtra("EXTRA_NAVIGATE_TO", "tracking")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            orderNumber.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val messageBody = customMessage ?: when (status) {
            "Prête", "Prête à récupérer" -> "🎓 Bonne nouvelle ! Votre tenue est prête à être récupérée à l'atelier DIAWISS de Béjaïa 06 !"
            "En préparation" -> "🧵 Nos maîtres tailleurs confectionnent actuellement votre tenue de soutenance."
            "Expédiée" -> "🚚 Votre colis DIAWISS a été remis au transporteur et est en route vers votre wilaya."
            "Livrée" -> "✨ Félicitations ! Votre commande a été livrée. Toute l'équipe DIAWISS vous souhaite plein succès pour votre soutenance !"
            "Confirmée" -> "✅ Votre commande DIAWISS a été validée par notre équipe."
            "À confirmer" -> "📞 Notre équipe va vous contacter pour valider les mesures et la livraison."
            else -> "Votre commande DIAWISS est désormais au statut : $status."
        }

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("DIAWISS • $orderNumber")
            .setContentText(messageBody)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody))
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(orderNumber.hashCode(), notificationBuilder.build())
    }

    fun saveFcmToken(context: Context, token: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_FCM_TOKEN, token).apply()
    }

    fun getFcmToken(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_FCM_TOKEN, null)
    }
}
