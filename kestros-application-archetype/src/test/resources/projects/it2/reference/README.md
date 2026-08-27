# Build Integration Test2 Application

This module contains the application layer for the Build Integration Test2 Project. It ties the interfaces from the api module and the services from the core module into a running site,
adding the sample UI Libraries and UI Frameworks, the sample component (its Sling Model, edit dialog,
and views), and the page and site templates.

## Included

* Sample Libraries
    * Versioned
    * Unversioned
* Sample UI Frameworks
    * Versioned
    * Unversioned
* Sample Component Implementation
  * Bundle
    * Component Sling Model
    * Validation
  * Content Package
    * Edit Dialog
    * Views 
      * Common View
      * Libraries Views (Versioned and Unversioned)
        * Layouts
        * Variations
          * Grouped
          * Standalone
          * Inline
      * Frameworks (Versioned and Unversioned)
        * Layouts
        * Variations
          * Grouped
          * Standalone
          * Inline

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
mvn clean install -P,installPackage
```

The following connection properties can be overwritten:

| Property       | Default Value |
|----------------|---------------|
| sling.host     | localhost     |
| sling.port     | 8080          |
| sling.protocol | http          |
| sling.user     | admin         |
| sling.password | admin         |


## Development Guide


