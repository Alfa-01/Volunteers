package ru.sicampus.bootcamp2025.data.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CenterListPagingDto(
    @SerialName("content")
    val centerList: List<CenterDto>?
)
