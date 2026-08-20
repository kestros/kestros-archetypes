package io.kestros.cms.archetype;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;

/**
 * The tutorial is eleven steps of content that only a human walking it can really judge, and until
 * now nothing judged it at all - this module had no src/test, so every defect below reached
 * develop unseen. These assertions cover the ones a machine can see: a page pointing at a theme
 * that does not exist, an instruction naming a framework that appears in no picker, an
 * instruction sending the learner backwards, two views that teach the same thing, a view nested
 * one directory too deep, and an empty file at the end of the walk.
 *
 * <p>This reads the SOURCE tree under src/main/resources/archetype-resources, not a generated
 * project. Every acceptance criterion on card #163 is stated against source paths, and generating
 * a project would fork a second Maven and resolve a SNAPSHOT parent from the private Nexus.
 */
public class TutorialContentTest {

  /**
   * Surefire runs with the module directory as its working directory, so the archetype's content
   * tree is a fixed path below it.
   */
  private static final File CONTENT_ROOT = new File(
      "src/main/resources/archetype-resources/src/content/jcr_root");

  private static final File UI_FRAMEWORKS = new File(CONTENT_ROOT, "etc/ui-frameworks");

  private static final File COMPONENTS = new File(CONTENT_ROOT, "apps/__artifactId__/components");

  private static final File GETTING_STARTED = new File(COMPONENTS, "tutorial-getting-started");

  private static final File STEP_ONE_VIEW = new File(GETTING_STARTED, "common/content.html");

  /** The view directory a step-less view sits in when it is the step 1 view. */
  private static final String COMMON = "common";

  private static final Pattern JCR_TITLE = Pattern.compile("jcr:title=\"([^\"]*)\"");

  private static final Pattern PRIMARY_TYPE = Pattern.compile("jcr:primaryType=\"([^\"]*)\"");

  private static final Pattern KES_THEME = Pattern.compile("kes:theme=\"([^\"]*)\"");

  /** "Select <code>X</code>", the instruction that moves a learner from one step to the next. */
  private static final Pattern SELECT_INSTRUCTION = Pattern.compile(
      "Select\\s*<code>\\s*([^<]*?)\\s*</code>");

  /** Both title forms in use: "Step 4: Using Library Versions" and "... (Step 4) 1.0.0". */
  private static final Pattern STEP_NUMBER = Pattern.compile("Step\\s+(\\d+)");

  private static final Pattern STEP_DIRECTORY = Pattern.compile("step-(\\d+)(?:-.*)?");

  private static final Pattern VERSION_DIRECTORY = Pattern.compile("\\d+\\.\\d+\\.\\d+");

  private static final Pattern EMPTY_LIST_ITEM = Pattern.compile("<li>\\s*</li>");

  private String read(final File file) throws IOException {
    return new String(Files.readAllBytes(file.toPath()), UTF_8);
  }

  private List<File> filesNamed(final File root, final String name) throws IOException {
    assertTrue(root.getPath() + " does not exist", root.isDirectory());
    try (Stream<Path> walk = Files.walk(root.toPath())) {
      return walk.filter(Files::isRegularFile).map(Path::toFile)
          .filter(file -> file.getName().equals(name))
          .sorted().collect(Collectors.toList());
    }
  }

  private String attribute(final String source, final Pattern pattern) {
    final Matcher matcher = pattern.matcher(source);
    return matcher.find() ? matcher.group(1) : null;
  }

  /** Every UiFramework or ManagedUiFramework node under etc/ui-frameworks, by its jcr:title. */
  private Map<String, File> frameworksByTitle() throws IOException {
    final Map<String, File> byTitle = new LinkedHashMap<>();
    for (final File node : filesNamed(UI_FRAMEWORKS, ".content.xml")) {
      final String source = read(node);
      final String primaryType = attribute(source, PRIMARY_TYPE);
      if (primaryType == null || !primaryType.endsWith("UiFramework")) {
        continue;
      }
      final String title = attribute(source, JCR_TITLE);
      if (title != null && !title.isEmpty()) {
        byTitle.put(title, node.getParentFile());
      }
    }
    assertTrue("no UI frameworks were found under " + UI_FRAMEWORKS.getPath(), !byTitle.isEmpty());
    return byTitle;
  }

