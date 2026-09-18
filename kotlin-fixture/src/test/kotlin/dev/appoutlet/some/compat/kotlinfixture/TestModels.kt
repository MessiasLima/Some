package dev.appoutlet.some.compat.kotlinfixture

internal data class TestUser(
    val id: Int,
    val name: String,
    val age: Int = 30
)

internal interface Animal
internal data class Dog(val name: String = "Buddy") : Animal
internal data class Cat(val name: String = "Whiskers") : Animal

internal data class GenericBox<T>(
    val value: T
)

internal data class RecursiveNode(
    val value: Int,
    val next: RecursiveNode?
)

internal data class NonNullableRecursiveNode(
    val value: Int,
    val next: NonNullableRecursiveNode
)
