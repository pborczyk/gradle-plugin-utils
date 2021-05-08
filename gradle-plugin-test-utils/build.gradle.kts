plugins {
    kotlin("jvm")
    `java-library`
    `maven-publish`
    id("org.jetbrains.dokka")
}


dependencies {
    compileOnly(gradleApi())

    compileOnly("org.junit.jupiter:junit-jupiter-api:5.7.0")
    compileOnly("org.spekframework.spek2:spek-dsl-jvm:2.0.9")
    compileOnly("com.willowtreeapps.assertk:assertk-jvm:0.22")
}
