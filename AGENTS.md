# Chronicle-Analytics – Local Quality Knobs

This module inherits the shared Chronicle quality configuration via
`net.openhft:java-parent-pom` → `net.openhft:root-parent-pom`. Its POM
defines a JDK-guarded `quality` profile that wires Checkstyle and
SpotBugs as gating checks on Java 11+.

Use the following commands when working on Chronicle-Analytics:

- From the repository root, run a full quality pass for all modules:
  - `mvn -q clean verify`
- To focus on Chronicle-Analytics only:
  - `mvn -q -pl Chronicle-Analytics -am clean verify`

Notes:

- Checkstyle is configured via `root-parent-pom` and this module's
  `quality` profile to analyse both main and test sources.
- SpotBugs runs with `effort=Max`, `threshold=Low`, `includeTests=true`,
  and `failOnError=true` under the `quality` profile, so new findings in
  this module should be fixed rather than suppressed unless justified by
  the SpotBugs low-violation rules.
