package com.test.buildintegrationtest.application.datasources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import io.kestros.cms.components.basic.testing.BaseDataSourceTest;
import org.apache.sling.api.resource.Resource;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the sample Text datasource.
 */
public class SampleTextDataSourceTest extends BaseDataSourceTest {

  private Resource resource;

  @Before
  public void setUp() {
    context.addModelsForClasses(SampleTextDataSource.class);
    resource = context.create().resource("/content/sample-text");
  }

  @Test
  public void testGetText() {
    SampleTextDataSource dataSource = resource.adaptTo(SampleTextDataSource.class);
    assertNotNull(dataSource);
    assertEquals("This paragraph text is supplied programmatically by SampleTextDataSource, "
        + "selected on the Text component via the kes:datasource property.",
        dataSource.getText());
  }
}
