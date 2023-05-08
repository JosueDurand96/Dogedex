@file:Suppress("SpellCheckingInspection")

package com.durand.dogedex.api.response.lostpetslist


import com.squareup.moshi.Json

data class LostPetsListResponse(
    @field:Json(name = "lista")
    val lostPetDetailResponse: List<LostPetDetailResponse>
)