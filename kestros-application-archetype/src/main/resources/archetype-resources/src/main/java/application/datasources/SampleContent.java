package ${package}.application.datasources;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Author-visible sample copy returned by the sample datasources.
 *
 * <p>Change the sample text here rather than in each datasource. Element ids and other markup
 * structure stay in the datasources.</p>
 */
public final class SampleContent {

  /** Sample Button text. */
  public static final String BUTTON_TEXT = "Sample Button from a datasource";

  /** Sample Button link. */
  public static final String BUTTON_HREF = "https://kestros.io";

  /** Sample Button title. */
  public static final String BUTTON_TITLE = "Visit kestros.io";

  /** Sample Button rel. */
  public static final String BUTTON_REL = "noopener";

  /** Sample Button aria-label. */
  public static final String BUTTON_ARIA_LABEL = "Sample button supplied by a datasource";

  /** Sample Card title. */
  public static final String CARD_TITLE = "Sample Card from a datasource";

  /** Sample Card description. */
  public static final String CARD_DESCRIPTION =
      "This card's title and description are supplied programmatically by "
          + "SampleCardDataSource, selected on the component via kes:datasource.";

  /** Sample Text paragraph. */
  public static final String TEXT =
      "This paragraph text is supplied programmatically by SampleTextDataSource, "
          + "selected on the Text component via the kes:datasource property.";

  /** Sample Table first column header. */
  public static final String TABLE_HEADER_NAME = "Name";

  /** Sample Table second column header. */
  public static final String TABLE_HEADER_ROLE = "Role";

  /** Sample Table data rows, one list of cell values per row. */
  public static final List<List<String>> TABLE_ROWS = Collections.unmodifiableList(Arrays.asList(
      Collections.unmodifiableList(Arrays.asList("Ada Lovelace", "Mathematician")),
      Collections.unmodifiableList(Arrays.asList("Alan Turing", "Computer Scientist"))));

  private SampleContent() {
  }
}
