package ru.sicampus.bootcamp2025.domain.repositories

import ru.sicampus.bootcamp2025.domain.entities.CenterEntity
import ru.sicampus.bootcamp2025.domain.entities.CenterMapEntity
import ru.sicampus.bootcamp2025.domain.entities.FullCenterEntity

interface CenterRepository {
    suspend fun getPaginatedCenters(pageNum: Int, pageSize: Int): Result<List<CenterEntity>>
    suspend fun getCenterById(centerId: Int): Result<FullCenterEntity>
    suspend fun getCenters(): Result<List<CenterMapEntity>>
    suspend fun pushVolunteer(centerId: Int, profileId: Int): Result<Unit>
}