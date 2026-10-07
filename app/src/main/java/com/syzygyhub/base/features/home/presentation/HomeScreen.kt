package com.syzygyhub.base.features.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.syzygyhub.ui.android.theme.SyzygyThemeProvider
import com.syzygyhub.ui.android.tokens.Spacing

@Composable
fun HomeScreen(onLogout: () -> Unit = {}) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Welcome!",
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = "You're logged in.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = Spacing.xs),
            )
            Spacer(modifier = Modifier.height(Spacing.lg))
            Button(onClick = onLogout) {
                Text(text = "Log Out")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    SyzygyThemeProvider {
        HomeScreen()
    }
}
