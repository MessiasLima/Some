plugins {
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.mavenPublish) apply false
    alias(libs.plugins.detekt) apply false

    alias(libs.plugins.dokka)
    alias(libs.plugins.gitHooks)
}

group = "dev.appoutlet"
version = "0.4.0"

repositories {
    mavenCentral()
}

dependencies {
    dokkaHtmlPlugin(libs.dokka.versioning)
    dokka(projects.android)
    dokka(projects.core)
    dokka(projects.kotest)
}

tasks.named("prepareKotlinBuildScriptModel") {
    dependsOn(":installGitHooks")
}

dokka {
    dokkaPublications.html {
        outputDirectory.set(rootDir.resolve("docs/reference/version/${project.version}"))
        includes.from("$rootDir/docs/reference/index.md")
    }

    pluginsConfiguration {
        html {
            customAssets.from("$rootDir/docs/logo-icon.svg")
            footerMessage.set("© AppOutlet, Lda")
        }

        versioning {
            olderVersionsDir.set(rootDir.resolve("docs/reference/version"))
        }
    }
}

tasks.register("generateDokkaRedirect") {
    description = "Generate a html file that redirects to the latest documentation version"

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
