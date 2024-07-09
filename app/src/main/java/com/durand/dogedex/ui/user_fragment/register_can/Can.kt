package com.durand.dogedex.ui.user_fragment.register_can

import android.app.Activity
import android.content.Context

class Can(
    val id: Int,
    val nombreMascota: String,
    val fechaNacimiento: String,
    val especie: String,
    val genero: String,
    val raza: String,
    val tamano: String,
    val caracter: String,
    val color: String,
    val pelaje: String,
    val esterelizado: String,
    val distrito: String,
    val modoObtencion: String,
    val razonTenencia: String,
    val latitud: String,
    val longitud: String,
    val photo: String,
) {
    companion object {
        private const val AUTH_PREFS = "can_auth"
        private const val ID = "id"
        private const val NOMBRE_MASCOTA = "nombreMascota"
        private const val FECHA_NACIMIENTO = "fechaNacimiento"
        private const val ESPECIE = "especie"
        private const val GENERO = "genero"
        private const val RAZA = "raza"
        private const val TAMANO = "tamano"
        private const val CARACTER = "caracter"

        private const val COLOR = "color"
        private const val PELAJE = "pelaje"
        private const val ESTERELIZADO = "esterelizado"
        private const val DISTRITO = "distrito"
        private const val MODO_OBTENCION = "modoObtencion"
        private const val RAZON_TENENCIA = "razonTenencia"
        private const val LATITUD = "latitud"
        private const val LONGITUD = "longitud"
        private const val PHOTO = "photo"

        fun setLoggedInUser(activity: Activity, can: Can) {

            activity.getSharedPreferences(AUTH_PREFS, Context.MODE_PRIVATE).also {
                it.edit()
                    .putInt(ID, can.id)
                    .putString(NOMBRE_MASCOTA, can.nombreMascota)
                    .putString(FECHA_NACIMIENTO, can.fechaNacimiento)
                    .putString(ESPECIE, can.especie)
                    .putString(GENERO, can.genero)
                    .putString(RAZA, can.raza)
                    .putString(TAMANO, can.tamano)
                    .putString(CARACTER, can.caracter)
                    .putString(COLOR, can.color)
                    .putString(PELAJE, can.pelaje)
                    .putString(ESTERELIZADO, can.esterelizado)
                    .putString(DISTRITO, can.distrito)
                    .putString(MODO_OBTENCION, can.modoObtencion)
                    .putString(RAZON_TENENCIA, can.razonTenencia)
                    .putString(LATITUD, can.latitud)
                    .putString(LONGITUD, can.longitud)
                    .putString(PHOTO, can.photo)
                    .apply()
            }
        }

        fun getLoggedInUser(activity: Activity): Can? {
            val prefs = activity.getSharedPreferences(AUTH_PREFS, Context.MODE_PRIVATE) ?: return null
            val userId = prefs.getInt(ID, 0)
            if (userId == 0) {
                return null
            }
            val user = Can(
                userId,
                prefs.getString(NOMBRE_MASCOTA, "") ?: "",
                prefs.getString(FECHA_NACIMIENTO, "") ?: "",
                prefs.getString(ESPECIE, "") ?: "",
                prefs.getString(GENERO, "") ?: "",
                prefs.getString(RAZA, "") ?: "",
                prefs.getString(TAMANO, "") ?: "",
                prefs.getString(CARACTER, "") ?: "",
                prefs.getString(COLOR, "") ?: "",
                prefs.getString(PELAJE, "") ?: "",
                prefs.getString(ESTERELIZADO, "") ?: "",
                prefs.getString(DISTRITO, "") ?: "",
                prefs.getString(MODO_OBTENCION, "") ?: "",
                prefs.getString(RAZON_TENENCIA, "") ?: "",
                prefs.getString(LATITUD, "") ?: "",
                prefs.getString(LONGITUD, "") ?: "",
                prefs.getString(PHOTO, "") ?: ""
            )
            return user
        }

        fun logout(activity: Activity) {
            activity.getSharedPreferences(AUTH_PREFS, Context.MODE_PRIVATE).also {
                it.edit().clear().apply()
            }
        }
    }
}