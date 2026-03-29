# Kestros Project Archetype

Maven archetype for generating a new Kestros CMS project with working component, datasource, dialog, and service examples.

## Generating a New Project

```bash
mvn archetype:generate \
  -DarchetypeGroupId=io.kestros.cms \
  -DarchetypeArtifactId=kestros-project-archetype \
  -DarchetypeVersion=0.5.3-SNAPSHOT
```

You will be prompted for:

| Property | Description |
|---|---|
| `groupId` | Maven group ID (e.g. `com.example`) |
| `artifactId` | Maven artifact ID (e.g. `my-site`) |
| `version` | Initial version (default: `1.0-SNAPSHOT`) |
| `artifactName` | Display name for the project |
| `artifactDescription` | Short description |
| `organizationName` | Your organization name |
| `artifactIdShorthand` | Short abbreviation used for framework prefixes (e.g. `ms` for `my-site`) |
| `artifactIdNoSpecialCharacters` | Artifact ID without hyphens, used for Java packages (e.g. `mysite`) |

## What's Included

The generated project contains the following modules:

| Module | Purpose |
|---|---|
| `api` | Model interfaces, service interfaces, and custom exceptions |
| `core` | Service implementations with current OSGi patterns |
| `application` | Sling components, datasources, dialog definitions, templates, and UI frameworks |
| `content` | Initial site content |

### Sample Component

A working sample component is included with:

- **KDF dialog fields**: textfield, selectfield, and checkbox examples using `kestros/cms2/fields` resource types
- **Datasource examples**: a static content datasource (JSON + Java class) and a child-pages datasource with pathfield dialog
- **HTL template**: common content view using Sling Models with `data-sly-use`
- **Unit tests**: Sling mock-based tests for all component properties

### Sample Service

An OSGi service implementation following current patterns:

- `@Component` / `@Activate` / `@Deactivate` annotations
- SLF4J logging
- Clean interface/implementation separation across api and core modules

## Building and Installing

```bash
mvn clean install -P,installBundle,installPackage
```

### Connection Properties

| Property | Default |
|---|---|
| `sling.host` | `localhost` |
| `sling.port` | `8080` |
| `sling.protocol` | `http` |
| `sling.user` | `admin` |
| `sling.password` | `admin` |
