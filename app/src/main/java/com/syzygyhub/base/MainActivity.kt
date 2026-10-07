package com.syzygyhub.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.syzygyhub.base.features.auth.presentation.LoginScreen
import com.syzygyhub.base.features.home.presentation.HomeScreen
import com.syzygyhub.ui.android.theme.SyzygyTheme
import com.syzygyhub.ui.android.theme.SyzygyThemeProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appModule = (application as SyzygyBaseApplication).appModule

        setContent {
            SyzygyThemeProvider(theme = SyzygyTheme.default) {
                var isLoggedIn by remember { mutableStateOf(false) }
                val loginViewModel = remember { appModule.provideLoginViewModel() }
                val uiState by loginViewModel.uiState.collectAsState()

                // React to successful logout: return to the login screen
                LaunchedEffect(uiState.isLoggedOut) {
                    if (uiState.isLoggedOut) {
                        isLoggedIn = false
                        loginViewModel.consumeLogout()
                    }
                }

                if (isLoggedIn) {
                    HomeScreen(onLogout = { loginViewModel.logout() })
                } else {
                    LoginScreen(
                        viewModel = loginViewModel,
                        onLoginSuccess = { isLoggedIn = true },
                    )
                }
            }
        }
    }
}
