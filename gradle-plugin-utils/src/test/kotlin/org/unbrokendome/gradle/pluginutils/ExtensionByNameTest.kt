package org.unbrokendome.gradle.pluginutils

import assertk.assertThat
import assertk.assertions.isInstanceOf
import assertk.assertions.isNull
import io.kotest.core.spec.style.DescribeSpec
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder


class ExtensionByNameTest : DescribeSpec({

    val extensionName = "example"

    lateinit var project: Project


    beforeEach {
        project = ProjectBuilder.builder()
            .build()

        // Install the extension
        project.extensions.add(ExampleExtension::class.java, extensionName, ExampleExtensionImpl())
    }


    describe("extensionByName on fixed container") {

        it("should return extension") {
            val extension: ExampleExtension by project.extensionByName(extensionName)

            assertThat(extension, "extension").isInstanceOf(ExampleExtension::class)
        }


        it("should use the property name as the default extension name") {
            val example: ExampleExtension by project.extensionByName()

            assertThat(example, "extension").isInstanceOf(ExampleExtension::class)
        }


        it("should allow nullable property") {
            val nonExistentExtension: ExampleExtension? by project.extensionByName("nonExistent")

            assertThat(nonExistentExtension).isNull()
        }
    }


    describe("extensionByName on receiver") {

        it("should return extension") {
            assertThat(project.exampleAlias, "extension").isInstanceOf(ExampleExtension::class)
        }


        it("should use the property name as the default extension name") {
            assertThat(project.example, "extension").isInstanceOf(ExampleExtension::class)
        }


        it("should allow nullable property") {
            assertThat(project.optionalExample).isNull()
        }
    }
}) {

    interface ExampleExtension

    class ExampleExtensionImpl : ExampleExtension
}


// Extension properties cannot be defined locally, so we must define them here
private val Project.example: ExtensionByNameTest.ExampleExtension by extensionByName()
private val Project.exampleAlias: ExtensionByNameTest.ExampleExtension by extensionByName("example")
private val Project.optionalExample: ExtensionByNameTest.ExampleExtension? by extensionByName("nonExistent")
