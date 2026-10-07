package com.syzygyhub.base.network

import com.syzygy.services.auth.JWTAuthProvider
import com.syzygy.services.networking.NetworkError
import com.syzygyhub.foundation.contracts.network.NetworkClientProtocol
import com.syzygyhub.foundation.contracts.network.NetworkRequest
import com.syzygyhub.foundation.contracts.network.NetworkResponse
import com.syzygyhub.foundation.errors.SyzygyErrorCode

/**
 * Wraps a [NetworkClientProtocol] delegate with automatic token-refresh logic.
 *
 * On a 401 (unauthenticated) response:
 * 1. Calls [authProvider].refreshToken() to obtain a new access token.
 * 2. Retries the original request once with the refreshed token in the
 *    `Authorization: Bearer <token>` header.
 * 3. If the retry also returns 401, propagates the error without looping.
 *
 * The wrapped [delegate] is expected to throw [NetworkError] with
 * [SyzygyErrorCode.unauthenticated] on HTTP 401 responses, matching the
 * behaviour of [com.syzygy.services.networking.OkHttpNetworkClient].
 *
 * @param delegate   The underlying network client to delegate requests to.
 * @param authProvider The JWT auth provider used to refresh tokens.
 */
class TokenRefreshNetworkClient(
    private val delegate: NetworkClientProtocol,
    private val authProvider: JWTAuthProvider,
) : NetworkClientProtocol by delegate {
    override suspend fun execute(request: NetworkRequest): NetworkResponse {
        return try {
            delegate.execute(request)
        } catch (e: NetworkError) {
            if (e.code != SyzygyErrorCode.unauthenticated) throw e

            // Attempt to refresh the token; if refresh fails, propagate.
            val refreshed = authProvider.refreshToken()
            if (!refreshed) throw e

            // Retry with the new access token.
            val currentToken =
                (authProvider.state.value as? com.syzygyhub.foundation.contracts.auth.AuthState.Authenticated)
                    ?.token?.accessToken
                    ?: throw e

            val retryRequest =
                request.copy(
                    headers = request.headers + mapOf("Authorization" to "Bearer $currentToken"),
                )

            // On a second 401, propagate immediately (avoids infinite loop).
            delegate.execute(retryRequest)
        }
    }
}
