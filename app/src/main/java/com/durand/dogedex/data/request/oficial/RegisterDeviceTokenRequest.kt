package com.durand.dogedex.data.request.oficial

import com.google.gson.annotations.SerializedName

data class RegisterDeviceTokenRequest(
    @SerializedName("token")
    val token: String,
    @SerializedName("idUsuario")
    val idUsuario: Long? = null,
    @SerializedName("plataforma")
    val plataforma: String = "android"
)


