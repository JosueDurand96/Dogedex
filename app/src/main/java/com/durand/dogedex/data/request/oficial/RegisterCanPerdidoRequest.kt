package com.durand.dogedex.data.request.oficial

import com.google.gson.annotations.SerializedName

data class RegisterCanPerdidoRequest(
    @SerializedName("fechaPerdida")
    var fechaPerdida: String,
    @SerializedName("lugarPerdida")
    var lugarPerdida: String,
    @SerializedName("comentario")
    var comentario: String,
)