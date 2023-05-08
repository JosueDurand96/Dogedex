package com.durand.dogedex.api.response


import com.squareup.moshi.Json

data class AddUserResponse(
    @Json(name = "apellidos")
    val lastname: String,
    @Json(name = "contrasena")
    val password: String,
    @Json(name = "correoElectronico")
    val email: String,
    @Json(name = "direccion")
    val address: String,
    @Json(name = "distrito")
    val district: String,
    @Json(name = "estadoUsuario")
    val userState: String,
    @Json(name = "fechaHoraCreacion")
    val createdAt: String? = null,
    @Json(name = "fechaHoraModificacion")
    val updatedAt: String? = null,
    @Json(name = "idUsuario")
    val userId: Int? = null,
    @Json(name = "nombre")
    val name: String,
    @Json(name = "numeroDocumento")
    val dni: String,
    @Json(name = "numeroMovil")
    val phone: String,
    @Json(name = "tipoVivienda")
    val typeHouse: String,
    @Json(name = "tipodocumento")
    val docType: String,
    @Json(name = "tratamientoDatos")
    val dataProcessing: String,
    @Json(name = "usuario")
    val user: String
)