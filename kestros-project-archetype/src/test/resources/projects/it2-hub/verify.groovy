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
        '"version":"0.0.1-SNAPSHOT","tags":["type:site"],"dependencies":["other:1.0.0","lib:2.0.0"]}'
assert json == expected : "descriptor was ${json}"
return true
