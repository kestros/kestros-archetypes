package com.test2.buildintegrationtest2.application.datasources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import io.kestros.cms.components.basic.api.content.AnchorTarget;
import io.kestros.cms.components.basic.testing.BaseDataSourceTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the sample Button datasource returns its sample values.
 */
public class SampleButtonDataSourceTest extends BaseDataSourceTest {

  private SampleButtonDataSource dataSource;

  @Before
  public void setUp() {
    context.addModelsForClasses(SampleButtonDataSource.class);
    dataSource = context.create().resource("/content/sample-button")
        .adaptTo(SampleButtonDataSource.class);
    assertNotNull(dataSource);
  }

  @Test
  public void testGetters() {
    assertEquals("Sample Button from a datasource", dataSource.getText());
    assertEquals("https://kestros.io", dataSource.getHref());
    assertEquals("Visit kestros.io", dataSource.getTitle());
    assertEquals(AnchorTarget.NEW_WINDOW, dataSource.getTarget());
    assertEquals("noopener", dataSource.getRel());
    assertEquals("Sample button supplied by a datasource", dataSource.getAriaLabel());
    assertNull(dataSource.getAriaDescribedBy());
    assertNull(dataSource.getLang());
    assertFalse(dataSource.isDisabled());
  }
}
