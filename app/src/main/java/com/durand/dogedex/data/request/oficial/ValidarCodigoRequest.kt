package com.durand.dogedex.data.request.oficial

import com.google.gson.annotations.SerializedName

data class ValidarCodigoRequest(
    @SerializedName("correoUsuario")
    var correoUsuario: String,
    @SerializedName("codigo")
    var codigo: String,
)