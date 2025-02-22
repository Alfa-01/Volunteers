package ru.sicampus.bootcamp2025.data.sources.network

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.sicampus.bootcamp2025.Const
import ru.sicampus.bootcamp2025.data.dtos.CredentialsDto
import ru.sicampus.bootcamp2025.data.dtos.UserDto

object UserNetworkDataSource {

    suspend fun isUserExist(login: String): Result<Boolean> = withContext(Dispatchers.IO) {
        runCatching {
            val result = Network.client.get("${Const.DOMAIN}/api/users/username/$login")
            result.status == HttpStatusCode.OK
        }
    }

    suspend fun login(token: String): Result<UserDto> =
        withContext(Dispatchers.IO) {
            runCatching {
                val result = Network.client.get("${Const.DOMAIN}/api/users/login") {
                    headers {
                        append(HttpHeaders.Authorization, token)
                    }
                }
                if (result.status != HttpStatusCode.OK)
                    error("Status ${result.status}")
                result.body()
            }
        }

    suspend fun register(
        login: String,
        password: String,
        name: String,
        lastname: String
    ): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val result = Network.client.post("${Const.DOMAIN}/api/users/register") {
                    headers {
                        contentType(ContentType.Application.Json)
                        setBody(
                            CredentialsDto(
                                login = login,
                                password = password,
                                name = name,
                                lastname = lastname
                            )
                        )
                    }
                }
                if (result.status != HttpStatusCode.OK)
                    error("Status ${result.status}")
                Unit
            }
        }

}
