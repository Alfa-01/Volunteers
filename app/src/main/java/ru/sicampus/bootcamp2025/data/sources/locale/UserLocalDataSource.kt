package ru.sicampus.bootcamp2025.data.sources.locale

import ru.sicampus.bootcamp2025.domain.entities.UserEntity

object UserLocalDataSource {

    private var currentUser: UserEntity? = null

    fun cacheData(user: UserEntity) {
        currentUser = user
    }

    fun getUser(): UserEntity? {
        return currentUser
    }

    fun clear() {
        currentUser = null
    }
}