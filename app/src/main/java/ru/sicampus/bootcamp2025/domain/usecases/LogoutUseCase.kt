package ru.sicampus.bootcamp2025.domain.usecases

import ru.sicampus.bootcamp2025.domain.repositories.UserRepository

class LogoutUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke() = repository.logout()
}