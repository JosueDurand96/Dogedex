package com.durand.dogedex.data.Request


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class IdAgresionRequest(
    @SerializedName("idAgresion")
    var idAgresion: Int
): Serializable