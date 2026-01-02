package com.durand.dogedex

import android.app.Application
import android.content.Context
import android.util.Log
import com.durand.dogedex.util.FCMTokenManager
import com.google.firebase.messaging.FirebaseMessaging

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this
        
        // Inicializar Firebase Messaging y obtener token
        initializeFirebaseMessaging()
    }

    private fun initializeFirebaseMessaging() {
        // Obtener token FCM (sin idUsuario aún, se actualizará después del login)
        FCMTokenManager.getAndRegisterToken(this)
        
        // Suscribirse a temas si es necesario
        FirebaseMessaging.getInstance().subscribeToTopic("lost_dogs")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("MyApplication", "Suscrito al tema 'lost_dogs'")
                } else {
                    Log.e("MyApplication", "Error al suscribirse al tema", task.exception)
                }
            }
    }

    companion object {
        lateinit var appContext: Context
            private set
    }
}