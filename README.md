[![Android](https://img.shields.io/badge/Android-Kotlin-7F77DD?style=flat)](https://developer.android.com/) [![Kotlin](https://img.shields.io/badge/Kotlin-2.0-1D9E75?logo=kotlin&logoColor=white&style=flat)](https://kotlinlang.org) [![CI](https://img.shields.io/github/actions/workflow/status/Syzygy-Hub/syzygy-base-android/ci.yml?label=ci&style=flat)](https://github.com/Syzygy-Hub/syzygy-base-android/actions/workflows/ci.yml) [![Version](https://img.shields.io/badge/version-3.0.0-D85A30?style=flat)](https://github.com/Syzygy-Hub/syzygy-base-android/releases) [![License](https://img.shields.io/badge/License-MIT-green?style=flat)](LICENSE)

<picture>
  <source media="(prefers-color-scheme: dark)" srcset="https://raw.githubusercontent.com/Syzygy-Hub/.github/main/brand/assets/banners/syzygy-banner-dark-1200.png">
  <img src="https://raw.githubusercontent.com/Syzygy-Hub/.github/main/brand/assets/banners/syzygy-banner-light-1200.png" alt="Syzygy" width="600">
</picture>

# syzygy-base-android

Template Android app (Kotlin + Jetpack Compose) that wires all 5 Syzygy layers via the Core DI Container.

## About

syzygy-base-android is a ready-to-clone Android template that wires Foundation, Core, Services, AI, and UI — the 5 Syzygy layers — through the Core DI Container from the moment the app starts. Clone the repo, run `setup.sh` with your app name and package, open in Android Studio, and all five layers resolve automatically from JitPack.

## Platforms

| Platform | Minimum | Build | Status |
|---|---|---|---|
| Android | 8.0+ (API 26) | Gradle / JitPack | ✅ Supported |

## Requirements

- Android Studio Meerkat or later
- Kotlin 2.0+
- Android API 26+
- Java 17+

## Installation

1. Clone the repository:
   ```
   git clone https://github.com/Syzygy-Hub/syzygy-base-android.git
   ```
2. Rename the project to your app:
   ```
   ./setup.sh YourAppName com.your.package
   ```
3. Open the project in Android Studio.
4. Sync Gradle — JitPack is already configured, and all 5 layers resolve automatically.

## Architecture

The app depends on all 5 Syzygy layers, each pulled from JitPack at version 3.0.0:

| Layer | Coordinate |
|---|---|
| Foundation | `com.github.Syzygy-Hub:syzygy-foundation-android:3.0.0` |
| Core | `com.github.Syzygy-Hub:syzygy-core-android:3.0.0` |
| Services | `com.github.Syzygy-Hub:syzygy-services-android:3.0.0` |
| AI | `com.github.Syzygy-Hub:syzygy-ai-android:3.0.0` |
| UI | `com.github.Syzygy-Hub:syzygy-ui-android:3.0.0` |

DI wiring is in `AppModule.kt`. The application entry point is `SyzygyBaseApplication` / `MainActivity`. The Compose UI entry point is `LoginScreen`, wrapped in `SyzygyThemeProvider`.

## Contents

| Folder | Description |
|---|---|
| `core/` | Extension functions and network error types shared across the app |
| `di/` | `AppModule` — registers all 5 Syzygy layers in the Core DI Container |
| `features/` | Screen-scoped feature packages (auth, home, settings) |
| `navigation/` | Navigation wiring stub using Core's `Router` |
| `network/` | `TokenRefreshNetworkClient` — 401-retry interceptor wrapping `OkHttpNetworkClient` |
| `storage/` | Storage wiring stub using `EncryptedStorageProvider` |
| `theme/` | Theme wiring stub using `SyzygyThemeProvider` |
| `ui/` | Shared UI components |
| `utils/` | Project-specific Kotlin extension functions |

## Usage

Resolve a ViewModel from the DI container via the `AppModule` getter pattern:

```kotlin
// In MainActivity or a Composable that has access to the Application
val appModule = (application as SyzygyBaseApplication).appModule
val loginViewModel = remember { appModule.provideLoginViewModel() }
```

Wrap Compose content with `SyzygyThemeProvider` so all components receive the design-system theme:

```kotlin
SyzygyThemeProvider(theme = SyzygyTheme.default) {
    // your composable tree
}
```

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) if present, or open a pull request against `main`. All submissions are reviewed before merge.

## Releases

See [CHANGELOG.md](CHANGELOG.md) for a full release history. The current release is `3.0.0`.

## License

[MIT](LICENSE)
