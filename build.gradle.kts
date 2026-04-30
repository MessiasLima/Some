plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "dev.appoutlet"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.reflect)
    testImplementation(libs.kotlin.test)
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}
