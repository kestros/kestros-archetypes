generatedProjectDirectory = request.outputDirectory + "/" + request.artifactId

groupId = request.groupId
version = request.version
packageValue = request.package
artifactId = request.artifactId
artifactIdNoSpecialCharacters = request.properties['artifactIdNoSpecialCharacters']
artifactIdShorthand = request.properties['artifactIdShorthand']
artifactDescription = request.properties['artifactDescription']
organizationName = request.properties['organizationName']
artifactName = request.properties['artifactName']
hasParentProject = "true"

// The submodule Java package is driven by ${package}. When the user does not pass -Dpackage
// (Maven then defaults request.package to the groupId), derive the conventional default
// groupId.artifactIdNoSpecialCharacters so the generated layout is unchanged. A user-supplied
// -Dpackage (anything other than the bare groupId) is honored verbatim. Note: Maven cannot
// distinguish an omitted -Dpackage from an explicit -Dpackage=<groupId> (both arrive as
// request.package == groupId), so a package deliberately set equal to the groupId resolves to
// the derived default rather than staying as the bare groupId.
if (packageValue == null || packageValue.trim().isEmpty() || packageValue == groupId) {
    packageValue = "${groupId}.${artifactIdNoSpecialCharacters}"
}


// rename gitignore file to .gitignore
def gitIgnoreFile = new File(generatedProjectDirectory + "/gitignore")
if (gitIgnoreFile.exists()) {
    gitIgnoreFile.renameTo(new File(generatedProjectDirectory + "/.gitignore"))
}


// copy original pom file to original-pom.xml
def pomFile = new File(generatedProjectDirectory + "/pom.xml")
def originalPomFile = new File(generatedProjectDirectory + "/original-pom.xml")
if (pomFile.exists()) {
    copyFile(pomFile, originalPomFile)
}

// make sure pom.xml and original-pom.xml exist

pomFile = new File(generatedProjectDirectory + "/pom.xml")
originalPomFile = new File(generatedProjectDirectory + "/original-pom.xml")

if (pomFile.exists() && originalPomFile.exists()) {
    println "pom.xml and original-pom.xml exist"
} else {
    println "pom.xml or original-pom.xml does not exist"
    return
}


// generate kestros-api-archetype
generatedProjectDirectory = request.outputDirectory + "/" + request.artifactId

println "building api module"
def result = archetypeGenerate(generatedProjectDirectory, 'api', "kestros-api-archetype", '0.9.1-SNAPSHOT', groupId, artifactId, version, packageValue, artifactIdNoSpecialCharacters, artifactIdShorthand, artifactDescription, organizationName, artifactName, hasParentProject)
generatedProjectDirectory = request.outputDirectory + "/" + request.artifactId
checkForGitIgnoreAndRemove(generatedProjectDirectory, 'api')
resetPomFile(generatedProjectDirectory)


println "building core module"
generatedProjectDirectory = request.outputDirectory + "/" + request.artifactId
result = archetypeGenerate(generatedProjectDirectory, 'core', "kestros-core-archetype", '0.9.1-SNAPSHOT', groupId, artifactId, version, packageValue, artifactIdNoSpecialCharacters, artifactIdShorthand, artifactDescription, organizationName, artifactName, hasParentProject)
checkForGitIgnoreAndRemove(generatedProjectDirectory, 'core')
resetPomFile(generatedProjectDirectory)

println "building content module"
generatedProjectDirectory = request.outputDirectory + "/" + request.artifactId
result = archetypeGenerate(generatedProjectDirectory, 'content', "kestros-content-archetype", '0.9.1-SNAPSHOT', groupId, artifactId, version, packageValue, artifactIdNoSpecialCharacters, artifactIdShorthand, artifactDescription, organizationName, artifactName, hasParentProject)
checkForGitIgnoreAndRemove(generatedProjectDirectory, 'content')
resetPomFile(generatedProjectDirectory)

println "building application module"
generatedProjectDirectory = request.outputDirectory + "/" + request.artifactId
result = archetypeGenerate(generatedProjectDirectory, 'application', "kestros-application-archetype", '0.9.1-SNAPSHOT', groupId, artifactId, version, packageValue, artifactIdNoSpecialCharacters, artifactIdShorthand, artifactDescription, organizationName, artifactName, hasParentProject)
checkForGitIgnoreAndRemove(generatedProjectDirectory, 'application')
replacePomFile(generatedProjectDirectory)

println "applying licence: ${request.properties['license'] ?: 'none'}"
applyLicense(new File(request.outputDirectory + "/" + request.artifactId), request.properties['license'],
        organizationName, java.time.Year.now().value)


