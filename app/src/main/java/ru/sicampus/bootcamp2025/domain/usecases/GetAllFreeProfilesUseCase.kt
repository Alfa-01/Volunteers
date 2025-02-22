package ru.sicampus.bootcamp2025.domain.usecases

import ru.sicampus.bootcamp2025.domain.entities.ProfileEntity
import ru.sicampus.bootcamp2025.domain.repositories.ProfileRepository

class GetAllFreeProfilesUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(): Result<List<ProfileEntity>> =
        repository.getAllFreeProfiles()
}