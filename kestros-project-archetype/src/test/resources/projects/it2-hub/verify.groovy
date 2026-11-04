// it2-hub: a project generated with hub properties builds, and its community-hub package carries
// the descriptor with every field.
import java.util.zip.ZipFile

def base = (binding.hasVariable('context') && context?.projectDir) ? new File(context.projectDir.toString()) : new File(basedir.toString())
def found = []
base.eachFileRecurse { if (it.name == 'build-integration-test-community-hub-0.0.1-SNAPSHOT.zip' && it.parentFile.name == 'target') found << it }
assert found.size() == 1 : "community-hub package not built under ${base}"

def zip = new ZipFile(found[0])
def entry = zip.getEntry('META-INF/kestros/community-hub.json')
assert entry != null : "META-INF/kestros/community-hub.json missing from ${found[0]}"
def json = zip.getInputStream(entry).getText('UTF-8').trim()
assert zip.entries().toList().any { it.name.endsWith('build-integration-test-application-0.0.1-SNAPSHOT.zip') } : 'application package not embedded'
assert zip.entries().toList().any { it.name.endsWith('build-integration-test-content-0.0.1-SNAPSHOT.zip') } : 'content package not embedded'
zip.close()

def expected = '{"id":"build-it","title":"Build IT","description":"Built by the archetype integration test",' +
        '"version":"0.0.1-SNAPSHOT","tags":["type:site"],"dependencies":["other:1.0.0","lib:2.0.0"],' +
        '"license":"none"}'
assert json == expected : "descriptor was ${json}"

// license=none: no LICENSE file, no licences/ directory left behind, and no generated source
// file carries a licence header.
def project = found[0].parentFile.parentFile.parentFile
assert !new File(project, 'LICENSE').exists() : 'license=none wrote a LICENSE file'
assert !new File(project, 'licenses').exists() : 'licenses/ directory left in the project'
def headed = []
def checked = 0
project.eachFileRecurse(groovy.io.FileType.FILES) {
    // the project itself sits under the archetype's target/, so skip build output by its path
    // inside the project
    def inBuildOutput = project.toPath().relativize(it.toPath()).any { it.toString() == 'target' }
    if (!inBuildOutput && it.name ==~ /.*\.(java|groovy|js|css|less|scss|xml|html|properties|yml|yaml)/) {
        checked++
        def head = it.getText('UTF-8').take(2000)
        if (head.contains('GNU General Public License') || head.contains('Licensed under the Apache License')
                || head.contains('Permission is hereby granted')) {
            headed << it.path
        }
    }
}
assert checked > 0 : 'no source files found to check'
assert headed.isEmpty() : "license=none, but these files carry a licence header: ${headed}"
return true
