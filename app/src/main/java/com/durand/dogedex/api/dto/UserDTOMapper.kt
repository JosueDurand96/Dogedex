package com.durand.dogedex.api.dto

import com.durand.dogedex.api.User

class UserDTOMapper {
    fun fromUserDTOToUserDomain(userDTO: UserDTO): User =
        User(userDTO.id, userDTO.email, userDTO.authenticationToken)

}