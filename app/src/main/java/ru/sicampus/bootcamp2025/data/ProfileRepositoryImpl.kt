package ru.sicampus.bootcamp2025.data

import ru.sicampus.bootcamp2025.data.dtos.ProfileDto
import ru.sicampus.bootcamp2025.data.sources.locale.CredentialsLocalDataSource
import ru.sicampus.bootcamp2025.data.sources.network.ProfileNetworkDataSource
import ru.sicampus.bootcamp2025.domain.entities.ProfileEntity
import ru.sicampus.bootcamp2025.domain.repositories.ProfileRepository


class ProfileRepositoryImpl(
    private val networkDataSource: ProfileNetworkDataSource,
    private val credentialsLocalDataSource: CredentialsLocalDataSource
) : ProfileRepository {

    override suspend fun getProfileById(profileId: Int): Result<ProfileEntity> {
        val networkAnswer =
            networkDataSource.getProfileById(
                profileId,
                credentialsLocalDataSource.getToken()
            )
        return map(networkAnswer)
    }


    private fun map(profileDto: Result<ProfileDto>): Result<ProfileEntity> {
        return profileDto.map { dto ->
            ProfileEntity(
                id = dto.id ?: return Result.failure(IllegalStateException("Null data")),
                name = dto.name ?: "",
                lastname = dto.lastname ?: "",
                photoUrl = dto.photoUrl,
                phoneNumber = dto.phoneNumber,
                email = dto.email,
                centerId = if (dto.centerId != null && dto.centerId > 0) dto.centerId else null
            )
        }
    }

    override suspend fun getAllProfiles(pageNum: Int, pageSize: Int): Result<List<ProfileEntity>> {
        return networkDataSource.getAllProfiles(
            pageNum,
            pageSize,
            credentialsLocalDataSource.getToken()
        ).map { pageDto ->
            pageDto.centerList?.mapNotNull { dto ->
                ProfileEntity(
                    id = dto.id ?: return@mapNotNull null,
                    centerId = dto.centerId,
                    name = dto.name ?: return@mapNotNull null,
                    lastname = dto.lastname ?: return@mapNotNull null,
                    photoUrl = dto.photoUrl,
                    phoneNumber = dto.phoneNumber,
                    email = dto.email
                )
            } ?: error("Null users list exception")
        }
    }


    override suspend fun getAllFreeProfiles(): Result<List<ProfileEntity>> {
        return networkDataSource.getAllFreeProfiles(
            credentialsLocalDataSource.getToken()
        ).map { listDto ->
            listDto.mapNotNull { dto ->
                ProfileEntity(
                    id = dto.id ?: return@mapNotNull null,
                    centerId = dto.centerId,
                    name = dto.name ?: return@mapNotNull null,
                    lastname = dto.lastname ?: return@mapNotNull null,
                    photoUrl = dto.photoUrl,
                    phoneNumber = dto.phoneNumber,
                    email = dto.email
                )
            }
        }
    }

    override suspend fun updateProfile(newProfile: ProfileEntity): Result<Unit> {
        return networkDataSource.updateProfile(
            ProfileDto(
                id = newProfile.id,
                centerId = newProfile.centerId,
                name = newProfile.name,
                lastname = newProfile.lastname,
                photoUrl = newProfile.photoUrl,
                phoneNumber = newProfile.phoneNumber,
                email = newProfile.email
            ), credentialsLocalDataSource.getToken()
        )
    }
}