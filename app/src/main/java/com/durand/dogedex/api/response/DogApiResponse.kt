package com.durand.dogedex.api.response

import com.durand.dogedex.api.dto.DogDTO
import com.squareup.moshi.Json

class DogApiResponse(
    val message: String,
    @field:Json(name = "is_success") val isSuccess: Boolean,
    val data: DogDTO
)