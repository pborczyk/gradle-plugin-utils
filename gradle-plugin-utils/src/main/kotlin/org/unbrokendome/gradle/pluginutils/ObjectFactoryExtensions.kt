package org.unbrokendome.gradle.pluginutils

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty


/**
 * Creates a [Property] to hold values of the given type.
 *
 * @param T the type of the property
 * @return the property
 */
inline fun <reified T : Any> ObjectFactory.property(): Property<T> =
    property(T::class.javaObjectType)


/**
 * Creates a new [ListProperty] to hold a list of values of the given type.
 *
 * @param T the type of element
 */
inline fun <reified T : Any> ObjectFactory.listProperty(): ListProperty<T> =
    listProperty(T::class.javaObjectType)


/**
 * Creates a new [SetProperty] to hold a list of values of the given type.
 *
 * @param T the type of element
 */
inline fun <reified T : Any> ObjectFactory.setProperty(): SetProperty<T> =
    setProperty(T::class.javaObjectType)


/**
 * Creates a new [MapProperty] to hold a map of the given key and value type.
 *
 * @param K the type of key
 * @param V the type of value
 */
@Suppress("UnstableApiUsage")
inline fun <reified K : Any, reified V : Any> ObjectFactory.mapProperty(): MapProperty<K, V> =
    mapProperty(K::class.javaObjectType, V::class.javaObjectType)
