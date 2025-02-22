package ru.sicampus.bootcamp2025.data.sources.network

import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.sicampus.bootcamp2025.Const

object RoleNetworkDataSource {

    private val network = Network.client

    suspend fun isRoleHasAdminPermissions(roleId: Int, token: String): Result<Boolean> = withContext(Dispatchers.IO) {
        runCatching {
            val response = network.get("${Const.DOMAIN}/api/authority/$roleId") {
                headers {
                    append(HttpHeaders.Authorization, token)
                }
            }
            response.status == HttpStatusCode.OK
        }
    }
}