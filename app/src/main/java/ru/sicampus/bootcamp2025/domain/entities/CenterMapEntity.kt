package ru.sicampus.bootcamp2025.domain.entities

data class CenterMapEntity(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
)