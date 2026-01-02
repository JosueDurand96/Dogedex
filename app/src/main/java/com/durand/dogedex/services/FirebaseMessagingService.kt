package com.durand.dogedex.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.durand.dogedex.MyApplication
import com.durand.dogedex.R
import com.durand.dogedex.ui.user_fragment.UserHome
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        // Guardar el token para registrarlo después del login
        saveTokenToPreferences(token)
        
        // Intentar actualizar el token en el servidor si hay un usuario logueado
        val sharedPref = applicationContext.getSharedPreferences("idUsuario", Context.MODE_PRIVATE)
        val idUsuario = sharedPref.getLong("idUsuario", -1)
        if (idUsuario != -1L) {
            // Hay un usuario logueado, actualizar el token con su idUsuario
            com.durand.dogedex.util.FCMTokenManager.updateTokenWithUserId(applicationContext, idUsuario)
        } else {
            // No hay usuario logueado, solo registrar el token sin idUsuario
            com.durand.dogedex.util.FCMTokenManager.getAndRegisterToken(applicationContext)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Si el mensaje tiene notificación, usarla directamente
        remoteMessage.notification?.let { notification ->
            Log.d(TAG, "Message Notification - Title: ${notification.title}, Body: ${notification.body}")
            showNotification(
                title = notification.title ?: "Can perdido",
                message = notification.body ?: "",
                data = remoteMessage.data
            )
            return // Ya mostramos la notificación, salir
        }

        // Si solo tiene datos (app en primer plano), manejar los datos
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            handleDataMessage(remoteMessage.data)
        }
    }

    private fun handleDataMessage(data: Map<String, String>) {
        val type = data["type"]
        val nombre = data["nombre"] ?: ""
        val distrito = data["distrito"] ?: ""
        val lugarPerdida = data["lugarPerdida"] ?: ""
        
        when (type) {
            "lost_dog" -> {
                // Formato: "Distrito: [distrito]\nNombre del can: [nombre]"
                val mensaje = if (distrito.isNotEmpty()) {
                    "Distrito: $distrito\nNombre del can: $nombre"
                } else if (lugarPerdida.isNotEmpty()) {
                    "Lugar: $lugarPerdida\nNombre del can: $nombre"
                } else {
                    "Nombre del can: $nombre"
                }
                
                showNotification(
                    title = "Can perdido",
                    message = mensaje,
                    data = data
                )
            }
            else -> {
                showNotification(
                    title = data["title"] ?: "Notificación",
                    message = data["body"] ?: "",
                    data = data
                )
            }
        }
    }

    private fun showNotification(title: String, message: String, data: Map<String, String>) {
        val intent = Intent(this, UserHome::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Agregar datos extras si es necesario
            data.forEach { (key, value) ->
                putExtra(key, value)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "lost_dog_channel"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear canal de notificación para Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Canes Perdidos",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones sobre canes perdidos"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 250, 500)
            }
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    private fun saveTokenToPreferences(token: String) {
        val sharedPref = applicationContext.getSharedPreferences("fcm_token", Context.MODE_PRIVATE)
        sharedPref.edit().putString("fcm_token", token).apply()
        Log.d(TAG, "Token guardado en SharedPreferences")
    }

    companion object {
        private const val TAG = "FCMService"
        
        fun getStoredToken(context: Context): String? {
            val sharedPref = context.getSharedPreferences("fcm_token", Context.MODE_PRIVATE)
            return sharedPref.getString("fcm_token", null)
        }
    }
}

