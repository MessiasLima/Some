plugins {
    kotlin("jvm") version "2.3.0"
}

group = "dev.appoutlet"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}