  /**
   * The step a view belongs to, taken from the nearest ancestor directory named step-N. Version
   * directories carry no step number of their own - step-4-lib/1.0.0 is still step 4 - so this
   * walks up rather than reading only the directory the file sits in. A view with no step ancestor
   * is step 1 only when it is the common view; anything else is an unplaced view and fails, rather
   * than silently passing every forward check.
   */
  private int stepOf(final File view) {
    for (File directory = view.getParentFile();
        directory != null && !directory.equals(GETTING_STARTED.getAbsoluteFile())
            && !directory.getPath().equals(GETTING_STARTED.getPath());
        directory = directory.getParentFile()) {
      final Matcher matcher = STEP_DIRECTORY.matcher(directory.getName());
      if (matcher.matches()) {
        return Integer.parseInt(matcher.group(1));
      }
      if (COMMON.equals(directory.getName())) {
        return 1;
      }
    }
    fail(view.getPath() + " sits under no step-N directory, so which step it teaches - and so "
         + "whether its instructions point forward - cannot be established");
    return -1;
  }

  private int stepNamedBy(final String title) {
    final Matcher matcher = STEP_NUMBER.matcher(title);
    return matcher.find() ? Integer.parseInt(matcher.group(1)) : -1;
  }

  private String sha256(final File file) throws IOException, NoSuchAlgorithmException {
    final byte[] digest = MessageDigest.getInstance("SHA-256")
        .digest(Files.readAllBytes(file.toPath()));
    final StringBuilder hex = new StringBuilder();
    for (final byte b : digest) {
      hex.append(String.format("%02x", b));
    }
    return hex.toString();
  }

  private void failWith(final String summary, final List<String> offenders) {
    if (!offenders.isEmpty()) {
      fail(summary + ":" + System.lineSeparator()
           + String.join(System.lineSeparator(), offenders));
    }
  }

  /**
   * Criterion 4. A page whose kes:theme names a node that was never created renders with no
   * framework at all, and the learner sees an unstyled page with no way to tell why.
   */
  @Test
  public void testEveryThemeReferenceNamesANodeThatExists() throws Exception {
    final List<String> offenders = new ArrayList<>();
    for (final File node : filesNamed(CONTENT_ROOT, ".content.xml")) {
      final String theme = attribute(read(node), KES_THEME);
      if (theme == null || theme.isEmpty()) {
        continue;
      }
      final File target = new File(CONTENT_ROOT, theme.replaceFirst("^/", ""));
      if (!target.isDirectory()) {
        offenders.add("  " + node.getPath() + " -> " + theme);
      }
    }
    failWith("kes:theme names a node that does not exist", offenders);
  }

  /**
   * Criterion 5. The tutorial's whole teaching mechanism is that switching framework changes the
   * page. Two views that are byte-identical teach the learner that it does not.
   */
  @Test
  public void testNoTwoViewsOfTheTutorialComponentAreIdentical() throws Exception {
    final Map<String, List<String>> byChecksum = new LinkedHashMap<>();
    for (final File view : filesNamed(GETTING_STARTED, "content.html")) {
      byChecksum.computeIfAbsent(sha256(view), key -> new ArrayList<>()).add(view.getPath());
    }

    final List<String> offenders = new ArrayList<>();
    for (final Map.Entry<String, List<String>> group : byChecksum.entrySet()) {
      if (group.getValue().size() > 1) {
        offenders.add("  " + group.getKey().substring(0, 8) + " shared by "
                      + String.join(", ", group.getValue()));
      }
    }
    failWith("views of tutorial-getting-started are byte-identical", offenders);
  }

