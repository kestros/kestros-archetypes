# Kestros Archetypes

## Starting Kestros
```
docker run -p 8080:8080 --name kestros-platform-beta -v /tmp/kestros/logs:/opt/sling/sling/logs kestros/kestros-platform-beta:latest
```

## Generating New Projects
```
mvn archetype:generate                                  \
  -DarchetypeGroupId=io.kestros.cms.archetypes   \
  -DarchetypeArtifactId=kestros-project-archetype       \
  -DarchetypeVersion=0.1.0-SNAPSHOT
```

## Installing Your Site
```
mvn clean install -P,installPackage,installBundle
```