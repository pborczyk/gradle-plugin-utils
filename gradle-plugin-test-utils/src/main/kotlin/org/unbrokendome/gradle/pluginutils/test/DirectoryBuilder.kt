package org.unbrokendome.gradle.pluginutils.test

import java.io.File
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption


@DslMarker
@Retention(AnnotationRetention.BINARY)
annotation class DirectoryBuilderDsl


/**
 * Defines a micro-DSL for setting up directories and files for tests.
 */
interface DirectoryBuilder {

    /**
     * The path of the directory in scope.
     */
    val path: Path


    /**
     * Creates a subdirectory at the given path.
     *
     * The [name] may contain multiple path elements (e.g. `path/to/subdirectory`); in this case multiple levels
     * of subdirectories are created.
     *
     * @param name the name of the subdirectory. This is interpreted as a path, relative to the current path.
     * @param spec an optional block to set up the contents of the subdirectory
     */
    @DirectoryBuilderDsl
    fun directory(name: String, spec: (DirectoryBuilder.() -> Unit)? = null)


    /**
     * Creates or modifies a file in the current directory scope.
     *
     * @param name the name of the file
     * @param contents the contents of the file, as a String
     * @param append if `true`, appends to an existing file; if `false` an existing file will be replaced
     * @param charset the character set to use
     */
    @DirectoryBuilderDsl
    fun file(name: String, contents: String, append: Boolean = false, charset: Charset = Charsets.UTF_8)
}


/**
 * Builds a directory and its contents.
 *
 * The directory may or may not already exist.
 *
 * @param basePath the absolute path of the directory
 * @param spec a block to set up the contents of the directory
 */
@DirectoryBuilderDsl
fun directory(basePath: Path, spec: DirectoryBuilder.() -> Unit) =
    DefaultDirectoryBuilder(basePath).let(spec)


/**
 * Builds a directory and its contents.
 *
 * The directory may or may not already exist.
 *
 * @param basePath the absolute path of the directory
 * @param spec a block to set up the contents of the directory
 */
@DirectoryBuilderDsl
fun directory(basePath: File, spec: DirectoryBuilder.() -> Unit) =
    directory(basePath.toPath(), spec)


/**
 * Builds a directory and its contents.
 *
 * The directory may or may not already exist.
 *
 * @param basePath the absolute path of the directory
 * @param spec a block to set up the contents of the directory
 */
@DirectoryBuilderDsl
fun directory(basePath: String, spec: DirectoryBuilder.() -> Unit) =
    directory(Paths.get(basePath), spec)


private class DefaultDirectoryBuilder(override val path: Path) : DirectoryBuilder {

    init {
        if (!Files.exists(path)) {
            Files.createDirectories(path)
        }
        check(Files.isDirectory(path)) { "Directory \"$path\" exists and is not a directory" }
    }


    override fun directory(name: String, spec: (DirectoryBuilder.() -> Unit)?) {
        val subPath = this.path.resolve(name)
        if (!Files.exists(subPath)) {
            Files.createDirectories(subPath)
        }
        check(Files.isDirectory(subPath)) { "Directory \"$subPath\" exists and is not a directory" }
        val builder = DefaultDirectoryBuilder(subPath)
        spec?.invoke(builder)
    }


    override fun file(name: String, contents: String, append: Boolean, charset: Charset) {
        val filePath = this.path.resolve(name)

        val options = arrayOf(
            StandardOpenOption.CREATE,
            StandardOpenOption.WRITE,
            if (append) StandardOpenOption.APPEND else StandardOpenOption.TRUNCATE_EXISTING
        )

        Files.newBufferedWriter(filePath, charset, *options)
            .use { writer ->
                writer.append(contents.trimIndent())
            }
    }
}