  /**
   * Criterion 6. Steps 8, 9 and 10 nest their views a directory deeper than steps 6 and 7, so one
   * of the two cannot resolve.
   */
  @Test
  public void testNoLayoutsDirectoryContainsAnotherLayoutsDirectory() throws Exception {
    final List<String> offenders;
    try (Stream<Path> walk = Files.walk(COMPONENTS.toPath())) {
      offenders = walk.map(path -> path.toFile().getPath().replace(File.separatorChar, '/'))
          .filter(path -> path.contains("/layouts/layouts/"))
          .map(path -> "  " + path).sorted().collect(Collectors.toList());
    }
    failWith("a layouts directory is nested inside another layouts directory", offenders);
  }

  /**
   * Criterion 7. An empty file is still a view: it renders as a blank region with no indication
   * that anything is missing.
   */
  @Test
  public void testNoViewIsEmpty() throws Exception {
    final List<String> offenders = new ArrayList<>();
    for (final File view : filesNamed(COMPONENTS, "content.html")) {
      final String source = read(view);
      if (source.trim().isEmpty()) {
        offenders.add("  " + view.getPath() + " is empty");
      } else if (EMPTY_LIST_ITEM.matcher(source).find()) {
        offenders.add("  " + view.getPath() + " has an empty <li></li>");
      }
    }
    failWith("a tutorial view renders nothing", offenders);
  }

  /**
   * Criterion 2. An instruction has to name a framework the learner can actually find in the
   * picker, and it has to name one further on than where they are - five of these named step 3
   * from step 3 onwards, which is why the tutorial could not be finished.
   */
  @Test
  public void testEverySelectInstructionNamesAFrameworkAndPointsForward() throws Exception {
    final Set<String> titles = frameworksByTitle().keySet();
    final List<String> offenders = new ArrayList<>();

    for (final File view : filesNamed(GETTING_STARTED, "content.html")) {
      final Matcher instruction = SELECT_INSTRUCTION.matcher(read(view));
      if (!instruction.find()) {
        continue;
      }
      // stepOf fails a view it cannot place, so it is only asked about views that actually
      // instruct the learner to move. The layout and variation views carry no instruction.
      final int here = stepOf(view);
      do {
        final String named = instruction.group(1);
        if (!titles.contains(named)) {
          offenders.add("  " + view.getPath() + " says to select \"" + named
                        + "\", which is the title of no UI framework");
          continue;
        }
        final int there = stepNamedBy(named);
        if (there <= here) {
          offenders.add("  " + view.getPath() + " is step " + here + " and sends the learner to \""
                        + named + "\" (step " + there + ")");
        }
      } while (instruction.find());
    }
    failWith("a Select instruction cannot be followed, or does not move the learner on", offenders);
  }

  /**
   * Criterion 3. Three versions listed under one label is a picker the learner cannot choose from
   * - all three of step 4's read "Versioned Framework (Step 4) 1.0.0".
   */
  @Test
  public void testNoTwoVersionsOfOneNodeShareATitle() throws Exception {
    final Map<String, Map<String, List<String>>> byParent = new LinkedHashMap<>();
    for (final File node : filesNamed(CONTENT_ROOT, ".content.xml")) {
      final File directory = node.getParentFile();
      if (!VERSION_DIRECTORY.matcher(directory.getName()).matches()) {
        continue;
      }
      final String title = attribute(read(node), JCR_TITLE);
      if (title == null) {
        continue;
      }
      byParent.computeIfAbsent(directory.getParentFile().getPath(), key -> new LinkedHashMap<>())
          .computeIfAbsent(title, key -> new ArrayList<>()).add(directory.getName());
    }

    final List<String> offenders = new ArrayList<>();
    for (final Map.Entry<String, Map<String, List<String>>> parent : byParent.entrySet()) {
      for (final Map.Entry<String, List<String>> title : parent.getValue().entrySet()) {
        if (title.getValue().size() > 1) {
          offenders.add("  " + parent.getKey() + " labels " + String.join(", ", title.getValue())
                        + " all as \"" + title.getKey() + "\"");
        }
      }
    }
    failWith("versions of one node cannot be told apart in a picker", offenders);
  }

