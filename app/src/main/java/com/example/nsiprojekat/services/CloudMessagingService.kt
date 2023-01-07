package com.example.nsiprojekat.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.example.nsiprojekat.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class CloudMessagingService : FirebaseMessagingService() {
    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d("CMSERVICE", "Refreshed token: $token")

        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?) {
        Log.d("CMSERVICE", "sendRegistrationTokenToServer($token)")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val notifTitle = message.notification!!.title
        val notifText = message.notification!!.body
        val CHANNEL_ID = "CM_NOTIFICATION"
        val channel: NotificationChannel = NotificationChannel(
            CHANNEL_ID,
            "Cloud Messaging Notification",
            NotificationManager.IMPORTANCE_HIGH
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)

        val notif = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle(notifTitle).setContentText(notifText)
            .setSmallIcon(R.drawable.ic_stat_ic_notification).setAutoCancel(true).build()
        NotificationManagerCompat.from(this).notify(1, notif)
        super.onMessageReceived(message)
    }
}