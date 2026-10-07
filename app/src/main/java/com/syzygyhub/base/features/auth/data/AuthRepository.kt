package com.syzygyhub.base.features.auth.data

import com.syzygy.services.auth.JWTAuthProvider
import com.syzygyhub.foundation.contracts.auth.AuthState
import com.syzygyhub.foundation.contracts.auth.AuthToken
import com.syzygyhub.base.features.auth.domain.AuthenticatedUser
import com.syzygyhub.base.features.auth.domain.AuthRepositoryProtocol
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/** Network DTO — intentionally separate from the [AuthenticatedUser] domain model. */
data class LoginResponseDto(
    val userId: String,
    val email: String,
    val displayName: String?,
    val accessToken: String,
    val refreshToken: String,
)

/** Retrofit contract for the remote auth endpoint. */
interface AuthApi {
    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(@Field("email") email: String, @Field("password") password: String): LoginResponseDto
}

/**
 * Data-layer implementation of auth: talks to [AuthApi] for network calls and [JWTAuthProvider]
 * for persisting tokens/session, and exposes only domain models upward.
 */
class AuthRepository(
    private val authApi: AuthApi,
    private val authProvider: JWTAuthProvider,
) : AuthRepositoryProtocol {

    override suspend fun login(email: String, password: String): AuthenticatedUser =
        withContext(Dispatchers.IO) {
            val response = authApi.login(email, password)
            authProvider.authenticate(
                AuthToken(
                    accessToken = response.accessToken,
                    refreshToken = response.refreshToken,
                )
            )
            AuthenticatedUser(
                id = response.userId,
                email = response.email,
                displayName = response.displayName,
            )
        }

    override suspend fun logout() = withContext(Dispatchers.IO) {
        authProvider.signOut()
    }

    override suspend fun currentUser(): AuthenticatedUser? = withContext(Dispatchers.IO) {
        val state = authProvider.state.value
        if (state is AuthState.Authenticated) {
            // We only have the token here; a real implementation would decode user info
            // from the JWT or fetch it from the profile endpoint.
            null
        } else {
            null
        }
    }

    override suspend fun isLoggedIn(): Boolean = withContext(Dispatchers.IO) {
        authProvider.state.value is AuthState.Authenticated
    }
}
