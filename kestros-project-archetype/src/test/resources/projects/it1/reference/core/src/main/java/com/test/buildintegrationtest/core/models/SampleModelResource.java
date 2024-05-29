package com.test.buildintegrationtest.core.models;

import com.test.buildintegrationtest.api.models.SampleModel;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;


/**
 * The @Model annotation allows resources to be adapted to this type. Optionally, the resourceType
 * property can be added in the annotation to tie this model to a specific resource type.
 * If the
 * resourceType is declared, the SlingModelUtils class can be used to dynamically create the
 * correct object based on the resource type, find Resources of a specific type, and more.
 *
 * <p>For more information on Sling Models, see
 * https://sling.apache.org/documentation/bundles/models.html
 */
@Model(adaptables = Resource.class)
public class SampleModelResource implements SampleModel {

  @Self
  private Resource resource;

  @Nonnull
  @Override
  public String getTitle() {
    // There are more ways to get a value from a resource. It is recommended that you read the
    // Sling Model documentation for more examples.
    return resource.getValueMap().get("jcr:title", StringUtils.EMPTY);
  }

  @Nonnull
  @Override
  public String getDescription() {
    return resource.getValueMap().get("jcr:description", StringUtils.EMPTY);
  }
}
