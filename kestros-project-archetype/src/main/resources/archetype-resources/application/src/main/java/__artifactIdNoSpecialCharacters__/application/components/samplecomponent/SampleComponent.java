package ${groupId}.${artifactIdNoSpecialCharacters}.application.components.samplecomponent;

import ${groupId}.${artifactIdNoSpecialCharacters}.api.services.SampleService;
import io.kestros.cms.sitebuilding.api.models.BaseComponent;
import io.kestros.commons.structuredslingmodels.annotation.KestrosModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

@KestrosModel()
@Model(adaptables = Resource.class,
       resourceType = "${artifactId}/components/content/sample-component")
public class SampleComponent extends BaseComponent {

  @OSGiService
  @Optional
  private SampleService sampleServive;

  public String getMyServiceValue() {
    if(sampleServive != null) {
      return sampleServive.getMyServiceValue();
    }
    return StringUtils.EMPTY;
  }

  public String getSampleProperty() {
    return getProperty("sampleProperty", StringUtils.EMPTY);
  }
}