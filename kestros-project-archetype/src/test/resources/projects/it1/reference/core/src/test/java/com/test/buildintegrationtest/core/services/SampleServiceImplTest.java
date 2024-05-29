package com.test.buildintegrationtest.core.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.test.buildintegrationtest.api.exceptions.SampleModelRetrievalException;
import com.test.buildintegrationtest.api.models.SampleModel;
import com.test.buildintegrationtest.core.models.SampleModelResource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.felix.hc.api.FormattingResultLog;
import org.apache.felix.hc.api.Result;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class SampleServiceImplTest {

  @Rule
  public SlingContext context = new SlingContext();

  private SampleServiceImpl sampleService;
  private Map<String, Object> properties = new HashMap<>();

  @Before
  public void setUp() {
    context.addModelsForPackage("com.test.buildintegrationtest");
    sampleService = spy(new SampleServiceImpl());
  }

  @Test
  public void testActivate() {
    sampleService.activate(context.componentContext());
  }

  @Test
  public void testDeactivate() {
    sampleService.deactivate(context.componentContext());
  }

  @Test
  public void testGetDisplayName() {
    assertEquals("My Sample Service", sampleService.getDisplayName());
  }

  @Test
  public void testRunAdditionalHealthChecks() {
    FormattingResultLog log = new FormattingResultLog();
    sampleService.runAdditionalHealthChecks(log);
    assertEquals(Result.Status.OK, log.getAggregateStatus());
  }

  @Test
  public void testRunAdditionalHealthChecksWhenServiceValueIsEmpty() {
    doReturn("").when(sampleService).getMyServiceValue();
    FormattingResultLog log = new FormattingResultLog();
    sampleService.runAdditionalHealthChecks(log);
    assertEquals(Result.Status.CRITICAL, log.getAggregateStatus());
  }

  @Test
  public void testGetMyServiceValue() {
    assertEquals("Hello World!", sampleService.getMyServiceValue());
  }

  @Test
  public void testGetSampleModels() throws SampleModelRetrievalException {
    properties.put("jcr:title", "Sample 1");
    properties.put("jcr:description", "Sample 1 Description");
    context.create().resource("/content/samples/sample1", properties);

    properties.put("jcr:title", "Sample 2");
    properties.put("jcr:description", "Sample 2 Description");
    context.create().resource("/content/samples/sample2", properties);

    assertEquals(2, sampleService.getSampleModels(context.resourceResolver()).size());
    assertEquals("Sample 1",
            sampleService.getSampleModels(context.resourceResolver()).get(0).getTitle());
    assertEquals("Sample 1 Description",
            sampleService.getSampleModels(context.resourceResolver()).get(0).getDescription());
    assertEquals("Sample 2",
            sampleService.getSampleModels(context.resourceResolver()).get(1).getTitle());
    assertEquals("Sample 2 Description",
            sampleService.getSampleModels(context.resourceResolver()).get(1).getDescription());
  }

  @Test
  public void testGetSampleModelsWhenSamplesRootResourceNotFound() {
    Exception exception = null;
    try {
      sampleService.getSampleModels(context.resourceResolver());
    } catch (SampleModelRetrievalException e) {
      exception = e;
    }
    assertNotNull(exception);
    assertEquals("Samples root resource was not found.", exception.getMessage());
  }

  @Test
  public void testGetSampleModelsWhenAdaptionFails() throws SampleModelRetrievalException {
    ResourceResolver resourceResolver = spy(context.resourceResolver());
    Resource samplesResource = mock(Resource.class);
    Iterable<Resource> samplesResourceChildren = mock(Iterable.class);

    when(resourceResolver.getResource("/content/samples")).thenReturn(samplesResource);
    when(samplesResource.getChildren()).thenReturn(samplesResourceChildren);
    when(samplesResourceChildren.iterator()).thenReturn(mock(java.util.Iterator.class));
    when(samplesResourceChildren.iterator().hasNext()).thenReturn(true, true, false);

    Resource failsAdaptationResource = mock(Resource.class);
    properties.put("jcr:title", "Sample 2");
    properties.put("jcr:description", "Sample 2 Description");

    Resource adaptableResource = context.create().resource("/content/samples/sample2", properties);

    when(samplesResourceChildren.iterator().next()).thenReturn(failsAdaptationResource)
                                                   .thenReturn(adaptableResource);
    when(failsAdaptationResource.adaptTo(SampleModelResource.class)).thenReturn(null);


    List<SampleModel> sampleModels = sampleService.getSampleModels(resourceResolver);
    assertEquals(1, sampleModels.size());
    assertEquals("Sample 2", sampleModels.get(0).getTitle());
    assertEquals("Sample 2 Description", sampleModels.get(0).getDescription());
    verify(failsAdaptationResource, times(1)).adaptTo(SampleModelResource.class);
  }

  @Test
  public void testGetSampleModel() throws SampleModelRetrievalException {
    properties.put("jcr:title", "Sample 1");
    properties.put("jcr:description", "Sample 1 Description");
    context.create().resource("/content/samples/sample1", properties);

    assertNotNull(sampleService.getSampleModel("sample1", context.resourceResolver()));
    assertEquals("Sample 1",
            sampleService.getSampleModel("sample1", context.resourceResolver()).getTitle());
    assertEquals("Sample 1 Description",
            sampleService.getSampleModel("sample1", context.resourceResolver()).getDescription());
  }

  @Test
  public void testGetSampleModelWhenModelAdaptionFails() throws SampleModelRetrievalException {
    ResourceResolver resourceResolver = mock(ResourceResolver.class);
    Resource resource = mock(Resource.class);
    when(resource.adaptTo(SampleModelResource.class)).thenReturn(null);
    when(resourceResolver.getResource("/content/samples/sample1")).thenReturn(resource);

    Exception exception = null;
    try {
      sampleService.getSampleModel("sample1", context.resourceResolver());
    } catch (SampleModelRetrievalException e) {
      exception = e;
    }
    assertNotNull(exception);
    assertEquals("Sample resource sample1 was not found.", exception.getMessage());
  }
}