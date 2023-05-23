package com.durand.dogedex.data.response.list_mascotas


import com.squareup.moshi.Json

data class ListMascotasMasterResponse(
    @field:Json(name = "lista")
    var listaMascotas: List<MascotaResponse>
)