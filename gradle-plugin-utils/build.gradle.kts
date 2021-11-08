plugins {
    kotlin("jvm")
    `java-library`
    `maven-publish`
    id("org.jetbrains.dokka")
}


dependencies {
    compileOnly(gradleApi())

    "testImplementation"(gradleApi())
    "testImplementation"(project(":gradle-plugin-test-utils"))
    "testImplementation"(libs.kotest.runner)
    "testImplementation"(libs.kotest.property)
    "testImplementation"(libs.assertk.core)
    "testImplementation"(libs.mockk)
}
