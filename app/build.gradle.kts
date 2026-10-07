plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ktlint)
}

android {
    namespace = "com.syzygyhub.base"
    compileSdk {
        version =
            release(37) {
                minorApiLevel = 1
            }
    }

    defaultConfig {
        applicationId = "com.syzygyhub.base"
        minSdk = 29
        targetSdk = 36
        versionCode = 300000
        versionName = "3.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "BASE_URL", "\"https://api.example.com/\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            optimization {
                enable = true
            }
            buildConfigField("String", "BASE_URL", "\"https://api.example.com/\"")
        }
        debug {
            buildConfigField("String", "BASE_URL", "\"https://staging-api.example.com/\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging.interceptor)

    implementation(libs.androidx.security.crypto)

    implementation("com.github.Syzygy-Hub:syzygy-foundation-android:3.0.0")
    implementation("com.github.Syzygy-Hub:syzygy-core-android:3.0.0")
    implementation("com.github.Syzygy-Hub:syzygy-services-android:3.0.0")
    implementation("com.github.Syzygy-Hub:syzygy-ai-android:3.0.0")
    implementation("com.github.Syzygy-Hub:syzygy-ui-android:3.0.0")

    testImplementation(libs.junit)
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.12.2")
    testImplementation("androidx.test:core:1.6.1")
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}

// ---------------------------------------------------------------------------
// ktlint — lint Kotlin sources via ktlint-cli
// ---------------------------------------------------------------------------

val ktlintCli: Configuration by configurations.creating

dependencies {
    ktlintCli("com.pinterest.ktlint:ktlint-cli:1.0.1")
}

val ktlintCheckSources by tasks.registering(JavaExec::class) {
    group = "verification"
    description = "Runs ktlint against src/main/java/**/*.kt and src/test/java/**/*.kt"
    classpath = ktlintCli
    mainClass.set("com.pinterest.ktlint.Main")
    args = listOf("src/main/java/**/*.kt", "src/test/java/**/*.kt")
    workingDir = project.projectDir
}

tasks.named("ktlintCheck") {
    dependsOn(ktlintCheckSources)
}

val ktlintFormatSources by tasks.registering(JavaExec::class) {
    group = "formatting"
    description = "Auto-fixes ktlint violations in src/**/*.kt"
    classpath = ktlintCli
    mainClass.set("com.pinterest.ktlint.Main")
    args = listOf("-F", "src/main/java/**/*.kt", "src/test/java/**/*.kt")
    workingDir = project.projectDir
}

tasks.named("ktlintFormat") {
    dependsOn(ktlintFormatSources)
}
