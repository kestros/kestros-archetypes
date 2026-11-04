// it4-bad-values: a bad version, no type: tag and an unparseable dependency each fail the
// community-hub build with a message naming the property.
def base = (binding.hasVariable('context') && context?.projectDir) ? new File(context.projectDir.toString()) : new File(basedir.toString())
def files = []
base.eachFileRecurse { files << it }
def log = files.find { it.name == 'build.log' }
assert log != null : "no build.log under ${base}"
def text = log.getText('UTF-8')

assert text.contains('project.version 1.0 is not x.y.z or x.y.z-SNAPSHOT') : 'bad version not reported'
assert text.contains('kestros.hub.tags has no type: tag') : 'missing type: tag not reported'
assert text.contains('kestros.hub.dependencies entry "bad" is not packageId:<version> or packageId:[a,b)') : 'bad dependency not reported'
assert !text.contains('kestros.hub.id is empty') : 'a set id was reported'
assert !text.contains('kestros.hub.title is empty') : 'a set title was reported'

def zips = files.findAll { it.parentFile.name == 'target' && it.name.endsWith('.zip') }*.name
assert !zips.any { it.startsWith('build-integration-test-community-hub') } : "community-hub packaged anyway: ${zips}"
return true
