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


// rename gitignore file to .gitignore
def gitIgnoreFile = new File(generatedProjectDirectory + "/gitignore")
if (gitIgnoreFile.exists()) {
    gitIgnoreFile.renameTo(new File(generatedProjectDirectory + "/.gitignore"))
}


// copy original pom file to original-pom.xml
def pomFile = new File(generatedProjectDirectory + "/pom.xml")
def originalPomFile = new File(generatedProjectDirectory + "/original-pom.xml")
if (pomFile.exists()) {
    // run cp pom.xml original-pom.xml
    def command = "cp pom.xml original-pom.xml"
    command.execute(null, new File(generatedProjectDirectory))

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
def result = archetypeGenerate(generatedProjectDirectory, 'api', "kestros-api-archetype", '0.0.1-SNAPSHOT', groupId, artifactId, version, packageValue, artifactIdNoSpecialCharacters, artifactIdShorthand, artifactDescription, organizationName, artifactName, hasParentProject)
generatedProjectDirectory = request.outputDirectory + "/" + request.artifactId
checkForGitIgnoreAndRemove(generatedProjectDirectory, 'api')
resetPomFile(generatedProjectDirectory)


println "building core module"
generatedProjectDirectory = request.outputDirectory + "/" + request.artifactId
result = archetypeGenerate(generatedProjectDirectory, 'core', "kestros-core-archetype", '0.0.1-SNAPSHOT', groupId, artifactId, version, packageValue, artifactIdNoSpecialCharacters, artifactIdShorthand, artifactDescription, organizationName, artifactName, hasParentProject)
checkForGitIgnoreAndRemove(generatedProjectDirectory, 'core')
resetPomFile(generatedProjectDirectory)

println "building content module"
generatedProjectDirectory = request.outputDirectory + "/" + request.artifactId
result = archetypeGenerate(generatedProjectDirectory, 'content', "kestros-content-archetype", '0.0.1-SNAPSHOT', groupId, artifactId, version, packageValue, artifactIdNoSpecialCharacters, artifactIdShorthand, artifactDescription, organizationName, artifactName, hasParentProject)
checkForGitIgnoreAndRemove(generatedProjectDirectory, 'content')
resetPomFile(generatedProjectDirectory)

println "building application module"
generatedProjectDirectory = request.outputDirectory + "/" + request.artifactId
result = archetypeGenerate(generatedProjectDirectory, 'application', "kestros-application-archetype", '0.0.1-SNAPSHOT', groupId, artifactId, version, packageValue, artifactIdNoSpecialCharacters, artifactIdShorthand, artifactDescription, organizationName, artifactName, hasParentProject)
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

    def command = """mvn archetype:generate -DarchetypeGroupId=io.kestros.archetypes -DarchetypeArtifactId=$archetype -DarchetypeVersion=$archetypeVersion -DgroupId=$groupId -DartifactId=$artifactId -Dversion=$version -Dpackage=$packageValue -DartifactIdNoSpecialCharacters=$artifactIdNoSpecialCharacters -DartifactIdShorthand=$artifactIdShorthand -DartifactDescription=$artifactDescription -DorganizationName=$organizationName -DartifactName="$artifactName" -DhasParentProject=$hasParentProject -DinteractiveMode=false"""
    // run command from the generated project directory
    def result = command.execute(null, new File(generatedProjectDirectory)).text


    // look for folder matching the new generated project, rename to the directory name
    def newProjectDirectory = new File(generatedProjectDirectory + "/" + artifactId)
    if (newProjectDirectory.exists()) {
        newProjectDirectory.renameTo(new File(generatedProjectDirectory + "/" + directoryName))
    }
    // do an ls -la command
    return result
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
        // run cp original-pom.xml pom.xml
        def command = "cp original-pom.xml pom.xml"
        command.execute(null, new File(generatedProjectDirectory))
    }
}

