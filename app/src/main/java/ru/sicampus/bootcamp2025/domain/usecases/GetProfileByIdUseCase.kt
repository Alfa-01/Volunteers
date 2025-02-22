package ru.sicampus.bootcamp2025.domain.usecases

import ru.sicampus.bootcamp2025.domain.repositories.ProfileRepository

class GetProfileByIdUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(profileId: Int) = repository.getProfileById(profileId)
}