def archetypeGenerate(generatedProjectDirectory, directoryName, archetype, archetypeVersion, groupId, artifactId, version, packageValue, artifactIdNoSpecialCharacters, artifactIdShorthand, artifactDescription, organizationName, artifactName, hasParentProject) {
//    println generatedProjectDirectory
//    // run shell command, from the generated project directory
//    println "groupId: $groupId"
//    println "artifactId: $artifactId"
//    println "version: $version"
//    println "package: $packageValue"
//    println "artifactIdNoSpecialCharacters: $artifactIdNoSpecialCharacters"
//    println "artifactName: $artifactName"
//    println "artifactDescription: $artifactDescription"
//    println "organizationName: $organizationName"
//    println "hasParentProject: $hasParentProject"

    def mvn = mavenExecutable(System.getProperty("maven.home"), System.getProperty("os.name"))

    // update/download dependency using dependency plugin, unless the version says not to.
    // See dependencyGetCommandFor for which version is on which path.
    def dependencyCommand = dependencyGetCommandFor(mvn, archetype, archetypeVersion)
    if (dependencyCommand == null) {
        println "Skipping dependency:get for SNAPSHOT archetype io.kestros.cms:$archetype:$archetypeVersion"
        println "  A SNAPSHOT is not published to Maven Central. It has to be installed in the local"
        println "  repository first - 'mvn install' on the archetype modules, in the order api, core,"
        println "  content, application, project."
    } else {
        println dependencyCommand.join(" ")
        def dependencyResult = dependencyCommand.execute(null, new File(generatedProjectDirectory)).text

        // print the result
        println dependencyResult

        // check that the dependency was downloaded
        if (dependencyResult.contains("BUILD SUCCESS")) {
            println "Dependency downloaded: io.kestros.cms:$archetype:$archetypeVersion"
        } else {
            println "Error downloading dependency: io.kestros.cms:$archetype:$archetypeVersion"
            throw new Exception("Error downloading dependency: io.kestros.cms:$archetype:$archetypeVersion")
        }
    }

    println "Generating archetype: $archetype"
    // Pass each argument as its own list element so metadata values that contain spaces
    // (artifactName / artifactDescription / organizationName) survive intact. The old
    // single-string form was tokenized on whitespace by String.execute(), which forced a
    // replaceAll("\\s","") strip that deleted spaces from the generated jcr:title values.
    def command = [mvn, "archetype:generate",
                   "-DarchetypeGroupId=io.kestros.cms",
                   "-DarchetypeArtifactId=${archetype}".toString(),
                   "-DarchetypeVersion=${archetypeVersion}".toString(),
                   "-DgroupId=${groupId}".toString(),
                   "-DartifactId=${artifactId}".toString(),
                   "-Dversion=${version}".toString(),
                   "-Dpackage=${packageValue}".toString(),
                   "-DartifactIdNoSpecialCharacters=${artifactIdNoSpecialCharacters}".toString(),
                   "-DartifactIdShorthand=${artifactIdShorthand}".toString(),
                   "-DartifactDescription=${artifactDescription}".toString(),
                   "-DorganizationName=${organizationName}".toString(),
                   "-DartifactName=${artifactName}".toString(),
                   "-DhasParentProject=${hasParentProject}".toString(),
                   "-DinteractiveMode=false"]
    println command.join(" ")
    // run command from the generated project directory
    def result = command.execute(null, new File(generatedProjectDirectory)).text
    if (result.contains("BUILD SUCCESS")) {
        println "Archetype generated: $archetype"
    } else {
        println result
        // For a SNAPSHOT that was never installed locally this is where the failure lands, and that
        // is intended. Skipping dependency:get above does not make a missing SNAPSHOT resolvable; it
        // only stops the script failing earlier for the wrong reason. The fix for a developer who
        // sees this is 'mvn install' on the archetype modules, not another change here - a snapshot
        // remote does not belong in a script that ships inside a released artifact.
        throw new Exception("Error generating archetype: $archetype")
    }


    // look for folder matching the new generated project, rename to the directory name
    println "Renaming $generatedProjectDirectory/$artifactId to $generatedProjectDirectory/$directoryName"
    def newProjectDirectory = new File(generatedProjectDirectory + "/" + artifactId)
    if (newProjectDirectory.exists()) {
        newProjectDirectory.renameTo(new File(generatedProjectDirectory + "/" + directoryName))
    }
    checkArchetypeWasBuilt(generatedProjectDirectory, directoryName)
    // do an ls -la command
    return result
}

