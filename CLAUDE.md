# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Chronicle Analytics is a lightweight telemetry client library for sending usage events to Google Analytics (both GA3/Universal Analytics and GA4). It is designed as a zero-dependency Java library with minimal footprint, thread-safe operation, and best-effort async delivery.

**Key characteristics:**
- Zero transitive runtime dependencies (only `org.jetbrains:annotations` at compile time)
- Thread-safe with single daemon thread for HTTP operations
- Fire-and-forget async event sending (never blocks caller)
- Automatic JUnit detection to prevent test events
- Built-in frequency limiting and anti-storm protection
- OSGi bundle support

## Build and Test Commands

```bash
# Verify build with all tests
mvn clean verify

# Quick compile (skip tests)
mvn clean compile -DskipTests

# Run specific test
mvn test -Dtest=AnalyticsTest

# Check code coverage (requires Java 11+)
mvn clean test jacoco:report
# Report at: target/site/jacoco/index.html

# Install to local repository
mvn clean install
```

**Coverage requirements:**
- Line coverage: 89%
- Branch coverage: 72%

## Architecture Overview

### Design Pattern: Fluent Builder

Analytics instances are created via a fluent builder pattern:

```java
Analytics analytics = Analytics.builder(measurementId, apiSecret)
    .putEventParameter("app_version", "1.4.2")
    .putUserProperty("os_name", "Linux")
    .withFrequencyLimit(10, 1, TimeUnit.MINUTES)
    .withErrorLogger(System.err::println)
    .build();

analytics.sendEvent("started");
```

### Core Components

**Public API** (`src/main/java/net/openhft/chronicle/analytics/`):
- `Analytics` interface: Main public API with `sendEvent()` methods
- `Analytics.Builder` interface: Fluent configuration API

**Internal Implementation** (`src/main/java/net/openhft/chronicle/analytics/internal/`):
- `VanillaAnalyticsBuilder`: Concrete builder, auto-selects GA3 vs GA4 based on measurement ID prefix
- `AbstractGoogleAnalytics`: Base class handling frequency limiting, client ID persistence, anti-storm logic
- `GoogleAnalytics4`: GA4 implementation (JSON payloads to `/mp/collect`)
- `GoogleAnalytics3`: Legacy Universal Analytics (URL-encoded to `/collect`)
- `MuteAnalytics`: Null-object pattern for testing (automatic when JUnit detected)
- `HttpUtil`: Single-threaded executor for async HTTP POST with 2-second timeout
- `FilesUtil`: Client ID persistence in `~/.chronicle.analytics.client.id`
- `JsonUtil`: Lightweight JSON serialization (no external library)
- `JUnitUtil`: Detects JUnit 4/5 on classpath

### Key Architectural Decisions

1. **Zero Dependencies**: Custom JSON implementation and native HTTP client to avoid transitive dependencies
2. **Best-Effort Delivery**: No retries, no delivery guarantees - analytics should never impact application reliability
3. **Frequency Limiting**: Built-in rate throttling prevents excessive traffic
4. **Anti-Storm Protection**: Filesystem timestamp prevents analytics storms when many instances start simultaneously
5. **Automatic JUnit Detection**: Mutes analytics when tests are running (override with `withReportDespiteJUnit()`)
6. **Single HTTP Thread**: Daemon thread `chronicle~analytics~http~client` handles all HTTP operations
7. **Dual Protocol Support**: Automatically selects GA3 (UA-* prefix) or GA4 based on measurement ID
8. **Client ID Persistence**: Stable UUID in user home directory tracks returning users

### Thread Safety

- **Analytics instances**: Thread-safe (atomic operations for rate limiting)
- **Builder**: Not thread-safe (single-use pattern enforced)
- **HTTP sending**: Single dedicated daemon thread
- **File operations**: Lock-free, best-effort

### Data Flow

1. Client calls `analytics.sendEvent(name, params)`
2. Eligibility check: mute detection, frequency limit
3. Payload assembly: merge parameters, load client ID
4. Async dispatch: submit HTTP POST task to executor
5. Fire-and-forget: return immediately, log errors to error logger

## Language and Character Set Requirements

**CRITICAL**: All code and documentation must follow these rules:

- **British English spelling**: `organisation`, `licence` (not `organization`, `license`) except technical terms like `synchronized`
- **ISO-8859-1 only**: Code points 0-255. No smart quotes, non-breaking spaces, or accented characters except in string literals
- Use textual forms for unavailable symbols: `micro-second`, `>=`, `:alpha:`

## JavaDoc Guidelines

Only write JavaDoc that adds information beyond the method signature:

**DO:**
- State behavioural contracts, edge cases, thread-safety guarantees
- Document units, performance characteristics, checked exceptions
- Keep first sentence short (becomes summary line)

**DON'T:**
- Restate the obvious ("Gets the value", "Sets the name")
- Duplicate parameter names/types without additional explanation
- Leave stale comments that contradict code

## Commit Message Format

