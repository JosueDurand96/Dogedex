package com.durand.dogedex.data.request.oficial

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("correo")
    var correo: String,
    @SerializedName("clave")
    var clave: String,
)