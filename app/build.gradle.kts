
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

val nasaKey = providers
    .environmentVariable("NASA_API_KEY")
    .orElse("DEMO_KEY")
    .get()

android {
    namespace = "com.astrosphere.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.astrosphere.app"
        minSdk = 23
        targetSdk = 35

        versionCode = 2
        versionName = "2.1.0"

        buildConfigField(
            "String",
            "NASA_API_KEY",
            "\"${nasaKey.replace("\\", "\\\\").replace("\"", "\\\"")}\""
        )
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2025.08.00"))

    implementation("androidx.activity:activity-compose:1.10.1")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    implementation("io.coil-kt:coil-compose:2.7.0")

    debugImplementation("androidx.compose.ui:ui-tooling")
}
