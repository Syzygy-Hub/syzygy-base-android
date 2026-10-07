package com.syzygyhub.base.di

import android.content.Context
import com.syzygyhub.core.configuration.ConfigRegistry
import com.syzygyhub.core.di.Container
import com.syzygyhub.core.di.Lifetime
import com.syzygyhub.core.di.register
import com.syzygyhub.core.eventbus.EventBus
import com.syzygyhub.core.featureflags.InMemoryFeatureFlagProvider
import com.syzygyhub.core.lifecycle.AppLifecycleTracker
import com.syzygyhub.core.logging.ConsoleLogDestination
import com.syzygyhub.core.logging.Logger
import com.syzygyhub.core.navigation.Router
import com.syzygyhub.core.scheduling.CoroutineScheduler
import com.syzygyhub.core.state.StateStore
import com.syzygyhub.foundation.contracts.auth.AuthProvider
import com.syzygyhub.foundation.contracts.logging.LogLevel
import com.syzygyhub.foundation.contracts.network.NetworkClientProtocol
import com.syzygyhub.foundation.contracts.storage.StorageProvider
import com.syzygy.services.auth.JWTAuthProvider
import com.syzygy.services.networking.OkHttpNetworkClient
import com.syzygy.services.persistence.EncryptedStorageProvider
import com.syzygyhub.base.features.auth.data.AuthRepository
import com.syzygyhub.base.features.auth.domain.AuthUseCase
import com.syzygyhub.base.features.auth.presentation.LoginViewModel
import com.syzygyhub.base.network.TokenRefreshNetworkClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking

/**
 * Wires all 5 Syzygy layers using Core's [Container].
 *
 * Registers singletons for:
 *  - Logger (Core)
 *  - NetworkClientProtocol → OkHttpNetworkClient (Services)
 *  - AuthProvider → JWTAuthProvider (Services)
 *  - StorageProvider → EncryptedStorageProvider (Services)
 *  - StateStore (Core)
 *  - EventBus (Core)
 *  - Router (Core)
 *  - CoroutineScheduler / Scheduler (Core)
 *  - InMemoryFeatureFlagProvider / FeatureFlagProvider (Core)
 *  - ConfigRegistry (Core)
 *  - AppLifecycleTracker (Core)
 *
 * Instantiate once from [com.syzygyhub.base.SyzygyBaseApplication] and reach it via
 * [com.syzygyhub.base.SyzygyBaseApplication.appModule].
 */
class AppModule(private val applicationContext: Context) {

    /** Root application scope for long-lived coroutines. */
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    /** The wired Core DI container. Call [setup] before resolving dependencies. */
    val container: Container = Container()

