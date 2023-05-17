@file:Suppress("SpellCheckingInspection")

package com.durand.dogedex.data.dto


import com.squareup.moshi.Json

data class AddUserDTO(
    @field:Json(name = "apellidos")
    val lastname: String,
    @field:Json(name = "contrasena")
    val password: String,
    @field:Json(name = "correoElectronico")
    val email: String,
    @field:Json(name = "direccion")
    val address: String,
    @field:Json(name = "distrito")
    val district: String,
    @field:Json(name = "estadoUsuario")
    val userState: String,
    @field:Json(name = "fechaHoraCreacion")
    val createdAt: String? = null,
    @field:Json(name = "fechaHoraModificacion")
    val updatedAt: String? = null,
    @field:Json(name = "idUsuario")
    val userId: Int? = null,
    @field:Json(name = "nombre")
    val name: String,
    @field:Json(name = "numeroDocumento")
    val dni: Long,
    @field:Json(name = "numeroMovil")
    val phone: String,
    @field:Json(name = "tipoVivienda")
    val typeHouse: String,
    @field:Json(name = "tipodocumento")
    val docType: String,
    @field:Json(name = "tratamientoDatos")
    val dataProcessing: String,
    @field:Json(name = "usuario")
    val user: String
)