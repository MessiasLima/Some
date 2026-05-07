# Contributing to Some

Thank you for considering a contribution to **Some**. We appreciate your time, your ideas, and your interest in helping improve the project.

Contributions of all sizes are welcome. Whether you are fixing a bug, improving documentation, adding a feature, refining tests, or correcting a small typo, your help matters.

## Project Stack

Some is a Kotlin JVM library for generating test data. The project uses the following tools and technologies:

- **Language**: [Kotlin](https://kotlinlang.org/) (JVM), targeting Java 21
- **Build tool**: [Gradle](https://gradle.org/) with Kotlin DSL
- **Testing**: [JUnit 5](https://junit.org/junit5/) through [kotlin-test](https://kotlinlang.org/api/latest/kotlin.test/)
- **Static analysis**: [Detekt](https://detekt.dev/) with auto-correction
- **Code coverage**: [Kover](https://github.com/Kotlin/kotlinx-kover), with minimum coverage requirements of 90% coverage
- **Documentation**: [Dokka](https://kotlinlang.org/docs/dokka-introduction.html)
- **Runtime reflection**: [kotlin-reflect](https://kotlinlang.org/docs/reflection.html)

## Development Environment Setup

### Prerequisites

Before working on the project, make sure you have the following tools available:

- **JDK 21**: the project uses JVM toolchain 21
- **Git**: used for version control
- **Gradle wrapper**: included in the repository as `./gradlew`, so you do not need to install Gradle separately

### Recommended Tools

- **IDE**: You can use any IDE that supports Kotlin and Gradle builds. We recommend [IntelliJ IDEA](https://www.jetbrains.com/idea/) or [Android Studio](https://developer.android.com/studio) because both provide strong Kotlin and Gradle support.

### Useful Gradlew Commands

Some is a library, so the most useful local checks are tests and static analysis. These commands are commonly used during development:

```bash
# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "*IntResolverTest"

# Run a single test method
./gradlew test --tests "*IntResolverTest.IntResolver generates int values"

# Run Detekt static analysis
./gradlew detekt

# Run tests with fresh execution, ignoring cache
./gradlew test --rerun-tasks
```

## Automations

The project runs [Detekt](https://detekt.dev/) before every push through a pre-push Git hook. This helps catch common quality and style issues before code is pushed. In normal situations, Git hooks are installed automatically, so you should not need to configure them manually. If, for some reason, the Git hooks are not installed automatically, you can install them by running: 
```sh
./gradlew installGitHooks
```

Every pull request is also checked by CI. The CI pipeline runs Detekt and all unit tests to make sure the code compiles, the minimum coverage requirements are met, and the code style is accepted.

Automated checks are important, but they are not the only review step. A human reviewer will always review the code before it is merged.

## Using AI for Coding

You are welcome to use any coding tool you prefer, including AI-assisted tools such as GitHub Copilot, Cursor, Claude, or others. We do not mind how the code is written as long as the final contribution meets the project's standards.

What matters most is the quality of the result:

- **Code should be clear and idiomatic**: follow the project's conventions, keep changes focused, and make sure Detekt passes.
- **Tests should be meaningful**: test real behavior and important edge cases. Avoid tests that only exist to increase coverage without validating useful behavior.
- **Changes should be covered by tests**: new behavior and bug fixes should include appropriate tests, and the project coverage requirements must continue to pass.

AI can be a helpful tool, but contributors are responsible for understanding, validating, and maintaining the code they submit.

## New to Open Source?

If this is your first time contributing to an open source project, welcome. GitHub has a helpful guide that explains the process, including how to fork a repository, create a branch, make changes, and open a pull request:

[Contributing to open source](https://docs.github.com/en/get-started/exploring-projects-on-github/contributing-to-open-source)

## Thank You

Thank you again for considering a contribution to Some. Every contribution is welcome, and we are grateful for the time and care you put into helping improve the project.
