package ${package}.core.services;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import ${package}.api.exceptions.SampleModelRetrievalException;
import org.apache.felix.hc.api.FormattingResultLog;
import org.apache.felix.hc.api.Result;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class SampleServiceImplTest {

  @Rule
  public SlingContext context = new SlingContext();

  private SampleServiceImpl sampleService;

  @Before
  public void setUp() {
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
    assertEquals(2, sampleService.getSampleModels(context.resourceResolver()).size());
  }

  @Test
  public void testGetSampleModel() {
    assertNotNull(sampleService.getSampleModel("1", context.resourceResolver()));
  }
}