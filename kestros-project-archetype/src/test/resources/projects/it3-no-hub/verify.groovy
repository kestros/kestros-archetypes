// it3-no-hub: a project generated without hub properties still builds its other modules; only
// community-hub fails, naming each missing property.
def base = (binding.hasVariable('context') && context?.projectDir) ? new File(context.projectDir.toString()) : new File(basedir.toString())
def files = []
base.eachFileRecurse { files << it }
def log = files.find { it.name == 'build.log' }
assert log != null : "no build.log under ${base}"
def text = log.getText('UTF-8')

assert text.contains('kestros.hub.id is empty') : 'missing id not reported'
assert text.contains('kestros.hub.title is empty') : 'missing title not reported'
assert text.contains('kestros.hub.tags has no type: tag') : 'missing type: tag not reported'
assert !text.contains('is not x.y.z or x.y.z-SNAPSHOT') : 'a valid version was reported'

def zips = files.findAll { it.parentFile.name == 'target' && it.name.endsWith('.zip') }*.name
assert zips.contains('build-integration-test-application-0.0.1-SNAPSHOT.zip') : "application did not build: ${zips}"
assert zips.contains('build-integration-test-content-0.0.1-SNAPSHOT.zip') : "content did not build: ${zips}"
assert !zips.any { it.startsWith('build-integration-test-community-hub') } : "community-hub packaged anyway: ${zips}"
return true
