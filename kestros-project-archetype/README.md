# Kestros Base Project

## Included

## Generating New Projects

```
mvn archetype:generate                                  \
  -DarchetypeGroupId=io.kestros.cms.archetypes   \
  -DarchetypeArtifactId=kestros-project-archetype       \
  -DarchetypeVersion=0.4.0-SNAPSHOT
```

## Installing Your Site

```
mvn clean install -P,installPackage,installBundle
```