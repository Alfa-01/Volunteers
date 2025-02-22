package ru.sicampus.bootcamp2025.data

import ru.sicampus.bootcamp2025.data.sources.locale.CredentialsLocalDataSource
import ru.sicampus.bootcamp2025.data.sources.locale.UserLocalDataSource
import ru.sicampus.bootcamp2025.data.sources.network.UserNetworkDataSource
import ru.sicampus.bootcamp2025.domain.entities.UserEntity
import ru.sicampus.bootcamp2025.domain.repositories.UserRepository

class UserRepositoryImpl(
    private val userLocalDataSource: UserLocalDataSource,
    private val credentialsLocalDataSource: CredentialsLocalDataSource,
    private val userNetworkDataSource: UserNetworkDataSource
) : UserRepository {
    override suspend fun isUserExist(login: String): Result<Boolean> {
        return userNetworkDataSource.isUserExist(login)
    }

    override suspend fun login(login: String, password: String): Result<Unit> {
        return authorize(credentialsLocalDataSource.updateToken(login, password)).onFailure {
            credentialsLocalDataSource.clear()
        }
    }

    override suspend fun register(login: String, password: String, name: String, lastname: String): Result<Unit> {
        return userNetworkDataSource.register(login, password, name, lastname)
    }

    override suspend fun getLocalCredentials(): Result<String> {
        return credentialsLocalDataSource.getTokenForAuth()
    }

    override suspend fun authorize(token: String): Result<Unit> {
        return runCatching {
            val response = userNetworkDataSource.login(token)
            val userDto = response.getOrNull()
            if (userDto != null) {
                userLocalDataSource.cacheData(
                    UserEntity(
                        userId = userDto.userId ?: error("UserId couldn't be null"),
                        profileId = userDto.profileId ?: error("ProfileId couldn't be null"),
                        roleId = userDto.roleId ?: error("RoleId couldn't be null")
                    )
                )
            }
            val exception = response.exceptionOrNull()
            if (exception != null)
                error(exception)
        }
    }

    override suspend fun getLocalUser(): UserEntity? {
        return userLocalDataSource.getUser()
    }

    override suspend fun logout() {
        userLocalDataSource.clear()
        credentialsLocalDataSource.clear()
    }
}