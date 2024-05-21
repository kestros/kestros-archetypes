# Build Integration Test API

This module contains the API for the Build Integration Test Project. It contains object models,
service models, and exceptions. Implementation logic should be added to the core module.

${hashSymbol}${hashSymbol} Included

* Sample Model
* Sample Service
* Sample Exception
* Sample Unit Test

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


