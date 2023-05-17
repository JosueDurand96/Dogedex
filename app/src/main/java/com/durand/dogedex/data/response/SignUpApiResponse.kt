package com.durand.dogedex.data.response

import com.durand.dogedex.data.response.UserResponse
import com.squareup.moshi.Json

class SignUpApiResponse(
    val message: String,
    @field:Json(name = "is_success") val isSuccess: Boolean,
    val data: UserResponse
)