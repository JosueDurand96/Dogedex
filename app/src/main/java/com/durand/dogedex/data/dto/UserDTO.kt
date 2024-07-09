package com.durand.dogedex.data.dto

import com.squareup.moshi.Json

class UserDTO(
    val id: Int,
    val email: String,
    @field:Json(name = "authentication_token")  val authenticationToken: String
)