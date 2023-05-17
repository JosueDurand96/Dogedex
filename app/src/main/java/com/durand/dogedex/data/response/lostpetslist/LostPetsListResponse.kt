@file:Suppress("SpellCheckingInspection")

package com.durand.dogedex.data.response.lostpetslist


import com.durand.dogedex.data.response.lostpetslist.LostPetDetailResponse
import com.squareup.moshi.Json

data class LostPetsListResponse(
    @field:Json(name = "lista")
    val lostPetDetailResponse: List<LostPetDetailResponse>
)