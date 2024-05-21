# Build Integration Test API

This module contains the Core service logic for the Build Integration Test Project. It contains
implemented object models, and OSGI services, but no usage of the services. Usage of the services,
such as components, jobs, or servlets, should be added to the application module.

## Included

* Sample Model Implementation
* Sample Service Implementation
* Sample Unit Tests

## Building and Installing

### Standard Build

* Compiles the project
* Runs unit tests

```
mvn clean install
```

### Strict Build

* Runs Checkstyle
* Runs Unit Tests
* Checks coverage
* Runs SpotBugs

```
mvn clean install -P,strict
```

### Skipping Tests

```
-DskipTests
```

### Skipping License Checks (Strict Build Only)

```
-Drat.skip
```

### Install to Kestros

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


