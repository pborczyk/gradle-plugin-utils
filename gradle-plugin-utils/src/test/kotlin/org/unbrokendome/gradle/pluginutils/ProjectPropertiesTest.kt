package org.unbrokendome.gradle.pluginutils

import assertk.assertThat
import assertk.assertions.*
import io.kotest.core.spec.style.DescribeSpec
import org.gradle.api.Project
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.testfixtures.ProjectBuilder
import java.io.File
import java.time.Duration


class ProjectPropertiesTest : DescribeSpec({

    lateinit var project: Project


    beforeEach {
        project = ProjectBuilder.builder()
            .build()
    }


    describe("providerFromProjectProperty") {

        it("should use the value from the project property") {

            val extra: ExtraPropertiesExtension = project.requiredExtension()
            extra.set("testProperty", "testValue")

            val provider = project.providerFromProjectProperty("testProperty")

            assertThat(provider.orNull)
                .isEqualTo("testValue")
        }

        it("should not have a value if project property is not set") {

            val provider = project.providerFromProjectProperty("testProperty")

            assertThat(provider.orNull)
                .isNull()
        }

        it("should use the default value if project property is not set") {

            val provider = project.providerFromProjectProperty("testProperty", "defaultValue")

            assertThat(provider.orNull)
                .isEqualTo("defaultValue")
        }

        it("should not use the default value if project property is set") {

            val extra: ExtraPropertiesExtension = project.requiredExtension()
            extra.set("testProperty", "testValue")

            val provider = project.providerFromProjectProperty("testProperty", "defaultValue")

            assertThat(provider.orNull)
                .isEqualTo("testValue")
        }

        it("should evaluate GString") {

            project.version = "1.2.3"

            val extra: ExtraPropertiesExtension = project.requiredExtension()
            extra.set("testProperty", "ver\${version}")

            val provider = project.providerFromProjectProperty("testProperty", evaluateGString = true)

            assertThat(provider.orNull)
                .isEqualTo("ver1.2.3")
        }
    }


    describe("booleanProviderFromProjectProperty") {

        it("should use the value from the project property") {

            val extra: ExtraPropertiesExtension = project.requiredExtension()
            extra.set("testProperty", true)

            val provider = project.booleanProviderFromProjectProperty("testProperty")

            assertThat(provider.orNull)
                .isNotNull().isTrue()
        }

        it("should use the value true if the project property is the string \"true\"") {

            val extra: ExtraPropertiesExtension = project.requiredExtension()
            extra.set("testProperty", "true")

            val provider = project.booleanProviderFromProjectProperty("testProperty")

            assertThat(provider.orNull)
                .isNotNull().isTrue()
        }

        it("should have value false if the project property is not \"true\"") {

            val extra: ExtraPropertiesExtension = project.requiredExtension()
            extra.set("testProperty", "someValue")

            val provider = project.booleanProviderFromProjectProperty("testProperty")

            assertThat(provider.orNull)
                .isNotNull().isFalse()
        }

        it("should not have a value if project property is not set") {

            val provider = project.booleanProviderFromProjectProperty("testProperty")

            assertThat(provider.orNull)
                .isNull()
        }

        it("should use the default value if project property is not set") {

            val provider = project.booleanProviderFromProjectProperty("testProperty", true)

            assertThat(provider.orNull)
                .isNotNull().isTrue()
        }

        it("should not use the default value if project property is set") {

            val extra: ExtraPropertiesExtension = project.requiredExtension()
            extra.set("testProperty", "false")

            val provider = project.booleanProviderFromProjectProperty("testProperty", true)

            assertThat(provider.orNull)
                .isNotNull().isFalse()
        }
    }


    describe("intProviderFromProjectProperty") {

        it("should use the value from the project property") {

            val extra: ExtraPropertiesExtension = project.requiredExtension()
            extra.set("testProperty", 42)

            val provider = project.intProviderFromProjectProperty("testProperty")

            assertThat(provider.orNull)
                .isEqualTo(42)
        }

        it("should convert a string value from the project property") {

            val extra: ExtraPropertiesExtension = project.requiredExtension()
            extra.set("testProperty", "42")

            val provider = project.intProviderFromProjectProperty("testProperty")

            assertThat(provider.orNull)
                .isEqualTo(42)
        }

        it("should throw an exception if the value cannot be converted to an integer") {

            val extra: ExtraPropertiesExtension = project.requiredExtension()
            extra.set("testProperty", "hello")

            val provider = project.intProviderFromProjectProperty("testProperty")

            assertThat { provider.get() }
                .isFailure()
                .isInstanceOf(IllegalArgumentException::class)
        }

        it("should not have a value if project property is not set") {

            val provider = project.intProviderFromProjectProperty("testProperty")

            assertThat(provider.orNull)
                .isNull()
        }

        it("should use the default value if project property is not set") {

            val provider = project.intProviderFromProjectProperty("testProperty", 123)

            assertThat(provider.orNull)
                .isEqualTo(123)
        }

        it("should not use the default value if project property is set") {

            val extra: ExtraPropertiesExtension = project.requiredExtension()
            extra.set("testProperty", "42")

            val provider = project.intProviderFromProjectProperty("testProperty", 123)

            assertThat(provider.orNull)
                .isEqualTo(42)
        }
    }


    describe("dirProviderFromProjectProperty") {

        it("should use the value from the project property") {

            val extra: ExtraPropertiesExtension = project.requiredExtension()
            extra.set("testProperty", "/foo/bar")

            val provider = project.dirProviderFromProjectProperty("testProperty")

            assertThat(provider.asFile().orNull)
                .isEqualTo(File("/foo/bar"))
        }

        it("should use the project dir as base for relative paths") {

            val extra: ExtraPropertiesExtension = project.requiredExtension()
            extra.set("testProperty", "foo/bar")

            val provider = project.dirProviderFromProjectProperty("testProperty")

            assertThat(provider.asFile().orNull)
                .isEqualTo(project.projectDir.resolve("foo/bar"))
        }

        it("should not have a value if project property is not set") {

            val provider = project.dirProviderFromProjectProperty("testProperty")

            assertThat(provider.orNull)
                .isNull()
        }

        it("should use the default value if project property is not set") {

            val provider = project.dirProviderFromProjectProperty("testProperty", "/default/path")

            assertThat(provider.asFile().orNull)
                .isEqualTo(File("/default/path"))
        }

        it("should use the project dir as base for relative path in default value") {

            val provider = project.dirProviderFromProjectProperty("testProperty", "default/path")

            assertThat(provider.asFile().orNull)
                .isEqualTo(project.projectDir.resolve("default/path"))
        }

        it("should not use the default value if project property is set") {

            val extra: ExtraPropertiesExtension = project.requiredExtension()
            extra.set("testProperty", "/foo/bar")

            val provider = project.dirProviderFromProjectProperty("testProperty", "/default/path")

            assertThat(provider.asFile().orNull)
                .isEqualTo(File("/foo/bar"))
        }

        it("should evaluate GString") {

            val extra: ExtraPropertiesExtension = project.requiredExtension()
            extra.set("fooDir", "/foo")
            extra.set("testProperty", "\${fooDir}/bar")

            val provider = project.dirProviderFromProjectProperty("testProperty", evaluateGString = true)

            assertThat(provider.asFile().orNull)
                .isEqualTo(File("/foo/bar"))
        }
    }


    describe("fileProviderFromProjectProperty") {

        it("should use the value from the project property") {

            val extra: ExtraPropertiesExtension = project.requiredExtension()
            extra.set("testProperty", "/foo/bar")

            val provider = project.fileProviderFromProjectProperty("testProperty")

            assertThat(provider.asFile().orNull)
                .isEqualTo(File("/foo/bar"))
        }

        it("should use the project dir as base for relative paths") {

            val extra: ExtraPropertiesExtension = project.requiredExtension()
            extra.set("testProperty", "foo/bar")

            val provider = project.fileProviderFromProjectProperty("testProperty")

            assertThat(provider.asFile().orNull)
                .isEqualTo(project.projectDir.resolve("foo/bar"))
        }

        it("should not have a value if project property is not set") {

            val provider = project.fileProviderFromProjectProperty("testProperty")

            assertThat(provider.orNull)
                .isNull()
        }

        it("should use the default value if project property is not set") {

            val provider = project.fileProviderFromProjectProperty("testProperty", "/default/path")

            assertThat(provider.asFile().orNull)
                .isEqualTo(File("/default/path"))
        }

        it("should use the project dir as base for relative path in default value") {

            val provider = project.fileProviderFromProjectProperty("testProperty", "default/path")

            assertThat(provider.asFile().orNull)
                .isEqualTo(project.projectDir.resolve("default/path"))
        }

        it("should not use the default value if project property is set") {

            val extra: ExtraPropertiesExtension = project.requiredExtension()
            extra.set("testProperty", "/foo/bar")

            val provider = project.fileProviderFromProjectProperty("testProperty", "/default/path")

            assertThat(provider.asFile().orNull)
                .isEqualTo(File("/foo/bar"))
        }

        it("should evaluate GString") {

            val extra: ExtraPropertiesExtension = project.requiredExtension()
            extra.set("fooDir", "/foo")
            extra.set("testProperty", "\${fooDir}/bar")

            val provider = project.fileProviderFromProjectProperty("testProperty", evaluateGString = true)

            assertThat(provider.asFile().orNull)
                .isEqualTo(File("/foo/bar"))
        }
    }


    describe("durationProviderFromProjectProperty") {

        it("should use the value from the project property") {

            val extra: ExtraPropertiesExtension = project.requiredExtension()
            extra.set("testProperty", Duration.ofSeconds(42))

            val provider = project.durationProviderFromProjectProperty("testProperty")

            assertThat(provider.orNull)
                .isEqualTo(Duration.ofSeconds(42))
        }

        it("should convert an ISO string value from the project property") {

            val extra: ExtraPropertiesExtension = project.requiredExtension()
            extra.set("testProperty", "PT3M30S")

            val provider = project.durationProviderFromProjectProperty("testProperty")

            assertThat(provider.orNull)
                .isEqualTo(Duration.ofSeconds(3 * 60 + 30))
        }

        it("should convert a duration string value from the project property") {

            val extra: ExtraPropertiesExtension = project.requiredExtension()
            extra.set("testProperty", "3m30s")

            val provider = project.durationProviderFromProjectProperty("testProperty")

            assertThat(provider.orNull)
                .isEqualTo(Duration.ofSeconds(3 * 60 + 30))
        }

        it("should convert a number string value from the project property") {

            val extra: ExtraPropertiesExtension = project.requiredExtension()
            extra.set("testProperty", "42")

            val provider = project.durationProviderFromProjectProperty("testProperty")

            assertThat(provider.orNull)
                .isEqualTo(Duration.ofSeconds(42))
        }

        it("should not have a value if project property is not set") {

            val provider = project.durationProviderFromProjectProperty("testProperty")

            assertThat(provider.orNull)
                .isNull()
        }

        it("should use the default value if project property is not set") {

            val provider = project.durationProviderFromProjectProperty("testProperty", Duration.ofSeconds(32))

            assertThat(provider.orNull)
                .isEqualTo(Duration.ofSeconds(32))
        }

        it("should not use the default value if project property is set") {

            val extra: ExtraPropertiesExtension = project.requiredExtension()
            extra.set("testProperty", "7m42s")

            val provider = project.durationProviderFromProjectProperty("testProperty", Duration.ofSeconds(32))

            assertThat(provider.orNull)
                .isEqualTo(Duration.ofSeconds(7 * 60 + 42))
        }
    }
})
