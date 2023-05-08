package com.durand.dogedex.api.response.dangerousdogs


import com.squareup.moshi.Json

data class DangerousDogListResponse(
    @Json(name = "lista")
    val list: List<DogProfileResponse>
)