  /**
   * Criterion 1. The first screen a learner reads. It linked to a component this archetype does
   * not contain, named a framework that does not exist, used the sibling archetype's directory
   * layout, hard-coded a localhost port, and pointed its CSS and JavaScript at a site path that
   * is not the one it is rendered on.
   */
  @Test
  public void testStepOneReadsCorrectly() throws Exception {
    final String source = read(STEP_ONE_VIEW);
    final List<String> offenders = new ArrayList<>();

    final long open = Pattern.compile("<p[\\s>]").matcher(source).results().count();
    final long close = Pattern.compile("</p>").matcher(source).results().count();
    if (open != close) {
      offenders.add("  " + open + " <p> against " + close + " </p>");
    }

    for (final String banned : new String[] {"sample-component", "application/src/content/",
        "http://localhost", "Empty Framework"}) {
      if (source.contains(banned)) {
        offenders.add("  still contains \"" + banned + "\"");
      }
    }

    // The real node is tutorial-beginner-step-1-framework, so a bare step-1-framework is only a
    // defect where it is not the tail of the real name.
    if (Pattern.compile("(?<!tutorial-beginner-)step-1-framework").matcher(source).find()) {
      offenders.add("  still names the framework node step-1-framework, which does not exist");
    }

    for (final String extension : new String[] {".css", ".js"}) {
      final Matcher href = Pattern.compile("\"(/content/sites/[^\"]*\\" + extension + ")\"")
          .matcher(source);
      boolean found = false;
      while (href.find()) {
        found = true;
        if (!href.group(1).startsWith("/content/sites/${artifactId}-beginner-tutorial.")) {
          offenders.add("  " + extension + " href " + href.group(1)
                        + " does not resolve against the site this page is on");
        }
      }
      if (!found) {
        offenders.add("  links to no " + extension + " file at all");
      }
    }

    final Map<String, File> frameworks = frameworksByTitle();
    if (!source.contains("Step 1: Getting Started")) {
      offenders.add("  does not name the framework it is rendered by, \"Step 1: Getting Started\"");
    }
    if (!source.contains("tutorial-beginner-step-1-framework")) {
      offenders.add("  does not name the node tutorial-beginner-step-1-framework");
    }
    assertTrue("tutorial-beginner-step-1-framework is not a framework any more, so the step 1 "
               + "content this asserts against is wrong",
        frameworks.containsKey("Step 1: Getting Started"));

    failWith("the first screen of the tutorial does not read correctly",
        Collections.unmodifiableList(offenders));
  }

  /** Guards the two constants above: a rename would otherwise make every test here vacuous. */
  @Test
  public void testTheTutorialContentTreeIsWhereThisTestExpectsIt() {
    assertTrue(CONTENT_ROOT.getPath() + " does not exist - surefire's working directory is the "
               + "module directory, so this path is relative to it", CONTENT_ROOT.isDirectory());
    assertTrue(GETTING_STARTED.getPath() + " does not exist", GETTING_STARTED.isDirectory());
    assertTrue(STEP_ONE_VIEW.getPath() + " does not exist", STEP_ONE_VIEW.isFile());
    assertTrue(UI_FRAMEWORKS.getPath() + " does not exist", UI_FRAMEWORKS.isDirectory());
    assertTrue("no frameworks are declared under " + UI_FRAMEWORKS.getPath(),
        new LinkedHashSet<>(Arrays.asList(UI_FRAMEWORKS.list())).size() > 1);
  }
}
