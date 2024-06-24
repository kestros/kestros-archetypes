package com.test2.buildintegrationtest2.core.models;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SampleModelImplTest {

  private SampleModelImpl sampleModelImpl;

  @Test
  public void testGetTitle() {
    sampleModelImpl = new SampleModelImpl("title", "description");
    assertEquals("title", sampleModelImpl.getTitle());
  }

  @Test
  public void testGetDescription() {
    sampleModelImpl = new SampleModelImpl("title", "description");
    assertEquals("description", sampleModelImpl.getDescription());
  }
}