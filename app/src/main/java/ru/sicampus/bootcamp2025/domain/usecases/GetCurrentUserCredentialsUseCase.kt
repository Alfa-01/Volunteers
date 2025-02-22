package ru.sicampus.bootcamp2025.domain.usecases

import ru.sicampus.bootcamp2025.domain.repositories.UserRepository

class GetCurrentUserCredentialsUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<String> = userRepository.getLocalCredentials()
}