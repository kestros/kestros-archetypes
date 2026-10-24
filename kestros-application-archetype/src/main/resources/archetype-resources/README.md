${hashSymbol} ${artifactName} Application

This module contains the application layer for the ${artifactName} Project. It ties the interfaces from the api module and the services from the core module into a running site,
adding the sample UI Libraries and UI Frameworks, the sample component (its Sling Model, edit dialog,
and views), and the page and site templates.

${hashSymbol}${hashSymbol} Included

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

${hashSymbol}${hashSymbol} Two ways to attach a view

A component view can be attached in one of two ways, and this project ships a working example of each.

| Scope | Attached by | Example in this project |
|-------|-------------|-------------------------|
| UI Framework | the framework's `kes:uiFrameworkCode` | `sample-component/${artifactIdShorthand}-ui` and `sample-component/${artifactIdShorthand}-versioned-ui` |
| Vendor Library | the library's `kes:libraryCode` | `apps/kestros/commons/components/content/text/${artifactIdShorthand}-versioned-lib/versions/0.0.1` |

A framework-scoped view belongs to a single framework's look. A library-scoped view travels with a
vendor library, so every framework that includes that library picks the view up.

Resolution is framework-first. Where a component has a view under the current framework's code, that
view wins and a library-scoped view for the same component is never reached. That is why the
library-scoped example is attached to a core Kestros component rather than to `sample-component`,
which already ships views for both of this project's framework codes.

The library-scoped example is carried by the VERSIONED library (`${artifactId}-versioned-library`,
library code `${artifactIdShorthand}-versioned-lib`), which is the library the versioned framework
includes. Under the unversioned framework the core Text component still renders its `common` view —
that is correct behaviour, not a broken example. To see the library-scoped view render, switch the
site's theme to one belonging to `${artifactId}-versioned-framework`.

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

${hashSymbol}${hashSymbol} Run-mode Configuration

OSGi configurations that differ per environment live in run-mode install folders:

* `src/content/jcr_root/apps/${artifactId}/install.dev/` is applied only on an instance running the `dev` run mode
* `src/content/jcr_root/apps/${artifactId}/install.prod/` is applied only on an instance running the `prod` run mode

Each folder holds a sample logger config, `org.apache.sling.commons.log.LogManager.factory.config~${artifactId}.cfg.json`,
that writes the `${package}` logger to `logs/${artifactId}.log`: DEBUG in `install.dev`, WARN in `install.prod`.

Start an instance in the `dev` run mode with:

```
-Dsling.run.modes=dev
```


${hashSymbol}${hashSymbol} Development Guide


