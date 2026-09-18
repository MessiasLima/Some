package dev.appoutlet.some.compat.kotlinfixture

import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Receiver interface for compatibility factory blocks.
 */
interface Generator<out T> {
    /**
     * Shared random source for generating fixture values.
     */
    val random: Random

    /**
     * Resolves a fixture value for [type] through the active resolver chain.
     */
    fun <R> resolveType(type: KType): R

    /**
     * Selects a value from [iterable] or falls back to resolving a value for [type] when [iterable] is empty.
     */
    fun <R> resolveRange(iterable: Iterable<R>, type: KType): R
}

/**
 * Resolves a fixture value of type [R] through the active resolver chain.
 */
inline fun <reified R> Generator<*>.fixture(): R = resolveType(typeOf<R>())

/**
 * Selects a random element from [iterable], falling back to generating a fixture of type [R] if empty.
 */
inline fun <reified R> Generator<*>.range(iterable: Iterable<R>): R = resolveRange(iterable, typeOf<R>())
