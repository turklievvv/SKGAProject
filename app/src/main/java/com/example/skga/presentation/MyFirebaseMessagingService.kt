package com.example.skga.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    // Этот метод автоматически срабатывает, когда на телефон ПРИЛЕТАЕТ пуш-уведомление
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM_CLIENT", "Пуш ДОЛЕТЕЛ до телефона! Данные: ${remoteMessage.data}")
        // Извлекаем текст, который нам прислал сервер
        val title =
            remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "Новое объявление"
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"]
        ?: "Проверьте обновления в SKGA"

        // Запускаем показ уведомления на шторке
        showNotificationOnScreen(title, body)
    }

    private fun showNotificationOnScreen(title: String, message: String) {
        val channelId = "skga_fcm_notifications"
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Для Android 8.0+ обязательно создавать "каналы" уведомлений
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Объявления вуза",
                NotificationManager.IMPORTANCE_HIGH // HIGH чтобы уведомление всплывало баннером сверху
            )
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Стандартная системная иконка (потом заменишь на лого SKGA)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true) // Чтобы исчезало при нажатии
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        // Показываем уведомление
        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        println("Новый FCM Токен устройства: $token")
    }
}