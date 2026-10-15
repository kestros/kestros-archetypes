package ${package}.application.datasources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import io.kestros.cms.components.basic.api.content.KestrosHeading;
import io.kestros.cms.components.basic.testing.BaseDataSourceTest;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the sample Card datasource returns its sample title, description and ids.
 */
public class SampleCardDataSourceTest extends BaseDataSourceTest {

  private SampleCardDataSource dataSource;

  @Before
  public void setUp() {
    context.addModelsForClasses(SampleCardDataSource.class);

    // Elements resolve their containing page, so the datasource resource must live under a page.
    Map<String, Object> pageProperties = new HashMap<>();
    pageProperties.put("jcr:primaryType", "kes:Page");
    context.create().resource("/content/page", pageProperties);
    context.create().resource("/content/page/jcr:content");
    dataSource = context.create().resource("/content/page/jcr:content/sample-card")
        .adaptTo(SampleCardDataSource.class);
    assertNotNull(dataSource);
  }

  @Test
  public void testGetTitleElement() {
    KestrosHeading title = dataSource.getTitleElement();
    assertNotNull(title);
    assertEquals("Sample Card from a datasource", title.getHeadingText());
    assertEquals("h3", title.getHeadingType());
    assertEquals("titleElement", title.getResource().getName());
  }

  @Test
  public void testGetDescription() {
    assertEquals("This card's title and description are supplied programmatically by "
        + "SampleCardDataSource, selected on the component via kes:datasource.",
        dataSource.getDescription());
  }

  @Test
  public void testImageAndButtonGroupAreOmitted() {
    assertNull(dataSource.getImageElement());
    assertNull(dataSource.getButtonGroupElement());
  }
}
