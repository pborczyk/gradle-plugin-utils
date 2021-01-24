package org.unbrokendome.gradle.pluginutils

import org.gradle.api.plugins.Convention
import org.gradle.api.plugins.ExtensionAware
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty


fun <T : Any> conventionPluginByType(type: KClass<T>): GenericReadOnlyPropertyDelegate<Any, T> =
    ReceiverConventionPluginByTypeDelegate(type)


inline fun <reified T : Any> conventionPluginByType(): GenericReadOnlyPropertyDelegate<Any, T> =
    conventionPluginByType(T::class)


fun <T : Any> Any.conventionPluginByType(type: KClass<T>): ReadOnlyPropertyDelegate<Nothing?, T> =
    ContainerConventionPluginByTypeDelegate(this, type)


inline fun <reified T : Any> Any.conventionPluginByType(): ReadOnlyPropertyDelegate<Nothing?, T> =
    this.conventionPluginByType(T::class)


private class ContainerConventionPluginByTypeDelegate<T : Any>(
    container: Any,
    private val type: KClass<T>
) : ReadOnlyPropertyDelegate<Nothing?, T> {

    private val container = container as ExtensionAware

    @Suppress("UNCHECKED_CAST")
    override fun provideDelegate(receiver: Nothing?, property: KProperty<*>): ReadOnlyProperty<Any?, T> {
        val propertyType = property.returnType
        return if (propertyType.isMarkedNullable) {
            OptionalConventionPluginByTypeProperty(container, type)
        } else {
            RequiredConventionPluginByTypeProperty(container, type)
        } as ReadOnlyProperty<Any?, T>
    }


    private class OptionalConventionPluginByTypeProperty<T : Any>(
        private val container: ExtensionAware,
        private val type: KClass<T>
    ): ReadOnlyProperty<Nothing?, T?> {

        override fun getValue(thisRef: Nothing?, property: KProperty<*>): T? =
            (container.extensions as Convention).findPlugin(type.javaObjectType)
    }


    private class RequiredConventionPluginByTypeProperty<T : Any>(
        private val container: ExtensionAware,
        private val type: KClass<T>
    ) : ReadOnlyProperty<Nothing?, T> {

        override fun getValue(thisRef: Nothing?, property: KProperty<*>): T =
            (container.extensions as Convention).getPlugin(type.javaObjectType)
    }

}


private class ReceiverConventionPluginByTypeDelegate<T : Any>(
    private val type: KClass<T>
) : GenericReadOnlyPropertyDelegate<Any, T> {

    @Suppress("UNCHECKED_CAST")
    override fun <R> provideDelegate(receiver: R, property: KProperty<*>): ReadOnlyProperty<Any, T> {
        val propertyType = property.returnType
        return if (propertyType.isMarkedNullable) {
            OptionalProperty(type)
        } else {
            RequiredProperty(type)
        } as ReadOnlyProperty<Any?, T>
    }


    private class OptionalProperty<T : Any>(
        private val type: KClass<T>
    ): ReadOnlyProperty<Any?, T?> {

        override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
            return (thisRef as? ExtensionAware?)
                ?.run { extensions as Convention }
                ?.findPlugin(type.javaObjectType)
        }
    }


    private class RequiredProperty<T : Any>(
        private val type: KClass<T>
    ) : ReadOnlyProperty<Any?, T> {

        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            val container = checkNotNull(thisRef as? ExtensionAware?) { "The container is not ExtensionAware" }
            return (container.extensions as Convention).getPlugin(type.javaObjectType)
        }
    }
}
