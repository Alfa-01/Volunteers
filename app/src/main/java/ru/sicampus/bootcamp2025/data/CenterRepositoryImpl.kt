package ru.sicampus.bootcamp2025.data

import ru.sicampus.bootcamp2025.data.dtos.CenterDto
import ru.sicampus.bootcamp2025.data.dtos.CenterListPagingDto
import ru.sicampus.bootcamp2025.data.dtos.FullCenterDto
import ru.sicampus.bootcamp2025.data.sources.locale.CredentialsLocalDataSource
import ru.sicampus.bootcamp2025.data.sources.network.CenterNetworkDataSource
import ru.sicampus.bootcamp2025.domain.entities.CenterEntity
import ru.sicampus.bootcamp2025.domain.entities.CenterMapEntity
import ru.sicampus.bootcamp2025.domain.entities.FullCenterEntity
import ru.sicampus.bootcamp2025.domain.repositories.CenterRepository

class CenterRepositoryImpl(
    private val networkDataSource: CenterNetworkDataSource,
    private val credentialsLocalDataSource: CredentialsLocalDataSource,
) : CenterRepository {
    override suspend fun getPaginatedCenters(
        pageNum: Int,
        pageSize: Int
    ): Result<List<CenterEntity>> {
        return mapPaged(
            networkDataSource.getPaginatedCenters(
                credentialsLocalDataSource.getToken(),
                pageNum,
                pageSize
            )
        )
    }

    override suspend fun getCenterById(centerId: Int): Result<FullCenterEntity> {
        return mapFull(
            networkDataSource.getCenterById(
                token = credentialsLocalDataSource.getToken(), centerId
            )
        )
    }

    private fun mapPaged(listCenterDto: Result<CenterListPagingDto>): Result<List<CenterEntity>> {
        return listCenterDto.map { listPagingDto ->
            listPagingDto.centerList?.mapNotNull { dto ->
                CenterEntity(
                    id = dto.id ?: return@mapNotNull null,
                    name = dto.name ?: return@mapNotNull null,
                    phone = dto.phone ?: return@mapNotNull null,
                    address = dto.address ?: return@mapNotNull null,
                    imageUrl = dto.imageUrl ?: return@mapNotNull null,
                    tag = dto.tag ?: return@mapNotNull null,
                    latitude = dto.latitude ?: return@mapNotNull null,
                    longitude = dto.longitude ?: return@mapNotNull null,
                )
            } ?: return Result.failure(IllegalStateException("Page data cannot be null"))
        }
    }

    private fun map(listCenterDto: Result<List<CenterDto>>?): Result<List<CenterMapEntity>> {
        return listCenterDto?.map { listDto ->
            listDto.mapNotNull { dto ->
                CenterMapEntity(
                    id = dto.id ?: return@mapNotNull null,
                    name = dto.name ?: return@mapNotNull null,
                    latitude = dto.latitude ?: return@mapNotNull null,
                    longitude = dto.longitude ?: return@mapNotNull null
                )
            }
        } ?: return Result.failure(IllegalStateException("Page data cannot be null"))
    }

    private fun mapFull(fullCenterDto: Result<FullCenterDto>): Result<FullCenterEntity> {
        return fullCenterDto.map { dto ->
            FullCenterEntity(
                name = dto.name ?: return Result.failure(IllegalStateException("Null data")),
                id = dto.id ?: return Result.failure(IllegalStateException("Null data")),
                address = dto.address ?: return Result.failure(IllegalStateException("Null data")),
                phone = dto.phone ?: return Result.failure(IllegalStateException("Null data")),
                email = dto.email ?: return Result.failure(IllegalStateException("Null data")),
                tags = dto.tags,
                imageUrl = dto.imageUrl
                    ?: return Result.failure(IllegalStateException("Null data")),
                type = dto.type ?: "",
                description = dto.description
                    ?: return Result.failure(IllegalStateException("Null data")),
                link = dto.link ?: return Result.failure(IllegalStateException("Null data")),
                active = dto.active
            )
        }
    }

    override suspend fun getCenters(): Result<List<CenterMapEntity>> {
        return map(networkDataSource.getCenters(credentialsLocalDataSource.getToken()))
    }

    override suspend fun pushVolunteer(centerId: Int, profileId: Int): Result<Unit> {
        return networkDataSource.pushVolunteer(centerId, profileId, credentialsLocalDataSource.getToken())
    }
}