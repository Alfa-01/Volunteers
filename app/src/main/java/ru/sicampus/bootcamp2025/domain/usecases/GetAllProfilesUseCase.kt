package ru.sicampus.bootcamp2025.domain.usecases

import ru.sicampus.bootcamp2025.domain.entities.ProfileEntity
import ru.sicampus.bootcamp2025.domain.repositories.ProfileRepository

class GetAllProfilesUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(pageNum: Int, pageSize: Int): Result<List<ProfileEntity>> =
        repository.getAllProfiles(pageNum, pageSize)
}