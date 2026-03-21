package ${groupId}.${artifactIdNoSpecialCharacters}.application.components.samplecomponent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class SampleComponentTest {

  @Rule
  public final SlingContext context = new SlingContext();

  private SampleComponent sampleComponent;

  private Resource resource;

  private Map<String, Object> properties = new HashMap<>();

  @Before
  public void setup() {
    context.addModelsForPackage("${groupId}.${artifactIdNoSpecialCharacters}");
  }

  @Test
  public void testGetSampleProperty() {
    properties.put("sampleProperty", "property-value");
    resource = context.create().resource("/sample-component", properties);

    sampleComponent = resource.adaptTo(SampleComponent.class);

    assertEquals("property-value", sampleComponent.getSampleProperty());
  }

  @Test
  public void testGetSamplePropertyWhenValueIsEmpty() {
    properties.put("sampleProperty", "");
    resource = context.create().resource("/sample-component", properties);

    sampleComponent = resource.adaptTo(SampleComponent.class);

    assertEquals("", sampleComponent.getSampleProperty());
  }

  @Test
  public void testGetSamplePropertyWhenValueNotSet() {
    resource = context.create().resource("/sample-component", properties);

    sampleComponent = resource.adaptTo(SampleComponent.class);

    assertEquals("", sampleComponent.getSampleProperty());
  }

  @Test
  public void testGetDisplayStyleDefault() {
    resource = context.create().resource("/sample-component", properties);

    sampleComponent = resource.adaptTo(SampleComponent.class);

    assertEquals("default", sampleComponent.getDisplayStyle());
  }

  @Test
  public void testGetDisplayStyleWhenSet() {
    properties.put("displayStyle", "highlight");
    resource = context.create().resource("/sample-component", properties);

    sampleComponent = resource.adaptTo(SampleComponent.class);

    assertEquals("highlight", sampleComponent.getDisplayStyle());
  }

  @Test
  public void testIsShowIconDefault() {
    resource = context.create().resource("/sample-component", properties);

    sampleComponent = resource.adaptTo(SampleComponent.class);

    assertFalse(sampleComponent.isShowIcon());
  }

  @Test
  public void testIsShowIconWhenTrue() {
    properties.put("showIcon", true);
    resource = context.create().resource("/sample-component", properties);

    sampleComponent = resource.adaptTo(SampleComponent.class);

    assertTrue(sampleComponent.isShowIcon());
  }

}