// The command that fetches an archetype before it is generated, or null when there is nothing to
// fetch. Two paths, and the archetype version alone decides which one you are on.
//
// RELEASED version - the path every end user is on. The archetype is on Maven Central, so it is
// fetched up front and the caller fails loudly and early if it is not there, which is a clearer
// error than whatever archetype:generate would report about it further down.
//
// -SNAPSHOT version - the developer path, and it must NOT fetch. A SNAPSHOT is never published to
// Central, so dependency:get always failed and this script threw before it reached generation at
// all. A SNAPSHOT has to be installed in the local repository first instead.
//
// Kept out of archetypeGenerate so the decision can be tested without launching Maven.
//
// Each argument is its own list element, as with the generate command below, so a Maven path
// containing a space is not torn in half by String.execute()'s whitespace tokenizing.
def dependencyGetCommandFor(mavenExecutable, archetype, archetypeVersion) {
    if (archetypeVersion != null && archetypeVersion.toString().endsWith("-SNAPSHOT")) {
        return null
    }
    return [mavenExecutable, "dependency:get",
            "-Dartifact=io.kestros.cms:${archetype}:${archetypeVersion}".toString(),
            "-Dtransative=false", "-U"]
}

// The Maven binary to fork with.
//
// `mvn` on PATH is not a safe assumption. This script runs inside a Maven JVM, but a process it
// forks inherits the launcher's environment, and there are ordinary setups where that PATH holds
// no mvn at all - a Jenkins Maven tool installation invokes it by absolute path, and so do the
// Maven wrapper and most IDEs. When that happens the fork dies with "Cannot run program mvn"
// before it can generate anything.
//
// Maven sets maven.home on its own JVM, so take the binary from there when it is there, and fall
// back to PATH when it is not. Arguments rather than System.getProperty calls so the choice is
// testable.
def mavenExecutable(mavenHome, osName) {
    if (mavenHome != null && !mavenHome.toString().trim().isEmpty()) {
        def windows = osName != null && osName.toString().toLowerCase().contains("windows")
        def candidate = new File(new File(mavenHome.toString(), "bin"), windows ? "mvn.cmd" : "mvn")
        if (candidate.isFile()) {
            return candidate.absolutePath
        }
    }
    return "mvn"
}

def checkArchetypeWasBuilt(generatedProjectDirectory, directoryName) {
    def newProjectDirectory = new File(generatedProjectDirectory + "/" + directoryName)
    if (newProjectDirectory.exists()) {
        println "Finished building archetype: $directoryName"
    } else {
        // throw an error
        println "Error building archetype: $directoryName"
        throw new Exception("Error building archetype: $directoryName")
    }
}

def checkForGitIgnoreAndRemove(generatedProjectDirectory, artifactId) {
    def artifactDirectory = new File(generatedProjectDirectory + "/" + artifactId)
    def gitIgnoreFile = new File(artifactDirectory, ".gitignore")
    if (gitIgnoreFile.exists()) {
        gitIgnoreFile.delete()
    }
}

def replacePomFile(generatedProjectDirectory) {
    def pomFile = new File(generatedProjectDirectory + "/pom.xml")
    def pomFileWithModules = new File(generatedProjectDirectory + "/pom-with-modules.xml")
    if (pomFile.exists()) {
        pomFile.delete()
        // rename pom-with-modules.xml to pom.xml
        pomFileWithModules.renameTo(pomFile)
    }
    def originalPomFile = new File(generatedProjectDirectory + "/original-pom.xml")
    if (originalPomFile.exists()) {
        originalPomFile.delete();
    }
}

def resetPomFile(generatedProjectDirectory) {
    // remove pom.xml
    def pomFile = new File(generatedProjectDirectory + "/pom.xml")
    if (pomFile.exists()) {
        pomFile.delete()
    }
    // copy original-pom.xml to pom.xml
    def originalPomFile = new File(generatedProjectDirectory + "/original-pom.xml")
    if (originalPomFile.exists()) {
        copyFile(originalPomFile, pomFile)
    }
}

// The licence the project was generated with. We do not choose it for the customer (Danny,
// 2026-10-22): `none` writes no header and no LICENSE file; any other licence stamps its header
// into every generated source file and writes the full text to LICENSE. The licences/ directory
// the archetype ships the texts in is removed either way.
def applyLicense(projectDirectory, license, holder, year) {
    license = (license ?: 'none').toString().trim()
    def licenceTexts = new File(projectDirectory, 'licenses')
    try {
        if (license == 'none') {
            return
        }
        def text = new File(licenceTexts, "${license}.txt")
        if (!text.isFile()) {
            throw new Exception("Unknown licence: ${license} (expected none, GPL-3.0, Apache-2.0 or MIT)")
        }
        def holderName = (holder ?: '').toString().trim() ?: 'the authors'
        new File(projectDirectory, 'LICENSE').setText(text.getText('UTF-8')
                .replace('[year]', year.toString()).replace('[fullname]', holderName), 'UTF-8')
        def header = licenseHeader(license, holderName, year)
        projectDirectory.eachFileRecurse(groovy.io.FileType.FILES) { file ->
            if (!file.path.contains("${File.separator}target${File.separator}")
                    && !file.path.startsWith(licenceTexts.path)) {
                stampHeader(file, header)
            }
        }
    } finally {
        licenceTexts.deleteDir()
    }
}

