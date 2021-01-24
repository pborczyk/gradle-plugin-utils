package org.unbrokendome.gradle.pluginutils


/**
 * Split a string into words. All characters that are not letters or digits are considered separators.
 *
 * Upper-case letters also start a new word (camel case). The casing will be changed to lower-case in the output.
 *
 * If the input string is empty, the returned list will be empty. If the input string does not contain any word
 * separators, the returned list will contain one element that is identical to the input string.
 *
 * @receiver the string to split into words
 * @return a [Sequence] of words in the string
 */
fun String.splitIntoWords(): Sequence<String> = sequence {

    val builder = StringBuilder()

    this@splitIntoWords.forEach { ch ->
        if (ch.isUpperCase()) {
            if (builder.isNotEmpty()) {
                yield(builder.toString())
                builder.setLength(0)
            }
            builder.append(ch.toLowerCase())

        } else if (!ch.isLetterOrDigit()) {
            if (builder.isNotEmpty()) {
                yield(builder.toString())
                builder.setLength(0)
            }

        } else {
            builder.append(ch)
        }
    }

    if (builder.isNotEmpty()) {
        yield(builder.toString())
    }
}


/**
 * Splits a string into words, capitalizes each word and concatenates them into a single string (camel case).
 *
 * @receiver the string to modify
 * @param capitalizeFirst whether to capitalize the first word
 * @return the resulting string
 */
fun String.capitalizeWords(capitalizeFirst: Boolean = true): String =
    buildString {
        splitIntoWords().iterator()
            .also {
                if (!capitalizeFirst && it.hasNext()) {
                    append(it.next())
                }
            }
            .forEachRemaining { word ->
                append(word.capitalize())
            }
    }
