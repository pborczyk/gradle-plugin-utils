enableFeaturePreview("VERSION_CATALOGS")

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
        if (requested.id.toString() == "org.jetbrains.dokka") {
            useVersion(dokkaVersion)
        }
    }
}

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
