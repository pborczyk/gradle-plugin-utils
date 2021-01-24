package org.unbrokendome.gradle.pluginutils.io

import groovy.text.SimpleTemplateEngine
import org.gradle.api.file.ContentFilterable
import org.unbrokendome.gradle.pluginutils.io.DelegateReader
import java.io.Reader
import java.io.StringReader
import java.io.StringWriter


/**
 * Expands property references in each file as it is copied.
 *
 * More specifically, each file is transformed using Groovy's [SimpleTemplateEngine]. This means you can use simple
 * property references, such as
 * `$property` or `${property}` in the file. You can also include arbitrary Groovy code in the
 * file, such as `${version ?: 'unknown'}` or `${classpath*.name.join(' ')}`
 *
 * @param properties to implement line based filtering
 * @param escapeBackslash whether to escape backslashes in the output
 * @return this
 */
fun ContentFilterable.expand(properties: Map<String, *>, escapeBackslash: Boolean): ContentFilterable =
    filter(
        mapOf(
            "properties" to properties,
            "escapeBackslash" to escapeBackslash
        ),
        SimpleTemplateEngineFilterReader::class.java
    )


/**
 * A transforming [Reader] that will use a Groovy [SimpleTemplateEngine].
 *
 * Similar to what Gradle's built-in [ContentFilterable.expand] does, but also makes available the `escapeBackslash`
 * property from the [SimpleTemplateEngine].
 */
internal class SimpleTemplateEngineFilterReader(
    input: Reader
) : DelegateReader(input) {

    var properties: Map<String, *> = emptyMap<String, Any?>()
    var escapeBackslash: Boolean = false


    override val delegate: Reader by lazy(LazyThreadSafetyMode.NONE) {

        val template = `in`.use { input ->
            val engine = SimpleTemplateEngine()
            engine.isEscapeBackslash = escapeBackslash
            engine.createTemplate(input)
        }

        val writer = StringWriter()
        template.make(properties).writeTo(writer)

        StringReader(writer.toString())
    }
}
