package ru.sicampus.bootcamp2025.domain.usecases

import ru.sicampus.bootcamp2025.domain.repositories.UserRepository

class IsUserExistUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(login: String): Result<Boolean> {
        return userRepository.isUserExist(login)
    }
}