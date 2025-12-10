package com.durand.dogedex.data.response.oficial

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RegisterCanPerdidoResponse(
    @SerializedName("codigo")
    val codigo: String,
    @SerializedName("mensaje")
    val mensaje: String,
): Serializable