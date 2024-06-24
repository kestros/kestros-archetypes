generatedProjectDirectory = request.outputDirectory + "/" + request.artifactId

groupId = request.groupId
version = request.version
packageValue = request.package
artifactId = request.artifactId
artifactIdNoSpecialCharacters = request.properties['artifactIdNoSpecialCharacters']
artifactDescription = request.properties['artifactDescription']
organizationName = request.properties['organizationName']
artifactName = request.properties['artifactName']
hasParentProject = request.properties['hasParentProject']

// if it has a parent project, delete .gitignore
if (hasParentProject == 'true') {
    new File(generatedProjectDirectory + "/gitignore").delete()
} else {
    // rename gitignore to .gitignore
    new File(generatedProjectDirectory + "/gitignore").renameTo(new File(generatedProjectDirectory + "/.gitignore"))
}
