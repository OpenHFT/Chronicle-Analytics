# Chronicle-Analytics – Local Quality Knobs

This module inherits the shared Chronicle quality configuration via
`net.openhft:java-parent-pom` → `net.openhft:root-parent-pom`. Use the
following profiles and commands when working on Chronicle-Analytics:

- `-P quality` – from the repository root, runs Checkstyle and SpotBugs
  for all modules:
  - `mvn -P quality clean verify`
- `-P sonar` – from the repository root, runs tests with JaCoCo coverage
  and Sonar wiring:
  - `mvn -P sonar clean verify`
- `-P module-quality` – from this module (or the root), focuses on
  Chronicle-Analytics only, wiring SpotBugs with the low-violation rules:
  - `mvn -pl Chronicle-Analytics -am -P module-quality clean verify`

Notes:

- Checkstyle is configured via `root-parent-pom` and this module's POM to
  analyse both main and test sources.
- SpotBugs runs with `effort=Max`, `threshold=Low`, `includeTests=true`,
  and `failOnError=true` under `module-quality`, so new findings in this
  module should be fixed rather than suppressed unless justified by the
  SpotBugs low-violation rules.

