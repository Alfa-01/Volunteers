package ru.sicampus.bootcamp2025.data.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class UserDto(
    @SerialName("id")
    val userId: Int?,
    @SerialName("authorityId")
    val roleId: Int?,
    @SerialName("profileId")
    val profileId: Int?
)