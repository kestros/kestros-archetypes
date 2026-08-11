package io.kestros.cms.archetype;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import groovy.lang.GroovyShell;
import groovy.lang.Script;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.junit.Before;
import org.junit.Test;

/**
 * Covers the released-versus-SNAPSHOT decision in archetype-post-generate.groovy, which ships
 * inside the archetype and runs on an end user's machine.
 *
 * <p>A released archetype is on Maven Central and must still be fetched before generation. A
 * -SNAPSHOT is never published there, so fetching it always fails and the script used to throw
 * before it generated anything. The two paths must not be confused: applying the skip to a
 * released version would break every real user to fix a developer path.
 */
public class ArchetypePostGenerateTest {

  private static final String SCRIPT = "/META-INF/archetype-post-generate.groovy";

  private Script script;

  @Before
  public void setUp() throws Exception {
    // Parsed, not run. Running it would need the archetype plugin's `request` binding and would
    // shell out to Maven; the decision under test is reachable without either.
    final InputStream stream = getClass().getResourceAsStream(SCRIPT);
    assertNotNull("archetype-post-generate.groovy is not on the classpath", stream);
    try (InputStreamReader reader = new InputStreamReader(stream, UTF_8)) {
      script = new GroovyShell().parse(reader, "archetype-post-generate.groovy");
    }
  }

  private Object dependencyGetCommandFor(final String archetype, final String archetypeVersion) {
    return script.invokeMethod("dependencyGetCommandFor",
        new Object[]{archetype, archetypeVersion});
  }

  @Test
  public void testDependencyGetCommandForWhenVersionIsReleased() {
    assertEquals("mvn dependency:get -Dartifact=io.kestros.cms:kestros-api-archetype:0.9.0"
                 + " -Dtransative=false -U",
        String.valueOf(dependencyGetCommandFor("kestros-api-archetype", "0.9.0")));
  }

  @Test
  public void testDependencyGetCommandForWhenVersionIsSnapshot() {
    assertNull(dependencyGetCommandFor("kestros-api-archetype", "0.9.1-SNAPSHOT"));
  }

  @Test
  public void testDependencyGetCommandForWhenVersionIsSnapshotForEveryGeneratedModule() {
    for (final String archetype : new String[]{"kestros-api-archetype", "kestros-core-archetype",
        "kestros-content-archetype", "kestros-application-archetype"}) {
      assertNull(archetype + " must not be fetched from Central as a SNAPSHOT",
          dependencyGetCommandFor(archetype, "1.0.0-SNAPSHOT"));
    }
  }

  @Test
  public void testDependencyGetCommandForWhenVersionMerelyContainsSnapshot() {
    // Only a version that ends in -SNAPSHOT is the developer path. A released version that happens
    // to carry the word elsewhere is still on Central and must still be fetched.
    assertEquals("mvn dependency:get -Dartifact=io.kestros.cms:kestros-core-archetype:0.9.0"
                 + "-SNAPSHOT-rebuild -Dtransative=false -U",
        String.valueOf(dependencyGetCommandFor("kestros-core-archetype", "0.9.0-SNAPSHOT-rebuild")));
  }

  @Test
  public void testDependencyGetCommandForWhenVersionIsNull() {
    assertEquals("mvn dependency:get -Dartifact=io.kestros.cms:kestros-api-archetype:null"
                 + " -Dtransative=false -U",
        String.valueOf(dependencyGetCommandFor("kestros-api-archetype", null)));
  }
}
