package ru.sicampus.bootcamp2025.data.dtos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FullCenterDto(
    @SerialName("id")
    val id: Int?,
    @SerialName("type")
    val type: String?,
    @SerialName("name")
    val name: String?,
    @SerialName("description")
    val description: String?,
    @SerialName("address")
    val address: String?,
    @SerialName("phone")
    val phone: String?,
    @SerialName("email")
    val email: String?,
    @SerialName("link")
    val link: String?,
    @SerialName("tags")
    val tags: List<String>?,
    @SerialName("imageUrl")
    val imageUrl: String?,
    @SerialName("volunteerIds")
    val active: List<Int>?
)
