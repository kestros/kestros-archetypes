package ${groupId}.${artifactIdNoSpecialCharacters}.components.samplecomponent;

import io.kestros.cms.sitebuilding.api.models.BaseComponent;
import io.kestros.commons.structuredslingmodels.annotation.KestrosModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

@KestrosModel(validationService = SampleComponentValidationService.class)
@Model(adaptables = Resource.class,
       resourceType = "${artifactId}/components/content/sample-component")
public class SampleComponent extends BaseComponent {

  public String getSampleProperty() {
    return getProperty("sampleProperty", StringUtils.EMPTY);
  }
}