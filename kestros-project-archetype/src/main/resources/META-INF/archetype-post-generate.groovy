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

