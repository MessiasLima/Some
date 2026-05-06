import kotlinx.kover.gradle.plugin.dsl.CoverageUnit

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.detekt)
    alias(libs.plugins.dokka)
    alias(libs.plugins.gitHooks)
    alias(libs.plugins.kover)
    alias(libs.plugins.mavenPublish)
}

group = "dev.appoutlet"
version = "0.1.3"

repositories {
    mavenCentral()
}

dependencies {
    detektPlugins(libs.detekt.formatting)

    dokkaHtmlPlugin(libs.dokka.versioning)

    implementation(libs.kotlin.reflect)

    testImplementation(libs.kotlin.test)
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
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

kover {
    reports {
        verify {
            rule {
                minBound(90, CoverageUnit.INSTRUCTION)
                minBound(90, CoverageUnit.LINE)
                minBound(67, CoverageUnit.BRANCH)
            }
        }
    }
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()

    coordinates(artifactId = "some")

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

dokka {
    dokkaPublications {
        html {
            outputDirectory.set(layout.projectDirectory.dir("docs/reference/version/${project.version}"))
            includes.from("$projectDir/docs/module.md")
        }
    }

    pluginsConfiguration {
        html {
            customAssets.from("$projectDir/docs/logo-icon.svg")
            footerMessage.set("© AppOutlet, Lda")
        }

        versioning {
            olderVersionsDir.set(layout.projectDirectory.dir("docs/reference/version"))
        }
    }
}

tasks.register("generateDokkaRedirect") {
    val version = project.version
    val targetDir = project.file("docs/reference/latest")

    doLast {
        targetDir.mkdirs() // Creates the dir if it doesn't exist, does nothing if it does

        File(targetDir, "index.html").writeText(
            """<meta http-equiv="refresh" content="0; url=../version/$version/">"""
        )
    }
}

tasks.named("dokkaGenerate") {
    finalizedBy("generateDokkaRedirect")
}