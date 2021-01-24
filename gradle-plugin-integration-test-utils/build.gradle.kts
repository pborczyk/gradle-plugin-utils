plugins {
    kotlin("jvm")
    `java-library`
    `maven-publish`
    id("com.jfrog.bintray")
    id("org.jetbrains.dokka")
}


dependencies {

    api(project(":gradle-plugin-test-utils"))

    compileOnly(gradleApi())
    compileOnly(gradleTestKit())

    compileOnly("org.junit.jupiter:junit-jupiter-api:5.7.0")
    compileOnly("org.spekframework.spek2:spek-dsl-jvm:2.0.9")
    compileOnly("com.willowtreeapps.assertk:assertk-jvm:0.22")
}
