package com.test.buildintegrationtest.application.datasources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import io.kestros.cms.components.basic.testing.BaseDataSourceTest;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the sample Table datasource.
 */
public class SampleTableDataSourceTest extends BaseDataSourceTest {

  private SampleTableDataSource dataSource;

  @Before
  public void setUp() {
    context.addModelsForClasses(SampleTableDataSource.class);

    // Table elements resolve their containing page, so the resource must live under a page.
    Map<String, Object> pageProperties = new HashMap<>();
    pageProperties.put("jcr:primaryType", "kes:Page");
    context.create().resource("/content/page", pageProperties);
    context.create().resource("/content/page/jcr:content");
    Resource resource = context.create().resource("/content/page/jcr:content/sample-table");
    dataSource = resource.adaptTo(SampleTableDataSource.class);
    assertNotNull(dataSource);
  }

  @Test
  public void testGetHeaderElements() {
    assertEquals(2, dataSource.getHeaderElements().size());
  }

  @Test
  public void testGetRowElements() {
    assertEquals(2, dataSource.getRowElements().size());
  }
}
