package ru.sicampus.bootcamp2025.domain.usecases

import ru.sicampus.bootcamp2025.domain.entities.CenterMapEntity
import ru.sicampus.bootcamp2025.domain.repositories.CenterRepository

class GetCentersUseCase(
    private val centerRepository: CenterRepository
) {
    suspend operator fun invoke(): Result<List<CenterMapEntity>> = centerRepository.getCenters()
}