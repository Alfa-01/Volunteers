package ru.sicampus.bootcamp2025.domain.usecases

import ru.sicampus.bootcamp2025.domain.repositories.RolesRepository

class IsRoleHaveAdminPermissionsUseCase(
    private val repository: RolesRepository
) {

    suspend operator fun invoke(roleId: Int): Result<Boolean> =
        repository.isRoleHaveAdminPermissions(roleId)
}