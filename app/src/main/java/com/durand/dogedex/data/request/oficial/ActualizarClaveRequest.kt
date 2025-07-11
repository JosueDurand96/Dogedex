package com.durand.dogedex.data.request.oficial

import com.google.gson.annotations.SerializedName

data class ActualizarClaveRequest(
    @SerializedName("correoUsuario")
    var correoUsuario: String,
    @SerializedName("clave")
    var clave: String,
)