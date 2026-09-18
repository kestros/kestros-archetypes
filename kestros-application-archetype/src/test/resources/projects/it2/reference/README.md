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
      * Framework-scoped Views (Versioned and Unversioned)
        * Layouts
        * Variations
          * Grouped
          * Standalone
          * Inline
* Library-scoped View on a Kestros Core Component
  * A view for the core Text component, supplied by the versioned Sample Library

## Two ways to attach a view

A component view can be attached in one of two ways, and this project ships a working example of each.

| Scope | Attached by | Example in this project |
|-------|-------------|-------------------------|
| UI Framework | the framework's `kes:uiFrameworkCode` | `sample-component/bit2-ui` and `sample-component/bit2-versioned-ui` |
| Vendor Library | the library's `kes:libraryCode` | `apps/kestros/commons/components/content/text/bit2-versioned-lib/versions/0.0.1` |

A framework-scoped view belongs to a single framework's look. A library-scoped view travels with a
vendor library, so every framework that includes that library picks the view up.

Resolution is framework-first. Where a component has a view under the current framework's code, that
view wins and a library-scoped view for the same component is never reached. That is why the
library-scoped example is attached to a core Kestros component rather than to `sample-component`,
which already ships views for both of this project's framework codes.

The library-scoped example is carried by the VERSIONED library (`build-integration-test2-versioned-library`,
library code `bit2-versioned-lib`), which is the library the versioned framework
includes. Under the unversioned framework the core Text component still renders its `common` view —
that is correct behaviour, not a broken example. To see the library-scoped view render, switch the
site's theme to one belonging to `build-integration-test2-versioned-framework`.

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


