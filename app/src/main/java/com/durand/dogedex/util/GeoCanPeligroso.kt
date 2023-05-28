package com.durand.dogedex.util

import android.app.Activity
import android.content.Context
class GeoCanPeligroso(
    val idPerdido: Int,
    val idMascota: Int,
    val latitud: String,
    val longitud: String,
    val fechaPerdida: String,
    val descripcion: String,
    val estado: String,
){
    companion object {
        private const val AUTH_PREFS = "geo_auth"
        private const val ID_CAN_LOST = "idPerdido"
        private const val ID_CAN = "idPerdido"
        private const val LAT = "latitud"
        private const val LNG = "longitud"
        private const val DATE_LOST_CAN = "fechaPerdida"
        private const val DESCRIPTION = "descripcion"
        private const val STATUS = "estado"

        fun setLoggedInUser(activity: Activity, user: GeoCanPeligroso) {

            activity.getSharedPreferences(AUTH_PREFS, Context.MODE_PRIVATE).also {
                it.edit()
                    .putInt(ID_CAN_LOST, user.idPerdido)
                    .putInt(ID_CAN, user.idMascota)
                    .putString(LAT, user.latitud)
                    .putString(LNG, user.longitud)
                    .putString(DATE_LOST_CAN, user.fechaPerdida)
                    .putString(DESCRIPTION, user.descripcion)
                    .putString(STATUS, user.estado)
                    .apply()
            }
        }

//        fun getLoggedInUser(activity: Activity): List<GeoCanPeligroso>? {
//            val prefs =
//                activity.getSharedPreferences(AUTH_PREFS, Context.MODE_PRIVATE) ?: return null
//            val userId = prefs.getInt(ID_CAN_LOST, 0)
//            if (userId == 0){
//                return null
//            }
//            val user = GeoCanPeligroso(
//                userId,
//                prefs.getString(LAT, "") ?: "",
//                prefs.getString(LNG, "") ?: ""
//            )
//            return user
//        }
        fun logout(activity: Activity){
            activity.getSharedPreferences(AUTH_PREFS, Context.MODE_PRIVATE).also {
                it.edit().clear().apply()
            }
        }
    }
}