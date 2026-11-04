# Kestros Base Project

## Included

## Generating New Projects

```
mvn archetype:generate                                  \
  -DarchetypeGroupId=io.kestros.cms   \
  -DarchetypeArtifactId=kestros-project-archetype       \
  -DarchetypeVersion=0.9.0
```

## Installing Your Site

```
mvn clean install -P,installPackage,installBundle
```