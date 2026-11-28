---
icon: lucide/git-branch
---
# Forced Subtypes

Sealed hierarchies normally resolve to a random leaf subclass. When a test needs one specific branch, register a
subtype mapping instead of relying on randomness:

```kotlin
sealed interface Payment {
    data class Card(val last4: String) : Payment
    data class Cash(val amount: Int) : Payment
}

val somePayments = someSetup {
    subType<Payment, Payment.Card>()
}

val payment: Payment = somePayments.some()
// Always Payment.Card, with a normally generated last4
```

The mapped subtype is resolved through the normal resolver chain, so its constructor and properties are generated
like any other fixture. A subtype mapping is deterministic: it does not fall back to another child when the sealed
parent has several subclasses.

## Supported parents

Mappings work for:

- Sealed classes and sealed interfaces.
- Abstract classes.
- Non-sealed interfaces.
- Concrete parents, when you want to force construction of one subclass.

A mapping is necessary for abstract classes and non-sealed interfaces because they cannot be instantiated directly.
It also gives deterministic control over a sealed hierarchy without changing random selection when no mapping is
registered.

```kotlin
abstract class Animal
data class Dog(val name: String) : Animal()
data class Cat(val lives: Int) : Animal()

val someAnimals = someSetup {
    subType<Animal, Dog>()
}

val animal = someAnimals.some<Animal>() // Dog(name = ...)
```

## Generic parents

The reified registration captures the parent and child classes. When the requested parent has type arguments and the
mapped subtype declares the same number of type parameters, those arguments are preserved on the subtype:

```kotlin
sealed class Result<out T> {
    data class Success<T>(val value: T) : Result<T>()
    data class Failure(val message: String) : Result<Nothing>()
}

val someResults = someSetup {
    subType<Result<String>, Result.Success<String>>()
}

val result = someResults.some<Result<String>>() // Result.Success(value = "...")
```

If the subtype has a different number of type parameters, the subtype is created without type arguments. Its
parameters then fall back to their declared upper bounds, and normal generation continues from there. This fallback is
useful for concrete children of generic parents:

```kotlin
abstract class Box<T>
data class NamedBox(val name: String) : Box<String>()

someSetup {
    subType<Box<String>, NamedBox>()
}
```

## Precedence

For a requested parent, Some applies configuration in this order:

1. Type factories registered with `factory(...)`.
2. Subtype mappings registered with `subType(...)`.
3. Random sealed-class selection.
4. Normal class resolution.

A factory for the parent therefore wins over a subtype mapping. Type-level factories registered for an exact `KType`
also win because they run with the other type factories.

Only one subtype can be registered per parent. Registering another mapping for the same parent replaces the previous
one.

## Cycles and self-mappings

A self-mapping such as `subType<Foo, Foo>()`, or a cyclic mapping that eventually maps a type back to itself, cannot
produce a finite value. Resolution fails with a clear `SomeSubtypeMappingException` instead of recursing until a stack
overflow.

```kotlin
some<Foo> {
    subType<Foo, Foo>()
}
// SomeSubtypeMappingException: Cyclic subtype mapping detected for Foo -> Foo
```

## Related Docs

- Use [Type and Property Factories](custom-factories.md) when a whole type or property needs a fixed value.
- Use [Supported Types](supported-types.md) to see how sealed classes, enums, and other Kotlin constructs are handled.
