# AGENTS.md

## Scope
- Chronicle-Analytics sends usage analytics to Google Analytics 4 and legacy GA3.
- Runtime behaviour: JUnit on the classpath disables analytics unless `withReportDespiteJUnit()` is used; `UA-` IDs route to GA3; other IDs route to GA4.
- Reporting uses a single daemon thread named `chronicle~analytics~http~client`, and `chronicle.analytics.disable=true` disables reporting.

## Build and test
- Preferred full check:
  - `mkdir -p logs`
  - `mvn verify -l logs/mvn-verify.log`
- Module-scoped example:
  - `mvn -pl <module> -am verify -l logs/mvn-verify.log`
- Test example:
  - `mvn -Dtest=AnalyticsTest test -l logs/mvn-test.log`
- No-op module:
  - `mvn -f noop/pom.xml test -l logs/mvn-noop-test.log`
- Review logs:
  - `rg -n '^\[(WARNING|ERROR)\]|SLF4J\(W\)|\bWARNING:|\bwarning:' logs/mvn-verify.log`
- Do not commit logs/.

## Repo map
- Main sources live in `src/main/java`, tests in `src/test/java`.
- Java 11 module descriptor lives in `src/main/Java11/module-info.java.txt` for the multi-release JAR.
- `noop/` builds an empty JAR used to disable analytics.

## Constraints
- Java baseline: 8 (multi-release descriptor for Java 11).
- Source files must stay ISO-8859-1 (code points 0-255). Prefer ASCII; avoid smart quotes and non-breaking spaces.
- Preserve public APIs unless explicitly requested.
- Keep dependencies minimal and avoid extra allocations on hot paths.
- Treat warnings as defects; keep logs clean.

## Docs and review checklist
- Keep docs, tests, and code in sync.
- For large mechanical changes, declare the transformation rule and keep it consistent.
- Add clarifying comments only when intent is non-obvious.

## References
- `OpenHFT/docs/Company-Wide-Tagging.adoc` for tagging and decision record templates.
