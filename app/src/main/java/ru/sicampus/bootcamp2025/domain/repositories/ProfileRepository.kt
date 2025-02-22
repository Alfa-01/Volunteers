package ru.sicampus.bootcamp2025.domain.repositories

import ru.sicampus.bootcamp2025.domain.entities.ProfileEntity

interface ProfileRepository {

    suspend fun getProfileById(profileId: Int): Result<ProfileEntity>
    suspend fun updateProfile(newProfile: ProfileEntity): Result<Unit>
    suspend fun getAllProfiles(pageNum: Int, pageSize: Int): Result<List<ProfileEntity>>
    suspend fun getAllFreeProfiles(): Result<List<ProfileEntity>>
}