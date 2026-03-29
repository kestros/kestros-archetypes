package com.test2.buildintegrationtest2.core.services;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class SampleServiceImplTest {

  private SampleServiceImpl sampleService;

  @Before
  public void setUp() {
    sampleService = new SampleServiceImpl();
  }

  @Test
  public void testGetDisplayName() {
    assertEquals("Sample Service", sampleService.getDisplayName());
  }

  @Test
  public void testGetMyServiceValue() {
    assertEquals("My Service Value.", sampleService.getMyServiceValue());
  }

}
