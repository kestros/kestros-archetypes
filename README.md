# Kestros Archetypes

Maven archetypes for scaffolding new Kestros CMS projects.

## Purpose

`kestros-archetypes` provides Maven archetype templates that generate a complete Kestros CMS project structure. The generated project includes the standard module layout (core, content, frontend), POM configuration, and starter content needed to build a site on the Kestros platform.

## Installation & Build

**Maven coordinates:**

```
io.kestros.cms.archetypes:kestros-project-archetype
```

**Build the archetype:**

```bash
mvn clean install
```

## Configuration

No OSGi configuration is required. The archetype generates project scaffolding only.

### Starting a Kestros Instance

Before generating a project, start a Kestros instance:

```bash
docker run -p 8080:8080 --name kestros-platform-beta \
  -v /tmp/kestros/logs:/opt/sling/sling/logs \
  kestros/kestros-platform-beta:latest
```

## API / Service Usage

### Generating a New Project

Run the Maven archetype generator:

```bash
mvn archetype:generate \
  -DarchetypeGroupId=io.kestros.cms.archetypes \
  -DarchetypeArtifactId=kestros-project-archetype
```

You will be prompted for:

| Parameter | Description |
|-----------|-------------|
| `groupId` | Your project's Maven group ID |
| `artifactId` | Your project's Maven artifact ID |
| `version` | Initial version |
| `package` | Java package prefix |

### Generated Project Structure

The archetype generates:

```
my-site/
  core/           Java Sling Models, services, and servlets
  content/        JCR content definitions (pages, components, configs)
  frontend/       CSS, JavaScript, and HTL templates
  pom.xml         Reactor POM with all modules
```

### Installing the Generated Site

After generating and customizing your project:

```bash
mvn clean install -P installPackage,installBundle
```

This builds all modules and deploys both the OSGi bundle and JCR content package to the running Sling instance.

## Dependencies

**Depends on:**

| Module | Purpose |
|--------|---------|
| `kestros-cms-foundation` | Generated projects depend on foundation services |
| `kestros-sitebuilding-api` | Generated projects extend base site and page classes |
| `kestros-basic-components` | Generated projects include basic components |

**Depended on by:**

None -- this is a project generation tool.
