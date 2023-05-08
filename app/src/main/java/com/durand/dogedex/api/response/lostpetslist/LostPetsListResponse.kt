package com.durand.dogedex.api.response.lostpetslist


import com.squareup.moshi.Json

data class LostPetsListResponse(
    @Json(name = "lista")
    val lostPetDetailResponse: List<LostPetDetailResponse>
)