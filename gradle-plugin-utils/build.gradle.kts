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
    "testImplementation"("io.kotest:kotest-runner-junit5:4.2.6")
    "testImplementation"("io.kotest:kotest-property:4.2.6")
    "testImplementation"("com.willowtreeapps.assertk:assertk-jvm:0.22")
    "testImplementation"("io.mockk:mockk:1.10.5")
}