1. Subject line <= 72 chars, imperative mood: "Fix roll-cycle offset in ExcerptAppender"
2. Reference JIRA/GitHub issue if exists
3. Body: root cause → fix → measurable impact
4. Run `mvn verify` after rebasing

## Testing Conventions

- **Framework**: JUnit 5 (Jupiter)
- **Parallel execution**: 4 forks with JVM reuse
- **Convention**: Test classes end with `Test.java`, mirror package structure
- **Integration tests**: Use MockWebServer for HTTP endpoint testing
- **File cleanup**: Tests clean up `.chronicle.analytics.*` files in home directory

### Test Organization

- Unit tests: Direct class testing (FilesUtilTest, JsonUtilTest)
- Integration tests: End-to-end with MockWebServer (AnalyticsTest, GoogleAnalytics3Test)
- Example mains: Runnable examples (AnalyticsExampleMain)

## Common Development Tasks

### Adding Event Parameters

Modify `VanillaAnalyticsBuilder` to add builder methods, then update `GoogleAnalytics4.jsonFor()` and `GoogleAnalytics3.bodyFor()` to include them in payloads.

### Modifying Rate Limiting

Edit `AbstractGoogleAnalytics.attemptToSend()` to adjust frequency limit logic.

### Changing HTTP Behavior

Modify `HttpUtil` class (connection timeout, thread pool, error handling).

### Adjusting JSON Serialization

Update `JsonUtil` for additional character escaping or structure changes.

## Project Structure

```
src/main/java/net/openhft/chronicle/analytics/
├── Analytics.java                    # Public API interface
├── internal/
│   ├── VanillaAnalyticsBuilder.java  # Builder implementation
│   ├── AbstractGoogleAnalytics.java  # Base implementation
│   ├── GoogleAnalytics4.java         # GA4 implementation
│   ├── GoogleAnalytics3.java         # UA implementation
│   ├── MuteAnalytics.java            # Null object
│   ├── HttpUtil.java                 # HTTP client
│   ├── FilesUtil.java                # File I/O
│   ├── JsonUtil.java                 # JSON serialization
│   └── JUnitUtil.java                # Test detection

src/test/java/net/openhft/chronicle/analytics/
├── AnalyticsTest.java                # API integration tests
├── internal/
│   ├── GoogleAnalytics3Test.java     # GA3 tests
│   ├── GoogleAnalyticsTest.java      # Core tests
│   ├── FilesUtilTest.java            # File utils tests
│   └── ...

src/main/adoc/
├── project-requirements.adoc         # Formal requirements
└── security-review.adoc              # Security analysis
```

## Important Files

- `src/main/java/net/openhft/chronicle/analytics/Analytics.java`: Main public API
- `src/main/java/net/openhft/chronicle/analytics/internal/VanillaAnalyticsBuilder.java`: Builder and factory logic
- `src/main/java/net/openhft/chronicle/analytics/internal/AbstractGoogleAnalytics.java`: Rate limiting and eligibility
- `pom.xml`: Maven configuration, dependencies, plugins

## Quick Reference

### Measurement ID Patterns

- **GA4**: Starts with `G-` (e.g., `G-TDAZG4CU3G`)
- **GA3/Universal Analytics**: Starts with `UA-` (e.g., `UA-123456-1`)

Builder automatically selects implementation based on prefix.

### Default Behaviors

- **JUnit detection**: Analytics automatically muted when JUnit 4/5 detected on classpath
- **Client ID**: Generated UUID stored in `~/.chronicle.analytics.client.id`
- **Frequency limit**: None by default (must explicitly configure)
- **Thread name**: `chronicle~analytics~http~client` (daemon)
- **HTTP timeout**: 2 seconds for connection and read
- **Anti-storm file**: `~/.chronicle.analytics.last` (second-of-day timestamp)

### Builder Options

- `putEventParameter(key, value)`: Add event-level parameter
- `putUserProperty(key, value)`: Add user-level property (GA4 only)
- `withFrequencyLimit(messages, duration, timeUnit)`: Rate limiting
- `withClientIdFileName(fileName)`: Custom client ID file location
- `withUrl(url)`: Custom GA endpoint (e.g., debug endpoint)
- `withErrorLogger(consumer)`: Custom error logging
- `withDebugLogger(consumer)`: Custom debug logging
- `withReportDespiteJUnit()`: Override automatic JUnit muting

## Security Considerations

- **Sensitive data**: Caller must scrub sensitive information before calling `sendEvent()`
- **Client ID file**: Documents stable UUID at `~/.chronicle.analytics.client.id`
- **Custom URL**: Only override for trusted proxies
- **HTTPS by default**: Transport security enabled
- **Error logging**: Supply non-no-op error logger for production debugging

## Maven Profiles

- **java11**: Excludes service files for Java 11+ JPMS builds
- **quality**: Runs Checkstyle and SpotBugs (activated on Java 11+)

## Real-Time Documentation

When making changes:

1. Update relevant `.adoc` files in `src/main/adoc/` alongside code changes
2. Keep documentation, tests, and code synchronised
3. Small commits: one requirement or coherent change per commit
4. Documentation should be precise enough for clean-room re-implementation