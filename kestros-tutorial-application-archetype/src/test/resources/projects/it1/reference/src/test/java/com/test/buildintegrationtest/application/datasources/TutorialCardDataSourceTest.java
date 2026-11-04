package com.test.buildintegrationtest.application.datasources;

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
 * Tests the tutorial's step-10 Card datasource. Extends {@link BaseDataSourceTest} for the shared
 * service setup and adds only this datasource's model and resource.
 */
public class TutorialCardDataSourceTest extends BaseDataSourceTest {

  private Resource resource;

  @Before
  public void setUp() {
    context.addModelsForClasses(TutorialCardDataSource.class);

    // Cards resolve their containing page, so the datasource resource must live under a page.
    Map<String, Object> pageProperties = new HashMap<>();
    pageProperties.put("jcr:primaryType", "kes:Page");
    context.create().resource("/content/page", pageProperties);
    context.create().resource("/content/page/jcr:content");
    resource = context.create().resource("/content/page/jcr:content/tutorial-card");
  }

  @Test
  public void testGetTitleElement() {
    TutorialCardDataSource dataSource = resource.adaptTo(TutorialCardDataSource.class);
    assertNotNull(dataSource);
    assertNotNull(dataSource.getTitleElement());
    assertEquals("This card came from Java", dataSource.getTitleElement().getHeadingText());
  }

  @Test
  public void testGetDescription() {
    TutorialCardDataSource dataSource = resource.adaptTo(TutorialCardDataSource.class);
    assertNotNull(dataSource);
    assertEquals("Nothing on this card is stored in the content package. TutorialCardDataSource "
                 + "builds the title and this description in code, and the Card component picked "
                 + "it up because its kes:datasource property names this datasource.",
        dataSource.getDescription());
  }

  @Test
  public void testGetImageElementIsNull() {
    TutorialCardDataSource dataSource = resource.adaptTo(TutorialCardDataSource.class);
    assertNotNull(dataSource);
    assertNull(dataSource.getImageElement());
  }

  @Test
  public void testGetButtonGroupElementIsNull() {
    TutorialCardDataSource dataSource = resource.adaptTo(TutorialCardDataSource.class);
    assertNotNull(dataSource);
    assertNull(dataSource.getButtonGroupElement());
  }
}
