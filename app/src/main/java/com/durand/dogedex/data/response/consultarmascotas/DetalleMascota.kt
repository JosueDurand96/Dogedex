@file:Suppress("SpellCheckingInspection")

package com.durand.dogedex.data.response.consultarmascotas


import com.squareup.moshi.Json

data class DetalleMascota(
    @field:Json(name = "caracter")
    val caracter: String,
    @field:Json(name = "color")
    val color: String,
    @field:Json(name = "distrito")
    val distrito: String,
    @field:Json(name = "especie")
    val especie: String,
    @field:Json(name = "estado")
    val estado: String,
    @field:Json(name = "esterilizado")
    val esterilizado: String,
    @field:Json(name = "fechaHoraCreacion")
    val fechaHoraCreacion: String,
    @field:Json(name = "fechaHoraModificacion")
    val fechaHoraModificacion: String,
    @field:Json(name = "fechaNacimiento")
    val fechaNacimiento: String,
    @field:Json(name = "genero")
    val genero: String,
    @field:Json(name = "idFoto")
    val idFoto: Int,
    @field:Json(name = "idMascota")
    val idMascota: Long,
    @field:Json(name = "idRaza")
    val idRaza: Int,
    @field:Json(name = "idUsuario")
    val idUsuario: Int,
    @field:Json(name = "modoObtencion")
    val modoObtencion: String,
    @field:Json(name = "nombre")
    val nombre: String,
    @field:Json(name = "pelaje")
    val pelaje: String,
    @field:Json(name = "razonTenencia")
    val razonTenencia: String,
    @field:Json(name = "tamano")
    val tamano: String,
    @field:Json(name = "foto")
    val foto: String
) {
    override fun toString(): String {
        return nombre
    }
}