// The standard notice for each licence, one line per element, without comment markers.
def licenseHeader(license, holder, year) {
    def copyright = "Copyright (C) ${year} ${holder}".toString()
    switch (license) {
        case 'GPL-3.0':
            return [copyright, '',
                    'This program is free software: you can redistribute it and/or modify',
                    'it under the terms of the GNU General Public License as published by',
                    'the Free Software Foundation, either version 3 of the License, or',
                    '(at your option) any later version.', '',
                    'This program is distributed in the hope that it will be useful,',
                    'but WITHOUT ANY WARRANTY; without even the implied warranty of',
                    'MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the',
                    'GNU General Public License for more details.', '',
                    'You should have received a copy of the GNU General Public License',
                    'along with this program.  If not, see <https://www.gnu.org/licenses/>.']
        case 'Apache-2.0':
            return [copyright, '',
                    'Licensed under the Apache License, Version 2.0 (the "License");',
                    'you may not use this file except in compliance with the License.',
                    'You may obtain a copy of the License at', '',
                    '    http://www.apache.org/licenses/LICENSE-2.0', '',
                    'Unless required by applicable law or agreed to in writing, software',
                    'distributed under the License is distributed on an "AS IS" BASIS,',
                    'WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.',
                    'See the License for the specific language governing permissions and',
                    'limitations under the License.']
        case 'MIT':
            return [copyright, '',
                    'Permission is hereby granted, free of charge, to any person obtaining a copy',
                    'of this software and associated documentation files (the "Software"), to deal',
                    'in the Software without restriction, subject to the conditions in the LICENSE',
                    'file at the root of this project.']
        default:
            throw new Exception("Unknown licence: ${license}")
    }
}

// Files that take a header, by the comment style they use. Anything else (json, md, images)
// is left alone: it has no comment syntax, or is not source.
def headerStyleFor(file) {
    def name = file.name.toLowerCase()
    def ext = name.contains('.') ? name.substring(name.lastIndexOf('.') + 1) : ''
    if (ext in ['java', 'groovy', 'js', 'ts', 'css', 'less', 'scss']) {
        return 'block'
    }
    if (ext in ['xml', 'html', 'htm']) {
        return 'markup'
    }
    if (ext in ['properties', 'yml', 'yaml', 'sh', 'cfg']) {
        return 'hash'
    }
    return null
}

// Put the header at the top of one file, after an XML declaration or a shebang if there is one.
// Returns whether the file took a header.
def stampHeader(file, header) {
    def style = headerStyleFor(file)
    if (style == null) {
        return false
    }
    def comment
    if (style == 'block') {
        comment = '/*\n' + header.collect { it ? " * ${it}" : ' *' }.join('\n') + '\n */\n'
    } else if (style == 'markup') {
        comment = '<!--\n' + header.collect { it ? "  ${it}" : '' }.join('\n') + '\n-->\n'
    } else {
        comment = header.collect { it ? "# ${it}" : '#' }.join('\n') + '\n'
    }
    def content = file.getText('UTF-8')
    def prologue = ''
    def matcher = content =~ /^(<\?xml[^>]*\?>\s*\n|#![^\n]*\n)/
    if (matcher.find()) {
        prologue = matcher.group(1)
        content = content.substring(prologue.length())
    }
    file.setText(prologue + comment + content, 'UTF-8')
    return true
}

// Copy a file and do not return until it is there.
//
// This used to shell out - "cp a b".execute(...) - and nothing ever waited on the process.
// String.execute() returns as soon as the fork happens, so every caller carried on against a file
// that might or might not exist yet. The observed failure was the existence check below the first
// copy: when cp had not finished, it reported "pom.xml or original-pom.xml does not exist" and the
// script returned before generating any module. resetPomFile had the same shape, which leaves
// pom.xml missing for the next generation and for replacePomFile.
//
// A java.nio copy has finished when it returns, which removes both. It also drops a dependency on
// a `cp` binary from a script that runs on whatever machine an end user generates a project on.
def copyFile(source, target) {
    java.nio.file.Files.copy(source.toPath(), target.toPath(),
            java.nio.file.StandardCopyOption.REPLACE_EXISTING)
}

