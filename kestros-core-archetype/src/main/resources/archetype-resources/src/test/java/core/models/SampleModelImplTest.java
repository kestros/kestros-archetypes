package ${package}.core.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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

  @Test
  public void testToString() {
    sampleModelImpl = new SampleModelImpl("title", "description");
    assertEquals("SampleModelImpl{title='title', description='description'}",
                 sampleModelImpl.toString());
  }

  @Test
  public void testEqualsWhenSameValues() {
    sampleModelImpl = new SampleModelImpl("title", "description");
    SampleModelImpl other = new SampleModelImpl("title", "description");
    assertEquals(sampleModelImpl, other);
    assertEquals(sampleModelImpl.hashCode(), other.hashCode());
  }

  @Test
  public void testEqualsWhenDifferentValues() {
    sampleModelImpl = new SampleModelImpl("title", "description");
    assertNotEquals(sampleModelImpl, new SampleModelImpl("other", "description"));
    assertNotEquals(sampleModelImpl, new SampleModelImpl("title", "other"));
  }

  @Test
  public void testEqualsWhenNullOrOtherType() {
    sampleModelImpl = new SampleModelImpl("title", "description");
    assertNotEquals(sampleModelImpl, null);
    assertNotEquals(sampleModelImpl, "title");
  }
}
