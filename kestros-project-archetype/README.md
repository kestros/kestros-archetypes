# Kestros Base Project

## Included

## Generating New Projects

```
mvn archetype:generate                                  \
  -DarchetypeGroupId=io.kestros.cms.archetypes   \
  -DarchetypeArtifactId=kestros-project-archetype       \
  -DarchetypeVersion=0.2.3
```

## Installing Your Site

```
mvn clean install -P,installPackage,installBundle
```