# Kestros Project Archetype

## Installation

```
mvn clean install -P,installBundle
```

```
mvn clean install -P,installBundle,installContent
```

### Available Property Overrides

`-Dsling.protocol=` Set the installation protocol. Default http
`-Dsling.host=` Set the installation IP/hostname. Default localhost
`-Dsling.port=` Set the installation port. Default 8080

## Modules

Kestros Project Archetype comes with the following modules. Remove any unused/unnecessary modules as needed.

### Administation

Includes any Java classes, Kestros Components Types, Dialog Fields, and content resources associated with custom site
administration features.

### API

Contains any data model interfaces, which are implemented by Administration, Application and Core modules.

### Application

Generally contains application level component types/views, libraries, UI frameworks, configurations and component Sling
Models.

#### Vendor Libraries

Two Vendor Libraries are included, one which is versioned and the other which does not allow versioning. These libraries
contain sample CSS and Javscript.

[Versioning Vendor Libraries]

#### UI Frameworks

Two UI Frameworks are included, one which is versioned and the other which does not allow versioning. UI Frameworks
compile Vendor Libraries to provide CSS, JS and HTL Templates to sites/applications.

[Building UI Frameworks]

#### Templates

Project structure comes with a base page template, which does not include any included components by default.

[Creating new templates]

#### Sample Component Types

[Building New Component Types]

[Adding View to Existing Component Types]

[Extending Existing Component Types]

### Content

Base content installation.

### Core

Generally contains background OSGI Services, which can run when the application module is not installed.