    /**
     * Registers all dependencies in the container.
     * Must be called once from [com.syzygyhub.base.SyzygyBaseApplication.onCreate].
     */
    fun setup() {
        // ── Foundation / Core ──────────────────────────────────────────────

        container.register<Logger>(Lifetime.SINGLETON) { _ ->
            Logger().apply {
                addDestination(ConsoleLogDestination(), LogLevel.DEBUG)
            }
        }

        container.register<EventBus>(Lifetime.SINGLETON) { _ ->
            EventBus()
        }

        container.register<Router>(Lifetime.SINGLETON) { _ ->
            Router()
        }

        container.register<CoroutineScheduler>(Lifetime.SINGLETON) { _ ->
            CoroutineScheduler(appScope)
        }

        container.register<InMemoryFeatureFlagProvider>(Lifetime.SINGLETON) { _ ->
            InMemoryFeatureFlagProvider()
        }

        container.register<ConfigRegistry>(Lifetime.SINGLETON) { _ ->
            ConfigRegistry()
        }

        container.register<AppLifecycleTracker>(Lifetime.SINGLETON) { _ ->
            AppLifecycleTracker()
        }

        // ── Services ───────────────────────────────────────────────────────

        container.register<EncryptedStorageProvider>(Lifetime.SINGLETON) { _ ->
            EncryptedStorageProvider()
        }

        container.register<StorageProvider>(Lifetime.SINGLETON) { c ->
            c.resolve(EncryptedStorageProvider::class)
        }

        container.register<OkHttpNetworkClient>(Lifetime.SINGLETON) { _ ->
            OkHttpNetworkClient()
        }

        container.register<JWTAuthProvider>(Lifetime.SINGLETON) { c ->
            JWTAuthProvider(
                storage = c.resolve(EncryptedStorageProvider::class),
                networkClient = c.resolve(OkHttpNetworkClient::class),
            )
        }

        container.register<AuthProvider>(Lifetime.SINGLETON) { c ->
            c.resolve(JWTAuthProvider::class)
        }

        // ── 401 refresh interceptor ────────────────────────────────────────
        // Wraps OkHttpNetworkClient; on HTTP 401 refreshes the JWT and retries once.
        container.register<TokenRefreshNetworkClient>(Lifetime.SINGLETON) { c ->
            TokenRefreshNetworkClient(
                delegate = c.resolve(OkHttpNetworkClient::class),
                authProvider = c.resolve(JWTAuthProvider::class),
            )
        }

        container.register<NetworkClientProtocol>(Lifetime.SINGLETON) { c ->
            c.resolve(TokenRefreshNetworkClient::class)
        }

        // ── StateStore (register once you define AppState and AppAction) ─────────────
        // StateStore<State, Action> is generic — define your own types first.
        //
        // Example:
        //   data class AppState(val isLoggedIn: Boolean = false, val user: User? = null)
        //   sealed class AppAction { data class Login(val user: User) : AppAction(); object Logout : AppAction() }
        //   val appReducer: Reducer<AppState, AppAction> = { state, action ->
        //       when (action) {
        //           is AppAction.Login  -> state.copy(isLoggedIn = true, user = action.user)
        //           is AppAction.Logout -> state.copy(isLoggedIn = false, user = null)
        //       }
        //   }
        //
        //   container.register<StateStore<AppState, AppAction>>(Lifetime.SINGLETON) { _ ->
        //       StateStore(initial = AppState(), reducer = appReducer)
        //   }
        //
        // See syzygy-core-android for the full StateStore API.

        // ── AI Layer registrations (add after syzygy-ai-android is available on JitPack) ──
        // implementation("com.github.Syzygy-Hub:syzygy-ai-android:3.0.0")
        //
        // container.register<LLMProvider>(Lifetime.SINGLETON) { _ ->
        //     DefaultLLMProvider()   // replace with concrete AI layer impl name from syzygy-ai-android
        // }
        // container.register<AgentProtocol>(Lifetime.SINGLETON) { c ->
        //     DefaultAgent(llm = c.resolve(LLMProvider::class))
        // }
        // container.register<EmbeddingProvider>(Lifetime.SINGLETON) { _ ->
        //     DefaultEmbeddingProvider()
        // }
        // container.register<RAGProvider>(Lifetime.SINGLETON) { c ->
        //     DefaultRAGProvider(embeddings = c.resolve(EmbeddingProvider::class))
        // }
        // container.register<MemoryManager>(Lifetime.SINGLETON) { _ ->
        //     DefaultMemoryManager()
        // }
        // container.register<NamespacedMemoryManager>(Lifetime.SINGLETON) { c ->
        //     NamespacedMemoryManager(base = c.resolve(MemoryManager::class))
        // }
    }

    /**
     * Factory for [LoginViewModel]. Called from [MainActivity].
     *
     * Uses [runBlocking] to resolve singletons from the container on the main thread.
     * In production you would use a ViewModel factory that suspends properly.
     */
    fun provideLoginViewModel(): LoginViewModel = runBlocking {
        val authProvider = container.resolve(JWTAuthProvider::class)
        // AuthApi is Retrofit-based; a real implementation would resolve it via OkHttpNetworkClient/Retrofit.
        // For now we supply a no-op stub so the app compiles and the DI wiring is demonstrable.
        val authRepository = AuthRepository(NoOpAuthApi, authProvider)
        val authUseCase = AuthUseCase(authRepository)
        LoginViewModel(authUseCase)
    }
}

/** Stub Retrofit API used until a real back-end URL is configured. */
private object NoOpAuthApi : com.syzygyhub.base.features.auth.data.AuthApi {
    override suspend fun login(email: String, password: String) =
        throw UnsupportedOperationException("Configure a real AuthApi via Retrofit to enable login")
}
