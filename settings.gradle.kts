plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("org.jetbrains.kotlinx.kover.aggregation") version "0.9.8"
}

rootProject.name = "Some"

include(":core")
include(":android")

kover {
    reports {
        verify {
            rule {
            }
        }
    }
}
