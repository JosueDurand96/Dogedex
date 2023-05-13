package com.durand.dogedex.api.response.list_mascotas

import com.squareup.moshi.Json


data class ListaMascotas(
    @field:Json(name = "caracter")
    var caracter: String,
    @field:Json(name = "color")
    var color: String,
    @field:Json(name = "distrito")
    var distrito: String,
    @field:Json(name = "especie")
    var especie: String,
    @field:Json(name = "estado")
    var estado: String,
    @field:Json(name = "esterilizado")
    var esterilizado: String,
    @field:Json(name = "fechaHoraCreacion")
    var fechaHoraCreacion: String,
    @field:Json(name = "fechaHoraModificacion")
    var fechaHoraModificacion: String,
    @field:Json(name = "fechaNacimiento")
    var fechaNacimiento: String,
    @field:Json(name = "genero")
    var genero: String,
    @field:Json(name = "idFoto")
    var idFoto: Int,
    @field:Json(name = "idMascota")
    var idMascota: Int,
    @field:Json(name = "idRaza")
    var idRaza: Int,
    @field:Json(name = "idUsuario")
    var idUsuario: Int,
    @field:Json(name = "modoObtencion")
    var modoObtencion: String,
    @field:Json(name = "nombre")
    var nombre: String,
    @field:Json(name = "pelaje")
    var pelaje: String,
    @field:Json(name = "razonTenencia")
    var razonTenencia: String,
    @field:Json(name = "tamano")
    var tamano: String
)