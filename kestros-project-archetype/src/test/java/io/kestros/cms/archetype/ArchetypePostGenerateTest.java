package io.kestros.cms.archetype;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import groovy.lang.GroovyShell;
import groovy.lang.Script;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

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

  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

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
    return dependencyGetCommandFor("mvn", archetype, archetypeVersion);
  }

  private Object dependencyGetCommandFor(final String mavenExecutable, final String archetype,
      final String archetypeVersion) {
    return script.invokeMethod("dependencyGetCommandFor",
        new Object[]{mavenExecutable, archetype, archetypeVersion});
  }

  @SuppressWarnings("unchecked")
  private static String joined(final Object command) {
    // The command is a list so an executable path containing a space survives; joining it back
    // keeps these assertions readable as the command line they produce.
    return String.join(" ", (List<String>) command);
  }

  private Object mavenExecutable(final String mavenHome, final String osName) {
    return script.invokeMethod("mavenExecutable", new Object[]{mavenHome, osName});
  }

  @Test
  public void testDependencyGetCommandForWhenVersionIsReleased() {
    assertEquals("mvn dependency:get -Dartifact=io.kestros.cms:kestros-api-archetype:0.9.0"
                 + " -Dtransative=false -U",
        joined(dependencyGetCommandFor("kestros-api-archetype", "0.9.0")));
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
        joined(dependencyGetCommandFor("kestros-core-archetype", "0.9.0-SNAPSHOT-rebuild")));
  }

  @Test
  public void testDependencyGetCommandForWhenVersionIsNull() {
    assertEquals("mvn dependency:get -Dartifact=io.kestros.cms:kestros-api-archetype:null"
                 + " -Dtransative=false -U",
        joined(dependencyGetCommandFor("kestros-api-archetype", null)));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testDependencyGetCommandForKeepsAMavenPathWithSpacesInOnePiece() {
    final List<String> command = (List<String>) dependencyGetCommandFor(
        "/opt/build tools/maven/bin/mvn", "kestros-api-archetype", "0.9.0");
    assertEquals("/opt/build tools/maven/bin/mvn", command.get(0));
  }

  /**
   * The script copied files by shelling out to `cp` and never waiting on the process, so the
   * caller carried on against a file that might not exist yet. Every assertion below is made
   * immediately on return, with no sleep and no retry — that is the whole point.
   */
  @Test
  public void testCopyFileHasFinishedWhenItReturns() throws Exception {
    final File source = folder.newFile("pom.xml");
    Files.write(source.toPath(), "<project/>".getBytes(UTF_8));
    final File target = new File(folder.getRoot(), "original-pom.xml");

    script.invokeMethod("copyFile", new Object[]{source, target});

    assertTrue("copyFile must not return before the target exists", target.exists());
    assertEquals("<project/>", new String(Files.readAllBytes(target.toPath()), UTF_8));
  }

  @Test
  public void testCopyFileOverwritesAnExistingTarget() throws Exception {
    // resetPomFile copies onto a path the previous module's generation may have left behind.
    final File source = folder.newFile("original-pom.xml");
    Files.write(source.toPath(), "<project>new</project>".getBytes(UTF_8));
    final File target = folder.newFile("pom.xml");
    Files.write(target.toPath(), "<project>stale</project>".getBytes(UTF_8));

    script.invokeMethod("copyFile", new Object[]{source, target});

    assertEquals("<project>new</project>", new String(Files.readAllBytes(target.toPath()), UTF_8));
  }

  @Test
  public void testResetPomFileRestoresPomFromOriginalBeforeItReturns() throws Exception {
    // The generated project's pom.xml is rewritten by each submodule generation, so it is put back
    // from original-pom.xml between them. The restore used to be an unwaited `cp`, so the next
    // generation - and replacePomFile after it - could run against a pom.xml that was not there.
    final File original = folder.newFile("original-pom.xml");
    Files.write(original.toPath(), "<project>original</project>".getBytes(UTF_8));
    final File pom = folder.newFile("pom.xml");
    Files.write(pom.toPath(), "<project>with-modules</project>".getBytes(UTF_8));

    script.invokeMethod("resetPomFile", new Object[]{folder.getRoot().getAbsolutePath()});

    assertTrue("pom.xml must exist by the time resetPomFile returns", pom.exists());
    assertEquals("<project>original</project>", new String(Files.readAllBytes(pom.toPath()), UTF_8));
  }

  @Test
  public void testResetPomFileLeavesNoPomWhenThereIsNoOriginal() throws Exception {
    final File pom = folder.newFile("pom.xml");
    Files.write(pom.toPath(), "<project>with-modules</project>".getBytes(UTF_8));

    script.invokeMethod("resetPomFile", new Object[]{folder.getRoot().getAbsolutePath()});

    assertFalse("nothing to restore from, so pom.xml stays removed", pom.exists());
  }

  /**
   * The script forks Maven to generate each submodule. `mvn` is not always on the PATH a forked
   * process inherits — a Jenkins Maven tool installation, the Maven wrapper and most IDEs all
   * invoke it by absolute path — and the fork then dies with "Cannot run program mvn".
   */
  @Test
  public void testMavenExecutableUsesTheBinaryFromMavenHome() throws Exception {
    final File bin = folder.newFolder("maven", "bin");
    final File mvn = new File(bin, "mvn");
    assertTrue(mvn.createNewFile());

    assertEquals(mvn.getAbsolutePath(),
        mavenExecutable(bin.getParentFile().getAbsolutePath(), "Linux"));
  }

  @Test
  public void testMavenExecutableUsesTheWindowsBinaryOnWindows() throws Exception {
    final File bin = folder.newFolder("maven", "bin");
    final File mvnCmd = new File(bin, "mvn.cmd");
    assertTrue(mvnCmd.createNewFile());

    assertEquals(mvnCmd.getAbsolutePath(),
        mavenExecutable(bin.getParentFile().getAbsolutePath(), "Windows 10"));
  }

  @Test
  public void testMavenExecutableFallsBackToThePathWhenMavenHomeIsUnset() {
    // System.getProperty("maven.home") is set by Maven's own launcher, but the script must not
    // break for anything that parses or runs it without one.
    assertEquals("mvn", mavenExecutable(null, "Linux"));
    assertEquals("mvn", mavenExecutable("  ", "Linux"));
  }

  @Test
  public void testMavenExecutableFallsBackToThePathWhenMavenHomeHoldsNoBinary() throws Exception {
    final File mavenHome = folder.newFolder("empty-maven");

    assertEquals("mvn", mavenExecutable(mavenHome.getAbsolutePath(), "Linux"));
  }
}
