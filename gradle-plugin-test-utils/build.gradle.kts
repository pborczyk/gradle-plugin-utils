plugins {
    kotlin("jvm")
    `java-library`
    `maven-publish`
    id("org.jetbrains.dokka")
}


dependencies {
    compileOnly(gradleApi())

    compileOnly(libs.junit.api)
    compileOnly(libs.spek.dsl)
    compileOnly(libs.assertk.core)
}
