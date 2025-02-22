package ru.sicampus.bootcamp2025.data

import ru.sicampus.bootcamp2025.data.sources.locale.CredentialsLocalDataSource
import ru.sicampus.bootcamp2025.data.sources.network.RoleNetworkDataSource
import ru.sicampus.bootcamp2025.domain.repositories.RolesRepository

class RolesRepositoryImpl(
    private val networkSource: RoleNetworkDataSource,
    private val credentialsLocalDataSource: CredentialsLocalDataSource
) : RolesRepository {
    override suspend fun isRoleHaveAdminPermissions(roleId: Int): Result<Boolean> {
        return networkSource.isRoleHasAdminPermissions(
            roleId,
            credentialsLocalDataSource.getToken()
        )
    }
}