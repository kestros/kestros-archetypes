package ${package}.application.datasources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import io.kestros.cms.components.basic.api.content.AnchorTarget;
import io.kestros.cms.components.basic.testing.BaseDataSourceTest;
import org.apache.sling.api.resource.Resource;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the sample Button datasource.
 */
public class SampleButtonDataSourceTest extends BaseDataSourceTest {

  private SampleButtonDataSource dataSource;

  @Before
  public void setUp() {
    context.addModelsForClasses(SampleButtonDataSource.class);
    Resource resource = context.create().resource("/content/sample-button");
    dataSource = resource.adaptTo(SampleButtonDataSource.class);
    assertNotNull(dataSource);
  }

  @Test
  public void testGetText() {
    assertEquals("Sample Button from a datasource", dataSource.getText());
  }

  @Test
  public void testGetHref() {
    assertEquals("https://kestros.io", dataSource.getHref());
  }

  @Test
  public void testGetTitle() {
    assertEquals("Visit kestros.io", dataSource.getTitle());
  }

  @Test
  public void testGetTarget() {
    assertEquals(AnchorTarget.NEW_WINDOW, dataSource.getTarget());
  }

  @Test
  public void testGetRel() {
    assertEquals("noopener", dataSource.getRel());
  }

  @Test
  public void testGetAriaLabel() {
    assertEquals("Sample button supplied by a datasource", dataSource.getAriaLabel());
  }

  @Test
  public void testGetAriaDescribedBy() {
    assertNull(dataSource.getAriaDescribedBy());
  }

  @Test
  public void testGetLang() {
    assertNull(dataSource.getLang());
  }

  @Test
  public void testIsDisabled() {
    assertFalse(dataSource.isDisabled());
  }
}
