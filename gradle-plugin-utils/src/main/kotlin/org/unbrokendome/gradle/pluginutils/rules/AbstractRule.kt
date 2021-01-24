package org.unbrokendome.gradle.pluginutils.rules

import org.gradle.api.Rule


/**
 * Base implementation for domain object rules.
 *
 * This class simply implements [toString] based on the [description][getDescription] of the rule.
 */
abstract class AbstractRule : Rule {

    override fun toString(): String =
        "Rule: $description"
}
