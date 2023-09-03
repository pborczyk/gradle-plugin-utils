pluginManagement {

    repositories {
        gradlePluginPortal()
        mavenCentral()
    }

    val dokkaVersion: String by settings

    resolutionStrategy.eachPlugin {
        if (requested.id.namespace == "org.jetbrains.kotlin" ||
                requested.id.namespace.orEmpty().startsWith("org.jetbrains.kotlin.")) {
            useVersion(embeddedKotlinVersion)
        }
    }

    plugins {
        id("org.jetbrains.dokka") version dokkaVersion apply false
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "gradle-plugin-utils-parent"

include(
    "bom",
    "gradle-plugin-integration-test-utils",
    "gradle-plugin-test-utils",
    "gradle-plugin-utils"
)
