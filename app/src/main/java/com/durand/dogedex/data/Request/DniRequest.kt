package com.durand.dogedex.data.Request


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DniRequest(
    @SerializedName("numeroDocumento")
    var numeroDocumento: String
): Serializable