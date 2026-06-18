plugins {
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.jvm) apply false

    alias(libs.plugins.dokka)
    alias(libs.plugins.gitHooks)
}

group = "dev.appoutlet"
version = "0.2.1"

repositories {
    mavenCentral()
}

dependencies {
    dokkaHtmlPlugin(libs.dokka.versioning)
}


tasks.named("prepareKotlinBuildScriptModel") {
    dependsOn(":installGitHooks")
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
