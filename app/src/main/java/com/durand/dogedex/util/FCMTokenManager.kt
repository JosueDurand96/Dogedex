package com.durand.dogedex.util

import android.content.Context
import android.util.Log
import com.durand.dogedex.data.ApiResponseStatus
import com.durand.dogedex.data.repository.NewOficialRepository
import com.durand.dogedex.data.request.oficial.RegisterDeviceTokenRequest
import com.durand.dogedex.services.MyFirebaseMessagingService
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object FCMTokenManager {
    private const val TAG = "FCMTokenManager"
    private val repository = NewOficialRepository()

    /**
     * Obtiene el token FCM y lo registra en el servidor
     * @param context Contexto de la aplicación
     * @param idUsuario ID del usuario (opcional, se puede registrar después del login)
     */
    fun getAndRegisterToken(context: Context, idUsuario: Long? = null) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Obtener el token
            val token = task.result
            Log.d(TAG, "FCM Registration Token: $token")

            // Guardar el token localmente
            MyFirebaseMessagingService.getStoredToken(context)?.let { storedToken ->
                if (storedToken != token) {
                    // El token cambió, actualizar
                    registerTokenOnServer(token, idUsuario)
                }
            } ?: run {
                // No hay token guardado, registrar nuevo
                registerTokenOnServer(token, idUsuario)
            }
        }
    }

    /**
     * Registra el token en el servidor
     */
    private fun registerTokenOnServer(token: String, idUsuario: Long?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = RegisterDeviceTokenRequest(
                    token = token,
                    idUsuario = idUsuario,
                    plataforma = "android"
                )
                
                when (val response = repository.registrarDeviceToken(request)) {
                    is ApiResponseStatus.Success -> {
                        Log.d(TAG, "Token registrado exitosamente: ${response.data.mensaje}")
                    }
                    is ApiResponseStatus.Error -> {
                        Log.e(TAG, "Error al registrar token: ${response.message}")
                        Log.e(TAG, "Request enviado - Token: $token, idUsuario: $idUsuario, plataforma: android")
                    }
                    is ApiResponseStatus.Loading -> {
                        Log.d(TAG, "Registrando token...")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception al registrar token: ${e.message}", e)
            }
        }
    }

    /**
     * Actualiza el idUsuario asociado al token después del login
     */
    fun updateTokenWithUserId(context: Context, idUsuario: Long) {
        val token = MyFirebaseMessagingService.getStoredToken(context)
        token?.let {
            registerTokenOnServer(it, idUsuario)
        } ?: run {
            // Si no hay token, obtener uno nuevo
            getAndRegisterToken(context, idUsuario)
        }
    }
}


