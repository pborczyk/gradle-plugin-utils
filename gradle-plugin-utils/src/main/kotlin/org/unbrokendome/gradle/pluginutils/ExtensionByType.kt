package org.unbrokendome.gradle.pluginutils

import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.reflect.TypeOf
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty


fun <T> extensionByType(type: TypeOf<T>): GenericReadOnlyPropertyDelegate<Any, T> =
    ReceiverExtensionByTypeDelegate(type)


fun <T : Any> extensionByType(type: KClass<T>): GenericReadOnlyPropertyDelegate<Any, T> =
    extensionByType(TypeOf.typeOf(type.java))


inline fun <reified T> extensionByType(): GenericReadOnlyPropertyDelegate<Any, T> =
    extensionByType(typeOf<T>())


fun <T : Any> Any.extensionByType(type: TypeOf<T>): ReadOnlyPropertyDelegate<Nothing?, T> =
    ContainerExtensionByTypeDelegate(this, type)


fun <T : Any> Any.extensionByType(type: KClass<T>): ReadOnlyPropertyDelegate<Nothing?, T> =
    extensionByType(TypeOf.typeOf(type.java))


inline fun <reified T : Any> Any.extensionByType(): ReadOnlyPropertyDelegate<Nothing?, T> =
    this.extensionByType(typeOf())


class ContainerExtensionByTypeDelegate<T>(
    container: Any,
    private val type: TypeOf<T>
): ReadOnlyPropertyDelegate<Nothing?, T> {

    private val container = container as ExtensionAware

    @Suppress("UNCHECKED_CAST")
    override fun provideDelegate(receiver: Nothing?, property: KProperty<*>): ReadOnlyProperty<Nothing?, T> =
        if (property.returnType.isMarkedNullable) {
            OptionalProperty(container, type) as ReadOnlyProperty<Nothing?, T>
        } else {
            RequiredProperty(container, type)
        }


    private class OptionalProperty<T>(
        private val container: ExtensionAware,
        private val type: TypeOf<T>
    ) : ReadOnlyProperty<Nothing?, T?> {

        override fun getValue(thisRef: Nothing?, property: KProperty<*>): T? =
            container.extensions.findByType(type)
    }

    private class RequiredProperty<T>(
        private val container: ExtensionAware,
        private val type: TypeOf<T>
    ) : ReadOnlyProperty<Nothing?, T> {

        override fun getValue(thisRef: Nothing?, property: KProperty<*>): T =
            container.extensions.getByType(type)
    }
}


private class ReceiverExtensionByTypeDelegate<T>(
    private val type: TypeOf<T>
) : GenericReadOnlyPropertyDelegate<Any, T> {

    @Suppress("UNCHECKED_CAST")
    override fun <R> provideDelegate(receiver: R, property: KProperty<*>): ReadOnlyProperty<Any, T> =
        if (property.returnType.isMarkedNullable) {
            OptionalExtensionByTypeProperty(type) as ReadOnlyProperty<Any?, T>
        } else {
            RequiredExtensionByTypeProperty(type)
        }


    private class OptionalExtensionByTypeProperty<T>(
        private val type: TypeOf<T>
    ) : ReadOnlyProperty<Any, T?> {

        override fun getValue(thisRef: Any, property: KProperty<*>): T? {
            return (thisRef as? ExtensionAware?)?.extensions?.findByType(type)
        }
    }


    private class RequiredExtensionByTypeProperty<T>(
        private val type: TypeOf<T>
    ) : ReadOnlyProperty<Any?, T> {

        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            val container = checkNotNull(thisRef as? ExtensionAware?) { "The container is not ExtensionAware" }
            return container.extensions.getByType(type)
        }
    }
}
