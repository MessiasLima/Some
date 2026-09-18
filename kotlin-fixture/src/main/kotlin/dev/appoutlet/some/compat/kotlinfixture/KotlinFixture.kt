package dev.appoutlet.some.compat.kotlinfixture

/**
 * Creates a compatibility [Fixture] generator configured with [configuration].
 */
fun kotlinFixture(
    configuration: ConfigurationBuilder.() -> Unit = {}
): Fixture {
    val builder = ConfigurationBuilder().apply(configuration)
    return Fixture(builder.build())
}
