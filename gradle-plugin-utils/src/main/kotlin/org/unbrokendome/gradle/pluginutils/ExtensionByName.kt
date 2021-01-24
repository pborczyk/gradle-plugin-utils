package org.unbrokendome.gradle.pluginutils

import org.gradle.api.plugins.ExtensionAware
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


fun <T> Any.extensionByName(name: String? = null): ReadOnlyPropertyDelegate<Any?, T> =
    ContainerExtensionByNameDelegate(this, name)


fun <T> extensionByName(name: String? = null): GenericReadOnlyPropertyDelegate<Any?, T> =
    ReceiverExtensionByNameDelegate(name)


private class ContainerExtensionByNameDelegate<T>(
    container: Any,
    private val name: String? = null,
) : ReadOnlyPropertyDelegate<Any?, T> {

    private val container = container as ExtensionAware

    @Suppress("UNCHECKED_CAST")
    override fun provideDelegate(receiver: Any?, property: KProperty<*>): ReadOnlyProperty<Any?, T> =
        if (property.returnType.isMarkedNullable) {
            OptionalProperty<T>(container, name ?: property.name) as ReadOnlyProperty<Any?, T>
        } else {
            RequiredProperty(container, name ?: property.name)
        }


    private class OptionalProperty<T>(
        private val container: ExtensionAware,
        private val name: String
    ) : ReadOnlyProperty<Any?, T?> {

        @Suppress("UNCHECKED_CAST")
        override fun getValue(thisRef: Any?, property: KProperty<*>): T? =
            container.extensions.findByName(name) as T?
    }


    private class RequiredProperty<T>(
        private val container: ExtensionAware?,
        private val name: String
    ) : ReadOnlyProperty<Any?, T> {

        @Suppress("UNCHECKED_CAST")
        override fun getValue(thisRef: Any?, property: KProperty<*>): T =
            container!!.extensions.getByName(name) as T
    }
}


private class ReceiverExtensionByNameDelegate<T>(
    private val name: String? = null
) : GenericReadOnlyPropertyDelegate<Any?, T> {

    @Suppress("UNCHECKED_CAST")
    override fun <R> provideDelegate(receiver: R, property: KProperty<*>): ReadOnlyProperty<Any?, T> =
        if (property.returnType.isMarkedNullable) {
            OptionalProperty<T>(name ?: property.name) as ReadOnlyProperty<Any?, T>
        } else {
            RequiredProperty(name ?: property.name)
        }


    private class OptionalProperty<T>(
        private val name: String
    ) : ReadOnlyProperty<Any?, T?> {

        @Suppress("UNCHECKED_CAST")
        override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
            return (thisRef as? ExtensionAware?)?.extensions?.findByName(name) as T?
        }
    }


    private class RequiredProperty<T>(
        private val name: String
    ) : ReadOnlyProperty<Any?, T> {

        @Suppress("UNCHECKED_CAST")
        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            val container = checkNotNull(thisRef as? ExtensionAware?) { "The container is not ExtensionAware" }
            return container.extensions.getByName(name) as T
        }
    }
}
