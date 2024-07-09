package com.durand.dogedex.data.response.can_perdido


import com.google.gson.annotations.SerializedName

data class ListCanPerdidoMasterResponse(
    @SerializedName("lista")
    var lista: List<ListCanPerdido>
)