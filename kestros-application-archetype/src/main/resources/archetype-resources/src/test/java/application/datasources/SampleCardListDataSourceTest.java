package ${package}.application.datasources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import ${package}.api.services.SampleService;
import ${package}.core.services.SampleServiceImpl;
import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the sample Card List datasource. Extends {@link BaseDataSourceTest} for the shared service
 * setup and adds only this datasource's model and resource.
 */
public class SampleCardListDataSourceTest extends BaseDataSourceTest {

  private Resource resource;

  @Before
  public void setUp() {
    context.addModelsForClasses(SampleCardListDataSource.class);

    // Cards resolve their containing page, so the datasource resource must live under a page.
    Map<String, Object> pageProperties = new HashMap<>();
    pageProperties.put("jcr:primaryType", "kes:Page");
    context.create().resource("/content/page", pageProperties);
    context.create().resource("/content/page/jcr:content");
    resource = context.create().resource("/content/page/jcr:content/sample-cards");
  }

  @Test
  public void testGetCardElementsWhenSampleServiceIsMissing() {
    SampleCardListDataSource dataSource = resource.adaptTo(SampleCardListDataSource.class);
    assertNotNull(dataSource);
    assertEquals(0, dataSource.getCardElements().size());
  }

  @Test
  public void testGetCardElements() {
    context.registerService(SampleService.class, new SampleServiceImpl());
    SampleCardListDataSource dataSource = resource.adaptTo(SampleCardListDataSource.class);
    assertNotNull(dataSource);
    assertEquals(2, dataSource.getCardElements().size());
  }
}
