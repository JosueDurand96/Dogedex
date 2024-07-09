package com.durand.dogedex.data.response

import com.squareup.moshi.Json

data class LoginMasterResponse(
    @field:Json(name = "idUsuario")
    var idUsuario: Int,
    @field:Json(name = "usuario")
    var usuario: String,
    @field:Json(name = "contrasena")
    var contrasena: String,
    @field:Json(name = "estadoUsuario")
    var estadoUsuario: String,
    @field:Json(name = "nombre")
    var nombre: String,
    @field:Json(name = "apellidos")
    var apellidos: String,
    @field:Json(name = "correoElectronico")
    var correoElectronico: String,
    @field:Json(name = "numeroMovil")
    var numeroMovil: String,
    @field:Json(name = "tipodocumento")
    var tipodocumento: String,
    @field:Json(name = "numeroDocumento")
    var numeroDocumento: String,
    @field:Json(name = "distrito")
    var distrito: String,
    @field:Json(name = "direccion")
    var direccion: String,
    @field:Json(name = "tipoVivienda")
    var tipoVivienda: String,
    @field:Json(name = "tratamientoDatos")
    var tratamientoDatos: String,
    @field:Json(name = "fechaHoraCreacion")
    var fechaHoraCreacion: String,
    @field:Json(name = "fechaHoraModificacion")
    var fechaHoraModificacion: String,
    @field:Json(name = "tipoUsuario")
    var tipoUsuario: String
)