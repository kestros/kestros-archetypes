package com.test2.buildintegrationtest2.application.components;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

/**
 * Default static datasource for the Sample Component. Renders child resources
 * directly as authored content.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class SampleComponentStaticDataSource extends SampleComponent {

  /**
   * Returns child resources of this component.
   *
   * @return List of child resources.
   */
  @Nonnull
  public List<Resource> getChildren() {
    List<Resource> children = new ArrayList<>();
    for (Resource child : getResource().getChildren()) {
      children.add(child);
    }
    return children;
  }

}
