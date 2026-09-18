plugins {
    alias(libs.plugins.detekt)
    alias(libs.plugins.dokka)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.mavenPublish)
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
}

dependencies {
    implementation(projects.core)
    implementation(libs.kotlin.reflect)

    testImplementation(libs.kotlin.test)

    detektPlugins(libs.detekt.formatting)
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()

    coordinates(artifactId = "some-kotlin-fixture")

    pom {
        name.set("Some KotlinFixture Compatibility")
        description.set("KotlinFixture compatibility module for Some, a Kotlin test data generation library.")
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

detekt { autoCorrect = true }
