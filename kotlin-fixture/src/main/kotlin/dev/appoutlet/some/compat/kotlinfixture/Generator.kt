package dev.appoutlet.some.compat.kotlinfixture

import kotlin.random.Random
import kotlin.reflect.KType
import kotlin.reflect.typeOf

interface Generator<T> {
    val random: Random
    val fixture: Fixture
}

internal typealias GeneratorFun = Generator<Any?>.() -> Any?

fun <T> Generator<T>.range(range: Iterable<T>) =
    range.shuffled(random).firstOrNull() ?: throw NoSuchElementException("Range is empty")