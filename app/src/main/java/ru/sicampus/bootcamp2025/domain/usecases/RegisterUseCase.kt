package ru.sicampus.bootcamp2025.domain.usecases

import ru.sicampus.bootcamp2025.domain.repositories.UserRepository

class RegisterUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(login: String, password: String, name: String, lastname: String): Result<Unit> {
        userRepository.register(login, password, name, lastname)
        return LoginUseCase(userRepository).invoke(login, password)
    }
}