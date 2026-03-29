package ${groupId}.${artifactIdNoSpecialCharacters}.application.components;

import ${groupId}.${artifactIdNoSpecialCharacters}.api.services.SampleService;
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
        resourceType = "${artifactId}/components/content/sample-component")
public class SampleComponent extends BaseComponent {

  @OSGiService
  @Optional
  private SampleService sampleService;

  /**
   * Retrieves a value from the SampleService.
   *
   * @return Value from the SampleService.
   */
  public String getMyServiceValue() {
    if (sampleService != null) {
      return sampleService.getMyServiceValue();
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

  /**
   * Returns the display style for this component.
   *
   * @return Display style value, defaults to "default".
   */
  @KestrosProperty(jcrPropertyName = "displayStyle",
          defaultValue = "default",
          description = "Controls the visual style of the component.",
          configurable = true,
          sampleValue = "highlight")
  public String getDisplayStyle() {
    return getProperty("displayStyle", "default");
  }

  /**
   * Whether the icon should be displayed.
   *
   * @return true if the icon should be shown.
   */
  @KestrosProperty(jcrPropertyName = "showIcon",
          defaultValue = "false",
          description = "Toggles icon visibility.",
          configurable = true,
          sampleValue = "true")
  public boolean isShowIcon() {
    return getProperty("showIcon", false);
  }
}
