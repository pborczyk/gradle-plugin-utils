plugins {
    kotlin("jvm")
    `java-library`
    `maven-publish`
    id("org.jetbrains.dokka")
}


dependencies {

    api(project(":gradle-plugin-test-utils"))

    compileOnly(gradleApi())
    compileOnly(gradleTestKit())

    compileOnly(libs.junit.api)
    compileOnly(libs.spek.dsl)
    compileOnly(libs.assertk.core)
}
