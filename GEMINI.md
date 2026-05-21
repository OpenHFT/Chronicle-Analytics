# Chronicle-Analytics Project

This file provides a development context for the Chronicle-Analytics library, a Java-based utility for sending application events to Google Analytics 4.

## Project Overview

Chronicle-Analytics is a lightweight, dependency-minimal Java library designed to let developers integrate Google Analytics 4 event tracking into their applications. It provides a simple, fluent builder API to construct and send analytics events. The library is designed to be robust and unobtrusive, with features like rate limiting and automatic disabling in test environments.

### Key Technologies

*   **Java 8+:** The library is compatible with Java 8 and higher.
*   **Maven:** The project is built and managed using Apache Maven.
*   **JUnit 5:** Testing is performed with JUnit 5.
*   **No Transitive Dependencies:** The library is designed to be lightweight and avoids adding transitive dependencies to projects that include it.

### Architecture

The library's core is the `Analytics` interface, which defines the contract for sending events. A concrete implementation is provided through the `VanillaAnalyticsBuilder` class, which is accessed via the static `Analytics.builder()` method. This builder provides a fluent API for configuring the analytics client, including setting user properties, event parameters, rate limiting, and custom logging. Events are sent to the Google Analytics Measurement Protocol endpoint via HTTP POST requests.

## Building and Running

### Building from Source

To build the project, run the following command from the root directory:

```bash
mvn install
```

This will compile the source code, run the tests, and install the library in your local Maven repository.

### Running Tests

To run the unit tests, use the following command:

```bash
mvn test
```

## Development Conventions

### Coding Style

The codebase follows standard Java conventions. The use of the `@NotNull` annotation from the JetBrains annotations library indicates a convention of non-null parameters and return values in the public API.

### Testing

The project uses JUnit 5 for testing. Tests are located in the `src/test/java` directory. The library includes a feature that automatically disables analytics reporting when JUnit is detected on the classpath, which can be overridden with the `withReportDespiteJUnit()` builder method.

### Contribution

Contributions are welcome. Please ensure that any new code includes appropriate unit tests and that all existing tests pass before submitting a pull request.
