package ${package}.application.datasources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import io.kestros.cms.components.basic.api.exceptions.ComponentConfigurationException;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the shared base of the sample container datasources.
 */
public class BaseSampleContainerDataSourceTest {

  private BaseSampleContainerDataSource dataSource;

  @Before
  public void setUp() {
    // Any concrete sample container datasource exercises the shared base.
    dataSource = new SampleTableDataSource();
  }

  @Test
  public void testBuildOrNullReturnsBuiltValue() {
    assertEquals("built", dataSource.buildOrNull(() -> "built", "value"));
  }

  @Test
  public void testBuildOrNullReturnsNullWhenBuilderThrows() {
    String result = dataSource.buildOrNull(() -> {
      throw new ComponentConfigurationException("broken");
    }, "value");
    assertNull(result);
  }

  @Test
  public void testGetLogIsCategorisedUnderConcreteClass() {
    assertEquals(SampleTableDataSource.class.getName(), dataSource.getLog().getName());
  }
}
