package org.unbrokendome.gradle.pluginutils

import assertk.assertThat
import assertk.assertions.isInstanceOf
import assertk.assertions.isNull
import io.kotest.core.spec.style.DescribeSpec
import org.gradle.api.Project
import org.gradle.api.plugins.Convention
import org.gradle.testfixtures.ProjectBuilder


class ConventionPluginByTypeTest : DescribeSpec({

    lateinit var project: Project


    beforeEach {
        project = ProjectBuilder.builder()
            .build()

        // Install the convention plugin
        (project.extensions as Convention).plugins["example"] = ExampleConventionImpl()
    }


    describe("conventionPluginByType on fixed container") {

        it("with explicit type") {
            val conventionPlugin by project.conventionPluginByType(ExampleConvention::class)

            assertThat(conventionPlugin, "conventionPlugin").isInstanceOf(ExampleConvention::class)
        }


        it("with type from property") {
            val conventionPlugin: ExampleConvention by project.conventionPluginByType()

            assertThat(conventionPlugin, "conventionPlugin").isInstanceOf(ExampleConvention::class)
        }


        it("should allow nullable property") {
            val conventionPlugin: NonExistentConvention? by project.extensionByType()

            assertThat(conventionPlugin, "conventionPlugin").isNull()
        }
    }


    describe("conventionPluginByType on receiver") {

        it("with explicit type") {
            assertThat(project.exampleWithType, "conventionPlugin").isInstanceOf(ExampleConvention::class)
        }


        it("with type from property") {
            assertThat(project.example, "conventionPlugin").isInstanceOf(ExampleConvention::class)
        }


        it("should allow nullable property") {
            assertThat(project.nonExistent, "conventionPlugin").isNull()
        }
    }
}) {

    interface ExampleConvention
    interface NonExistentConvention

    class ExampleConventionImpl : ExampleConvention
}


// Extension properties cannot be defined locally, so we must define them here
private val Project.exampleWithType by conventionPluginByType(ConventionPluginByTypeTest.ExampleConvention::class)
private val Project.example: ConventionPluginByTypeTest.ExampleConvention by conventionPluginByType()
private val Project.nonExistent: ConventionPluginByTypeTest.NonExistentConvention? by conventionPluginByType()
