plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.detekt)
    alias(libs.plugins.dokka)
    alias(libs.plugins.mavenPublish)
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

    testImplementation(libs.androidx.compose.ui)
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.test)

    detektPlugins(libs.detekt.formatting)
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()

    coordinates(artifactId = "some-android")

    pom {
        name.set("Some Android")
        description.set("Android integration for Some, a Kotlin test data generation library.")
        inceptionYear.set("2026")
        url.set("https://github.com/MessiasLima/Some")

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("repo")
            }
        }

        developers {
            developer {
                id.set("MessiasLima")
                name.set("Messias Lima")
                url.set("https://github.com/MessiasLima")
            }
        }

        scm {
            url.set("https://github.com/MessiasLima/Some")
            connection.set("scm:git:git://github.com/MessiasLima/Some.git")
            developerConnection.set("scm:git:ssh://git@github.com/MessiasLima/Some.git")
        }
    }
}
