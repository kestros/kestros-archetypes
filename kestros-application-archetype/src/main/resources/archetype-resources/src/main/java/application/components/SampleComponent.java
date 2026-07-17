package ${package}.application.components;

import ${package}.api.services.SampleService;
import io.kestros.cms.sitebuilding.api.models.BaseComponent;
import io.kestros.commons.structuredslingmodels.annotation.KestrosModel;
import io.kestros.commons.structuredslingmodels.annotation.KestrosProperty;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

/**
 * The Sling Model for a sample component.
 */
@KestrosModel()
@Model(adaptables = Resource.class,
        resourceType = "${artifactId}/components/sample-component")
public class SampleComponent extends BaseComponent {

  @OSGiService
  @Optional
  private SampleService sampleServive;

  /**
   * Retrieves a value from the SampleService.
   *
   * @return Value from the SampleService.
   */
  public String getMyServiceValue() {
    if (sampleServive != null) {
      return sampleServive.getMyServiceValue();
    }
    return StringUtils.EMPTY;
  }

  /**
   * Retrieves the value of a sample property, which is configured in the component's edit dialog.
   *
   * <p>The {@link KestrosProperty} annotation is used to provide a description about the property,
   * for the automated documentation built into Kestros.
   *
   * @return Value of the sample property.
   */
  @KestrosProperty(jcrPropertyName = "sampleProperty",
          defaultValue = "",
          description = "This is a sample property, which can be edited in the component's edit "
                  + "dialog.",
          configurable = true,
          sampleValue = "Hello World!")
  public String getSampleProperty() {
    return getProperty("sampleProperty", StringUtils.EMPTY);
  }
}