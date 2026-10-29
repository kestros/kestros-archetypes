// Community Hub descriptor: checks the kestros.hub.* properties in the project pom and writes
// them, with the project version, to META-INF/kestros/community-hub.json in the built package.
//
// Run by gmavenplus twice: hubMode=check at validate (fails the build, one line per problem,
// each naming the property) and hubMode=write at prepare-package (writes the descriptor into
// filevault's work directory, which the package goal then zips).

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

def VERSION = /\d+\.\d+\.\d+(-SNAPSHOT)?/
def PLAIN_VERSION = /\d+\.\d+\.\d+/
def DEPENDENCY = /[A-Za-z0-9._-]+:(${PLAIN_VERSION}|\[${PLAIN_VERSION},${PLAIN_VERSION}\))/
def LICENSES = ['none', 'GPL-3.0', 'Apache-2.0', 'MIT']

// A -D on the command line wins over the pom, as it does for any other Maven property.
def prop = { String name ->
    (session.userProperties.getProperty(name) ?: project.properties.getProperty(name) ?: '').trim()
}

def id = prop('kestros.hub.id')
def title = prop('kestros.hub.title')
def description = prop('kestros.hub.description')
def tags = prop('kestros.hub.tags').split(',').collect { it.trim() }.findAll { it }
def dependencies = prop('kestros.hub.dependencies').split(/\s+/).findAll { it }
def license = prop('kestros.hub.license')
def version = project.version

def problems = []
if (!(license in LICENSES)) {
    problems << "kestros.hub.license \"${license}\" is not one of ${LICENSES.join(', ')}"
}
if (!id) {
    problems << 'kestros.hub.id is empty'
}
if (!title) {
    problems << 'kestros.hub.title is empty'
}
if (!tags.any { it.startsWith('type:') && it.length() > 'type:'.length() }) {
    problems << 'kestros.hub.tags has no type: tag'
}
if (!(version ==~ VERSION)) {
    problems << "project.version ${version} is not x.y.z or x.y.z-SNAPSHOT"
}
dependencies.findAll { !(it ==~ DEPENDENCY) }.each {
    problems << "kestros.hub.dependencies entry \"${it}\" is not packageId:<version> or packageId:[a,b)"
}

if (problems) {
    problems.each { log.error("Community Hub descriptor: ${it}") }
    throw new IllegalStateException("Community Hub descriptor is incomplete: " + problems.join('; '))
}

if (hubMode != 'write') {
    return
}

def json = { String s ->
    '"' + s.collect { c ->
        switch (c) {
            case '"': return '\\"'
            case '\\': return '\\\\'
            case '\n': return '\\n'
            case '\r': return '\\r'
            case '\t': return '\\t'
            default: return c < ' ' ? String.format('\\u%04x', (int) c.charAt(0)) : c
        }
    }.join('') + '"'
}
def list = { List values -> '[' + values.collect { json(it) }.join(',') + ']' }

def descriptor = '{' + [
        '"id":' + json(id),
        '"title":' + json(title),
        '"description":' + json(description),
        '"version":' + json(version),
        '"tags":' + list(tags),
        '"dependencies":' + list(dependencies),
        '"license":' + json(license)
].join(',') + '}\n'

// filevault's work directory: its package goal zips everything under META-INF here.
def entry = Paths.get(project.build.directory, 'vault-work', 'META-INF', 'kestros', 'community-hub.json')
Files.createDirectories(entry.parent)
Files.write(entry, descriptor.getBytes('UTF-8'), StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)
log.info("Community Hub descriptor written to ${entry}")
