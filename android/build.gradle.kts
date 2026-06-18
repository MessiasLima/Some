plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.detekt)
    alias(libs.plugins.dokka)
}

group = rootProject.group
version = rootProject.version

android {
    namespace = "dev.appoutlet.some.android"

    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

detekt { autoCorrect = true }

dependencies {
    implementation(projects.core)

    testImplementation(libs.junit)

    detektPlugins(libs.detekt.formatting)
}