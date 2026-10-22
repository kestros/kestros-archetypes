// it5-gpl: a project generated with license=GPL-3.0 builds with the licence check on, writes the
// GPL text to LICENSE, stamps the header into every generated source file, and records the
// licence in the pom and in the community-hub descriptor.
import java.util.zip.ZipFile

def base = (binding.hasVariable('context') && context?.projectDir) ? new File(context.projectDir.toString()) : new File(basedir.toString())
def found = []
base.eachFileRecurse { if (it.name == 'build-integration-test-community-hub-0.0.1-SNAPSHOT.zip' && it.parentFile.name == 'target') found << it }
assert found.size() == 1 : "community-hub package not built under ${base}"

def zip = new ZipFile(found[0])
def entry = zip.getEntry('META-INF/kestros/community-hub.json')
assert entry != null : "META-INF/kestros/community-hub.json missing from ${found[0]}"
def json = zip.getInputStream(entry).getText('UTF-8').trim()
zip.close()
assert json.endsWith(',"license":"GPL-3.0"}') : "descriptor was ${json}"

def project = found[0].parentFile.parentFile.parentFile
def licenseFile = new File(project, 'LICENSE')
assert licenseFile.isFile() : 'license=GPL-3.0 wrote no LICENSE file'
assert licenseFile.getText('UTF-8').contains('GNU GENERAL PUBLIC LICENSE') : 'LICENSE is not the GPL text'
assert !new File(project, 'licenses').exists() : 'licenses/ directory left in the project'
assert new File(project, 'pom.xml').getText('UTF-8').contains('<kestros.hub.license>GPL-3.0</kestros.hub.license>') :
        'licence not saved in the project pom'

def missing = []
def checked = 0
project.eachFileRecurse(groovy.io.FileType.FILES) {
    if (!it.path.contains('/target/') && it.name ==~ /.*\.(java|groovy|js|css|less|scss|xml|html|properties|yml|yaml)/) {
        checked++
        if (!it.getText('UTF-8').take(2000).contains('GNU General Public License')) {
            missing << it.path
        }
    }
}
assert checked > 0 : 'no source files found to check'
assert missing.isEmpty() : "license=GPL-3.0, but these files have no header: ${missing}"
return true
