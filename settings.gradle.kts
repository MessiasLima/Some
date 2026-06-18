import kotlinx.kover.gradle.aggregation.settings.dsl.minBound
import kotlinx.kover.gradle.plugin.dsl.CoverageUnit

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("org.jetbrains.kotlinx.kover.aggregation") version "0.9.8"
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "Some"

include(":core")
include(":android")

kover {
    enableCoverage()
    reports {
        verify {
            rule {
                name = "Minimum coverage"
                minBound(95, CoverageUnit.LINE)
                minBound(95, CoverageUnit.INSTRUCTION)
                minBound(79, CoverageUnit.BRANCH)
            }
        }
    }
}
