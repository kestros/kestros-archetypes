package ${groupId}.${artifactIdNoSpecialCharacters}.application.components;

import static org.junit.Assert.assertEquals;

import ${groupId}.${artifactIdNoSpecialCharacters}.api.services.SampleService;
import ${groupId}.${artifactIdNoSpecialCharacters}.core.services.SampleServiceImpl;
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

  private SampleService sampleService;

  private Resource resource;

  private Map<String, Object> properties = new HashMap<>();

  @Before
  public void setup() {
    context.addModelsForPackage("${groupId}");
    sampleService = new SampleServiceImpl();
  }

  @Test
  public void testGetMyServiceValue() {
    context.registerInjectActivateService(sampleService);
    resource = context.create().resource("/sample-component", properties);

    sampleComponent = resource.adaptTo(SampleComponent.class);

    assertEquals("Hello World!", sampleComponent.getMyServiceValue());
  }

  @Test
  public void testGetMyServiceValueWhenServiceIsNull() {
    resource = context.create().resource("/sample-component", properties);

    sampleComponent = resource.adaptTo(SampleComponent.class);

    assertEquals("", sampleComponent.getMyServiceValue());
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

}