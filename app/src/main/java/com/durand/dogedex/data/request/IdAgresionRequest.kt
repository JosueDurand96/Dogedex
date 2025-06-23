package com.durand.dogedex.data.request


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class IdAgresionRequest(
    @SerializedName("idAgresion")
    var idAgresion: Int
): Serializable