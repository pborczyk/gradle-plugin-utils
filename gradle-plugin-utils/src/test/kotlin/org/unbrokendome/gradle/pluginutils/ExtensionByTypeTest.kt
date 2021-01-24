package org.unbrokendome.gradle.pluginutils

import assertk.assertThat
import assertk.assertions.isInstanceOf
import assertk.assertions.isNull
import io.kotest.core.spec.style.DescribeSpec
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder


class ExtensionByTypeTest : DescribeSpec({

    lateinit var project: Project


    beforeEach {
        project = ProjectBuilder.builder()
            .build()

        // Install the extension
        project.extensions.add(ExampleExtension::class.java, "example", ExampleExtensionImpl())
    }


    describe("extensionByType on fixed container") {

        it("with explicit type") {
            val extension by project.extensionByType(ExampleExtension::class)

            assertThat(extension, "extension").isInstanceOf(ExampleExtension::class)
        }


        it("with type from property") {
            val extension: ExampleExtension by project.extensionByType()

            assertThat(extension, "extension").isInstanceOf(ExampleExtension::class)
        }


        it("should allow nullable property") {
            val nonExistentExtension: NonExistentExtension? by project.extensionByType()

            assertThat(nonExistentExtension).isNull()
        }
    }


    describe("extensionByName on receiver") {

        it("with explicit type") {
            assertThat(project.exampleWithType, "extension").isInstanceOf(ExampleExtension::class)
        }


        it("with type from property") {
            assertThat(project.example, "extension").isInstanceOf(ExampleExtension::class)
        }


        it("should allow nullable property") {
            assertThat(project.nonExistent).isNull()
        }
    }
}) {

    interface ExampleExtension
    interface NonExistentExtension

    class ExampleExtensionImpl : ExampleExtension
}


// Extension properties cannot be defined locally, so we must define them here
private val Project.exampleWithType by extensionByType(ExtensionByTypeTest.ExampleExtension::class)
private val Project.example: ExtensionByTypeTest.ExampleExtension by extensionByType()
private val Project.nonExistent: ExtensionByTypeTest.NonExistentExtension? by extensionByType()
