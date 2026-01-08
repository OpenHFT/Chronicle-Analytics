# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Chronicle-Analytics is a library that provides remote ingress to Google Analytics 4 (GA4), allowing Java applications to send usage statistics and analytics data. The library supports both GA4 (measurement IDs starting with "G-") and legacy Google Analytics 3 (IDs starting with "UA-").

Key characteristics:
- Minimal dependencies (only `org.jetbrains:annotations` at compile time)
- Thread-safe Analytics instances
- Single daemon thread for HTTP communication
- Built-in frequency limiting and muting capabilities
- Automatic JUnit detection (analytics disabled during tests by default)

## Build Commands

### Basic Maven Operations
```bash
# Build the project
mvn clean install

# Run tests (runs with 4 forks, each test class gets its own JVM)
mvn test

# Run a single test class
mvn test -Dtest=AnalyticsTest

# Run a specific test method
mvn test -Dtest=AnalyticsTest#testMethodName

# Skip tests
mvn clean install -DskipTests

# Generate JavaDocs
mvn javadoc:javadoc

# Check code coverage (Jacoco configured with 89% line, 72% branch coverage)
mvn verify
```

## Architecture

### Core Components

**Public API:**
- `Analytics` interface: Main entry point for sending events
- `Analytics.Builder` interface: Builder pattern for configuring Analytics instances

**Internal Implementation:**
- `VanillaAnalyticsBuilder`: Concrete builder implementation that creates appropriate Analytics instances
- `AbstractGoogleAnalytics`: Base class containing:
  - Client ID management (persistent across application runs)
  - Frequency limiting logic (rate limiting per duration)
  - Muting logic (prevents call storms when multiple instances start on same machine)
  - Event parameter merging
- `GoogleAnalytics4`: GA4 implementation (for measurement IDs starting with "G-")
- `GoogleAnalytics3`: Legacy GA3 implementation (for IDs starting with "UA-")
- `MuteAnalytics`: No-op implementation returned when JUnit is detected

**Utility Classes:**
- `HttpUtil`: Manages single-threaded HTTP communication using daemon thread named "chronicle~analytics~http~client"
- `FilesUtil`: Handles persistent client ID storage (default: `~/.chronicle.analytics.client.id`)
- `JsonUtil`: Basic JSON generation without external dependencies
- `JUnitUtil`: Detects JUnit presence to disable analytics during tests

### Design Patterns

**Measurement ID Routing**: The builder automatically selects `GoogleAnalytics3` or `GoogleAnalytics4` based on whether the measurement ID starts with "UA-" or not.

**Frequency Limiting**: Implemented in `AbstractGoogleAnalytics.attemptToSend()`, tracking messages sent per time window using atomic operations.

**Muting**: Uses file timestamps to detect if another instance on the same machine recently sent analytics (same second of day), preventing call storms.

**Builder Protection**: Builders can only be used once; calling `build()` twice throws `IllegalStateException`.

### Thread Safety

- Analytics instances are thread-safe
- HTTP requests execute asynchronously on a single daemon thread
- Client ID and frequency limiting use atomic operations

## Testing

### Test Infrastructure
- JUnit 5 (Jupiter) for all tests
- MockWebServer (OkHttp) for HTTP endpoint mocking
- Test classes run in isolated JVMs (fork count: 4, reuse enabled)

### Special Test Considerations
- Analytics automatically disables when JUnit classes are on classpath
- Use `withReportDespiteJUnit()` to enable reporting in tests
- Tests often set system properties, which is why each test class gets its own JVM

## Module Structure

### Main Module (`chronicle-analytics`)
Standard Maven structure with production code in `src/main/java`.

### Java 11+ Module
`src/main/Java11/module-info.java.txt` contains module descriptor for Java 11+ JPMS support. The Maven bundle plugin packages this as a multi-release JAR.

### No-op Module (`noop`)
Located in `noop/` directory, this is a special build that produces an empty JAR. Users can include this to ensure analytics does nothing (alternative to exclusion rules).

## Version Information

Current development version: 2026.0-SNAPSHOT (release/2026.XYZ branch)
Parent POM: `net.openhft:java-parent-pom:2026.0-SNAPSHOT`

## Disabling Analytics

Analytics can be disabled by:
1. System property: `chronicle.analytics.disable=true`
2. JUnit detection (automatic unless `withReportDespiteJUnit()` is called)
3. Using the no-op module in `noop/`
4. Maven exclusion or Gradle configuration (see README.adoc)
