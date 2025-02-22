package ru.sicampus.bootcamp2025.domain.usecases

import ru.sicampus.bootcamp2025.domain.repositories.CenterRepository

class LinkVolunteerUseCase(
    private val repository: CenterRepository
) {

    suspend operator fun invoke(centerId: Int, profileId: Int) = repository.pushVolunteer(centerId, profileId)
}