package ru.sicampus.bootcamp2025.domain.entities

data class CenterEntity (
    val id: Int,
    val name: String,
    val address: String,
    val phone: String,
    val latitude: Double,
    val longitude: Double,
    val tag: String,
    val imageUrl: String,
)