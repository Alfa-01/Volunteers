package ru.sicampus.bootcamp2025.data.sources.locale

import android.content.SharedPreferences
import okhttp3.Credentials
import ru.sicampus.bootcamp2025.Const.TOKEN_KEY

class CredentialsLocalDataSource private constructor(private val preferences: SharedPreferences) {
    companion object {

        @Volatile
        private var INSTANCE: CredentialsLocalDataSource? = null

        fun getInstance(): CredentialsLocalDataSource {
            return INSTANCE!!
        }

        fun buildSource(sharedPreferences: SharedPreferences) {
            INSTANCE = CredentialsLocalDataSource(sharedPreferences)
        }
    }

    private var savedToken: String? = preferences.getString(TOKEN_KEY, null)

    fun updateToken(login: String, password: String): String {
        val updatedToken = Credentials.basic(login, password)
        savedToken = updatedToken
        cacheData()
        return updatedToken
    }

    private fun cacheData() {
        with(preferences.edit()) {
            putString(TOKEN_KEY, savedToken)
            apply()
        }
    }

    fun getToken(): String {
        return savedToken!!
    }

    fun getTokenForAuth(): Result<String> {
        return if (savedToken != null) Result.success(savedToken!!) else
            Result.failure(IllegalStateException("User was not authorized"))
    }

    fun clear() {
        preferences.edit().clear().apply()
        savedToken = null
    }
}