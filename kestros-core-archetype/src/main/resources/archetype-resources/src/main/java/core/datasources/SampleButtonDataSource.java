package ${package}.core.datasources;

import io.kestros.cms.components.basic.api.content.AnchorTarget;
import io.kestros.cms.components.basic.api.content.KestrosButton;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

/**
 * Sample Button datasource.
 *
 * <p>Feeds programmatic data into the Kestros Button component
 * ({@code /libs/kestros/commons/components/content/button}). It supplies the button's text, href
 * and title in code rather than from authored properties.</p>
 *
 * <p>It is registered to the Button component by the application module, via the
 * {@code apps/kestros/commons/components/content/button/datasources/${artifactId}-sample-button} node (see
 * the {@code classPath} property on that node). Authors select it on a Button component instance,
 * which stores the datasource name in the {@code kes:datasource} property. The component's common
 * view resolves the selected datasource through {@code ButtonDataSourceComponent}.</p>
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class SampleButtonDataSource extends BaseSlingModelDataSource implements KestrosButton {

  @Nullable
  @Override
  public String getText() {
    return "Sample Button from a datasource";
  }

  @Nullable
  @Override
  public String getHref() {
    return "https://kestros.io";
  }

  @Nullable
  @Override
  public String getTitle() {
    return "Visit kestros.io";
  }

  @Nonnull
  @Override
  public AnchorTarget getTarget() {
    return AnchorTarget.NEW_WINDOW;
  }

  @Nullable
  @Override
  public String getRel() {
    return "noopener";
  }

  @Nullable
  @Override
  public String getAriaLabel() {
    return "Sample button supplied by a datasource";
  }

  @Nullable
  @Override
  public String getAriaDescribedBy() {
    return null;
  }

  @Nullable
  @Override
  public String getLang() {
    return null;
  }

  @Override
  public boolean isDisabled() {
    return false;
  }
}
