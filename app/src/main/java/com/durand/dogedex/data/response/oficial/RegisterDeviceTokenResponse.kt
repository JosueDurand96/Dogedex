package com.durand.dogedex.data.response.oficial

import com.google.gson.annotations.SerializedName

data class RegisterDeviceTokenResponse(
    @SerializedName("codigo")
    val codigo: String,
    @SerializedName("mensaje")
    val mensaje: String
)



