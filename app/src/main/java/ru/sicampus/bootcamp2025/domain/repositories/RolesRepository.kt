package ru.sicampus.bootcamp2025.domain.repositories

interface RolesRepository {
    suspend fun isRoleHaveAdminPermissions(roleId: Int): Result<Boolean>
}