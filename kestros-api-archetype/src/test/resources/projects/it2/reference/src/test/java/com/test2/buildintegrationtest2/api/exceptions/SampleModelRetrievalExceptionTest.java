package com.test2.buildintegrationtest2.api.exceptions;

import junit.framework.TestCase;
import org.junit.Test;

public class SampleModelRetrievalExceptionTest extends TestCase {

  private SampleModelRetrievalException sampleModelRetrievalException;

  @Test
  public void testSampleModelRetrievalException() {
    sampleModelRetrievalException = new SampleModelRetrievalException("message");
    assertEquals("message", sampleModelRetrievalException.getMessage());
  }

  @Test
  public void testSampleModelRetrievalExceptionWithCause() {
    Throwable cause = new Throwable();
    sampleModelRetrievalException = new SampleModelRetrievalException("message", cause);
    assertEquals("message", sampleModelRetrievalException.getMessage());
    assertEquals(cause, sampleModelRetrievalException.getCause());
  }

  @Test
  public void testSampleModelRetrievalExceptionWithThrowable() {
    Throwable cause = new Throwable();
    sampleModelRetrievalException = new SampleModelRetrievalException(cause);
    assertEquals(cause, sampleModelRetrievalException.getCause());
  }

}