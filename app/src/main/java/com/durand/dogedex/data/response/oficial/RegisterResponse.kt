package com.durand.dogedex.data.response.oficial

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("codigo")
    var codigo: String,
    @SerializedName("mensaje")
    var mensaje: String
)