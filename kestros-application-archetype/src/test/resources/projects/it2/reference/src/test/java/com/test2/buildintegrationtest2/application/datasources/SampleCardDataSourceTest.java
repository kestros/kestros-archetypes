package com.test2.buildintegrationtest2.application.datasources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import io.kestros.cms.components.basic.testing.BaseDataSourceTest;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the sample single-Card datasource.
 */
public class SampleCardDataSourceTest extends BaseDataSourceTest {

  private SampleCardDataSource dataSource;

  @Before
  public void setUp() {
    context.addModelsForClasses(SampleCardDataSource.class);

    // The title heading resolves its containing page, so the resource must live under a page.
    Map<String, Object> pageProperties = new HashMap<>();
    pageProperties.put("jcr:primaryType", "kes:Page");
    context.create().resource("/content/page", pageProperties);
    context.create().resource("/content/page/jcr:content");
    Resource resource = context.create().resource("/content/page/jcr:content/sample-card");
    dataSource = resource.adaptTo(SampleCardDataSource.class);
    assertNotNull(dataSource);
  }

  @Test
  public void testGetTitleElement() {
    assertNotNull(dataSource.getTitleElement());
  }

  @Test
  public void testGetDescription() {
    assertEquals("This card's title and description are supplied programmatically by "
        + "SampleCardDataSource, selected on the component via kes:datasource.",
        dataSource.getDescription());
  }

  @Test
  public void testGetImageElement() {
    assertNull(dataSource.getImageElement());
  }

  @Test
  public void testGetButtonGroupElement() {
    assertNull(dataSource.getButtonGroupElement());
  }
}
