${hashSymbol} Build Integration Test Core

This module contains the Core service logic for the Build Integration Test Project. It contains
implemented object models, and OSGI services, but no usage of the services. Usage of the services,
such as components, jobs, or servlets, should be added to the application module.

${hashSymbol}${hashSymbol} Included

* Sample Model Implementation
* Sample Service Implementation
* Sample Unit Tests

${hashSymbol}${hashSymbol} Building and Installing

${hashSymbol}${hashSymbol}${hashSymbol} Standard Build

* Compiles the project
* Runs unit tests

```
mvn clean install
```

${hashSymbol}${hashSymbol}${hashSymbol} Strict Build

* Runs Checkstyle
* Runs Unit Tests
* Checks coverage
* Runs SpotBugs

```
mvn clean install -P,strict
```

${hashSymbol}${hashSymbol}${hashSymbol} Skipping Tests

```
-DskipTests
```

${hashSymbol}${hashSymbol}${hashSymbol} Skipping License Checks (Strict Build Only)

```
-Drat.skip
```

${hashSymbol}${hashSymbol}${hashSymbol} Install to Kestros

```
mvn clean install -P,installBundle
```

The following connection properties can be overwritten:

| Property       | Default Value |
|----------------|---------------|
| sling.host     | localhost     |
| sling.port     | 8080          |
| sling.protocol | http          |
| sling.user     | admin         |
| sling.password | admin         |


