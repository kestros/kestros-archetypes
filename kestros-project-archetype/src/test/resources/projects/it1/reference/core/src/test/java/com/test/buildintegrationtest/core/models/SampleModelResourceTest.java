package com.test.buildintegrationtest.core.models;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class SampleModelResourceTest {

  @Rule
  public SlingContext context = new SlingContext();

  private SampleModelResource sampleModelResource;

  private Resource resource;

  private Map<String, Object> properties = new HashMap<>();

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testGetTitle() {
    properties.put("jcr:title", "Sample Title");
    resource = context.create().resource("/sample-resource", properties);
    sampleModelResource = resource.adaptTo(SampleModelResource.class);
    assertEquals("Sample Title", sampleModelResource.getTitle());
  }

  @Test
  public void testGetTitleWhenPropertyIsMissing() {
    resource = context.create().resource("/sample-resource");
    sampleModelResource = resource.adaptTo(SampleModelResource.class);
    assertEquals("", sampleModelResource.getTitle());
  }

  @Test
  public void testGetDescription() {
    properties.put("jcr:description", "Sample Description");
    resource = context.create().resource("/sample-resource", properties);
    sampleModelResource = resource.adaptTo(SampleModelResource.class);
    assertEquals("Sample Description", sampleModelResource.getDescription());
  }

  @Test
  public void testGetDescriptionWhenPropertyIsMissing() {
    resource = context.create().resource("/sample-resource");
    sampleModelResource = resource.adaptTo(SampleModelResource.class);
    assertEquals("", sampleModelResource.getDescription());
  }
}