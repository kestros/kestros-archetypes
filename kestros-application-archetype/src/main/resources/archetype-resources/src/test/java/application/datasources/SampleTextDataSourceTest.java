package ${package}.application.datasources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import io.kestros.cms.components.basic.testing.BaseDataSourceTest;
import org.junit.Test;

/**
 * Tests the sample Text datasource returns its sample paragraph.
 */
public class SampleTextDataSourceTest extends BaseDataSourceTest {

  @Test
  public void testGetText() {
    context.addModelsForClasses(SampleTextDataSource.class);
    SampleTextDataSource dataSource = context.create().resource("/content/sample-text")
        .adaptTo(SampleTextDataSource.class);
    assertNotNull(dataSource);
    assertEquals("This paragraph text is supplied programmatically by SampleTextDataSource, "
        + "selected on the Text component via the kes:datasource property.",
        dataSource.getText());
  }
}
