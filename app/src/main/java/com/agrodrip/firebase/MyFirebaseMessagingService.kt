package com.agrodrip.firebase

import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.agrodrip.activity.startupScreen.SplashScreenActivity
import com.agrodrip.utils.Constants
import com.agrodrip.utils.Pref
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Log.d(TAG, "onNewToken: $s")
        Pref.setTokenValue(this, Constants.PREF_FIREBASE_TOKEN, s)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.from)
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            val data = remoteMessage.data
            handleData(data)
//            handleData(data.)
        } else if (remoteMessage.notification != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body)
            handleNotification(remoteMessage.notification)
        }
    }

    private fun handleNotification(RemoteMsgNotification: RemoteMessage.Notification?) {
        val message = RemoteMsgNotification!!.body
        val title = RemoteMsgNotification.title
        val notificationVO = NotificationVO()
        notificationVO.title = title
        notificationVO.message = message
        val resultIntent = Intent(applicationContext, SplashScreenActivity::class.java)
        val notificationUtils = NotificationUtils(applicationContext)
        notificationUtils.displayNotification(notificationVO, resultIntent)
        notificationUtils.playNotificationSound()
    }

    private fun handleData(data: Map<String, String>) {
        val title = data[TITLE]
        val iconUrl = data[IMAGE]
        val message = data[MESSAGE]
        val notificationVO = NotificationVO()
        notificationVO.title = title
        notificationVO.message = message
        notificationVO.iconUrl = iconUrl

        val intent = Intent(Intent.ACTION_POWER_CONNECTED)
        applicationContext.sendBroadcast(intent)

        val resultIntent = Intent(applicationContext, SplashScreenActivity::class.java)
        resultIntent.putExtra("message", message)
        sendMessageToActivity(message)
        Log.d("mes", "handleData: $message")
        val notificationUtils = NotificationUtils(applicationContext)
        notificationUtils.displayNotification(notificationVO, resultIntent)
        notificationUtils.playNotificationSound()
    }

    private fun sendMessageToActivity(msg: String?) {
        val intent = Intent("GPSLocationUpdates")
        intent.putExtra("Status", msg)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }

    companion object {
        private const val TAG = "MyFirebaseMsgingService"
        private const val TITLE = "title"
        private const val EMPTY = ""
        private const val MESSAGE = "body"
        private const val IMAGE = "image"
        private const val ACTION = "action"
        private const val DATA = "data"
        private const val ACTION_DESTINATION = "action_destination"
    }
}
