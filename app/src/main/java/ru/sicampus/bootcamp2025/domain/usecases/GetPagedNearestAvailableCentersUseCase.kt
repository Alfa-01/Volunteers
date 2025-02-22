package ru.sicampus.bootcamp2025.domain.usecases

import ru.sicampus.bootcamp2025.domain.entities.CenterEntity
import ru.sicampus.bootcamp2025.domain.repositories.CenterRepository

class GetPagedNearestAvailableCentersUseCase(
    private val repository: CenterRepository
) {
    suspend operator fun invoke(pageNum: Int, pageSize: Int) : Result<List<CenterEntity>> =
        repository.getPaginatedCenters(pageNum, pageSize)
}