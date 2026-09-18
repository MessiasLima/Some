package dev.appoutlet.some.compat.kotlinfixture

internal data class TestUser(
    val id: Int,
    val name: String,
    val age: Int = 30
)

internal data class GenericBox<T>(val value: T)
