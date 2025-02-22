package ru.sicampus.bootcamp2025.data.sources.network

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.sicampus.bootcamp2025.Const
import ru.sicampus.bootcamp2025.data.dtos.ProfileDto
import ru.sicampus.bootcamp2025.data.dtos.ProfileListPagingDto

object ProfileNetworkDataSource {
    private val client = Network.client

    suspend fun getProfileById(profileId: Int, token: String): Result<ProfileDto> = withContext(Dispatchers.IO) {
        runCatching {
            val result = client.get("${Const.DOMAIN}/api/profiles/${profileId}") {
                headers {
                    append(HttpHeaders.Authorization, token)
                }
            }
            if (result.status != HttpStatusCode.OK) error("Status ${result.status}")
            result.body()
        }
    }

    suspend fun updateProfile(newProfileDto: ProfileDto, token: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val result = client.put("${Const.DOMAIN}/api/profiles/${newProfileDto.id}") {
                contentType(ContentType.Application.Json)
                setBody(
                    newProfileDto
                )
                headers {
                    append(HttpHeaders.Authorization, token)
                }
            }

            if (result.status != HttpStatusCode.OK) error("Status ${result.status}")
            Unit
        }
    }

    suspend fun getAllProfiles(pageNum: Int, pageSize: Int, token: String): Result<ProfileListPagingDto> = withContext(Dispatchers.IO) {
        runCatching {
            val result = client.get("${Const.DOMAIN}/api/users/paginated?pageNum=$pageNum&pageSize=$pageSize") {
                headers {
                    append(HttpHeaders.Authorization, token)
                }
            }
            if (result.status != HttpStatusCode.OK)
                error("Status ${result.status}")
            result.body()
        }
    }

    suspend fun getAllFreeProfiles(token: String): Result<List<ProfileDto>> = withContext(Dispatchers.IO) {
        runCatching {
            val result = client.get("${Const.DOMAIN}/api/users/unoccupied") {
                headers {
                    append(HttpHeaders.Authorization, token)
                }
            }
            if (result.status != HttpStatusCode.OK)
                error("Status ${result.status}")
            result.body()
        }
    }
}