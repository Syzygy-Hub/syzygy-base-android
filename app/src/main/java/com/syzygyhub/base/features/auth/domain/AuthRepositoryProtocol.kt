package com.syzygyhub.base.features.auth.domain

/**
 * Domain-layer contract for the auth repository.
 *
 * [AuthUseCase] depends only on this interface; the concrete implementation
 * ([com.syzygyhub.base.features.auth.data.AuthRepository]) lives in the data
 * layer and is injected at the DI boundary.
 */
interface AuthRepositoryProtocol {
    suspend fun login(
        email: String,
        password: String
    ): AuthenticatedUser

    suspend fun logout()

    suspend fun currentUser(): AuthenticatedUser?

    suspend fun isLoggedIn(): Boolean
}
