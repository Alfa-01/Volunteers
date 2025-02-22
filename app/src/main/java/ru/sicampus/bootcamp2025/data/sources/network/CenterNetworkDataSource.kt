package ru.sicampus.bootcamp2025.data.sources.network

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.sicampus.bootcamp2025.Const
import ru.sicampus.bootcamp2025.data.dtos.CenterDto
import ru.sicampus.bootcamp2025.data.dtos.CenterListPagingDto
import ru.sicampus.bootcamp2025.data.dtos.FullCenterDto

object CenterNetworkDataSource {
    private val client = Network.client

    suspend fun getPaginatedCenters(
        token: String,
        pageNum: Int,
        pageSize: Int
    ): Result<CenterListPagingDto> =
        withContext(Dispatchers.IO) {
            runCatching {
                val result =
                    client.get("${Const.DOMAIN}/api/centers/paginated?page=$pageNum&size=$pageSize") {
                        headers {
                            append(HttpHeaders.Authorization, token)
                        }
                    }
                if (result.status != HttpStatusCode.OK) error("Status ${result.status}")
                result.body()
            }
        }

    suspend fun getCenterById(token: String, centerId: Int): Result<FullCenterDto> =
        withContext(Dispatchers.IO) {
            runCatching {
                val result = client.get("${Const.DOMAIN}/api/centers/${centerId}") {
                    headers {
                        append(HttpHeaders.Authorization, token)
                    }
                }
                if (result.status != HttpStatusCode.OK) error(("Status ${result.status}"))
                result.body()
            }
        }

    suspend fun getCenters(token: String): Result<List<CenterDto>> =
        withContext(Dispatchers.IO) {
            runCatching {
                val result = client.get("${Const.DOMAIN}/api/centers") {
                    headers {
                        append(HttpHeaders.Authorization, token)
                    }
                }
                if (result.status != HttpStatusCode.OK) error(("Status ${result.status}"))
                result.body()
            }
        }

    suspend fun pushVolunteer(centerId: Int, profileId: Int, token: String) = withContext(Dispatchers.IO) {
        runCatching {
            val result = client.post("${Const.DOMAIN}/api/centers/punish/$centerId/$profileId") {
                headers {
                    append(HttpHeaders.Authorization, token)
                }
            }
            if (result.status != HttpStatusCode.OK)
                error("Status ${result.status}")
            Unit
        }
    }
}