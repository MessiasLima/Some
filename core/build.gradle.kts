plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.autoservice.ir)
    alias(libs.plugins.mavenPublish)
}

// Remove this setup temporarity
// group = "dev.appoutlet"
// version = "0.2.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.reflect)
    implementation(libs.kermit)

    testImplementation(libs.kotlin.test)
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

    coordinates(artifactId = "some-core")

    pom {
        name.set("Some")
        description.set("A Kotlin test data generation library that creates random instances of data classes, sealed classes/interfaces, collections, and primitive types.")
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
