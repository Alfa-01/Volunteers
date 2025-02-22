package ru.sicampus.bootcamp2025.domain.usecases

import ru.sicampus.bootcamp2025.domain.entities.FullCenterEntity
import ru.sicampus.bootcamp2025.domain.repositories.CenterRepository

class GetCenterByIdUseCase(
    private val centerRepository: CenterRepository
) {

    suspend operator fun invoke(centerId: Int): Result<FullCenterEntity> = centerRepository.getCenterById(centerId)
}