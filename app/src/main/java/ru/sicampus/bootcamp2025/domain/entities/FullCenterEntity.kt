package ru.sicampus.bootcamp2025.domain.entities

data class FullCenterEntity(
    val id: Int,
    val type: String?,
    val name: String,
    val description: String,
    val address: String,
    val phone: String,
    val email: String,
    val link: String,
    val tags: List<String>?,
    val imageUrl: String,
    val active: List<Int>?
)
