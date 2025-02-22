package ru.sicampus.bootcamp2025.domain.usecases

import ru.sicampus.bootcamp2025.domain.repositories.UserRepository

class AuthorizeUseCase(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(token: String) : Result<Unit> = userRepository.authorize(token)
}