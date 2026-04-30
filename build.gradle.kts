plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.detekt)
    alias(libs.plugins.gitHooks)
    alias(libs.plugins.kover)
}

group = "dev.appoutlet"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.reflect)
    testImplementation(libs.kotlin.test)
    detektPlugins(libs.detekt.formatting)
}

kotlin {
    jvmToolchain(21)
}

detekt {
    autoCorrect = true
    parallel = true
    buildUponDefaultConfig = true
    config.setFrom("$rootDir/detekt/detekt.yml")
    source.setFrom("src/main/kotlin", "src/test/kotlin")
}

tasks.named("prepareKotlinBuildScriptModel") {
    dependsOn(":installGitHooks")
}

tasks.test {
    useJUnitPlatform()
}

kover {
    reports {
        verify {
            rule {
                minBound(90)
            }
        }
    }
}
