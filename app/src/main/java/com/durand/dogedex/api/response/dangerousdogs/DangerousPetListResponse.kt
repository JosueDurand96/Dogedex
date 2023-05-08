@file:Suppress("SpellCheckingInspection")

package com.durand.dogedex.api.response.dangerousdogs


import com.squareup.moshi.Json

data class DangerousPetListResponse(
    @field:Json(name = "lista")
    val list: List<PetProfileResponse>
)