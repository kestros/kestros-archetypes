# BuildIntegrationTest API

This module contains the API for the BuildIntegrationTest Project. It contains object models,
service models, and exceptions. Implementation logic should be added to the core module.

## Included

* Sample Model
* Sample Service
* Sample Exception
* Sample Unit Tests

## Building and Installing

### Standard Build

* Compiles the project
* Runs unit tests

```
mvn clean install
```

### Mutation Testing

* Runs PIT Mutation Testing

```
mvn clean install -P,mutationTest
```

For more information regarding PIT Mutation Testing
see: [PIT Mutation Testing](hhttps://pitest.org/quickstart/maven/)

### Strict Build

* Runs Checkstyle
* Runs Unit Tests
* Checks coverage
* Runs SpotBugs

For more information on customizing these configurations see:

* [Checkstyle Configuration](https://maven.apache.org/plugins/maven-checkstyle-plugin/)
* [JaCoCo Configuration](https://www.eclemma.org/jacoco/trunk/doc/maven.html)
* [SpotBugs Configuration](https://spotbugs.github.io/spotbugs-maven-plugin/index.html)

```
mvn clean install -P,strict

```

### Skipping Tests

```
-DskipTests
```

### Skipping License Checks (Strict Build Only)

Since files are not generated with license headers, this will need to be skipped during the initial
build. It is recommended to add license headers to all files, or update the maven plugin to skip the
check.

Inline

```
-Drat.skip
```

pom.xml

```
<plugin>
    <groupId>org.apache.rat</groupId>
    <artifactId>apache-rat-plugin</artifactId>
    <configuration>
        <skip>true</skip>
    </configuration>
</plugin>
```

### Skipping Checkstyle (Strict Build Only)

inline

```
-Dcheckstyle.skip
```

pom.xml

```
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <configuration>
        <skip>true</skip>
    </configuration>
</plugin>
```

### Skipping SpotBugs (Strict Build Only)

inline

```
-Dspotbugs.skip=true
```

pom.xml

```
<plugin>
    <groupId>com.github.spotbugs</groupId>
    <artifactId>spotbugs-maven-plugin</artifactId>
    <configuration>
        <skip>true</skip>
    </configuration>
</plugin>
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