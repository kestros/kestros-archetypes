package com.test2.buildintegrationtest2.core.datasources;

import io.kestros.cms.components.basic.api.content.KestrosButtonGroup;
import io.kestros.cms.components.basic.api.content.KestrosCard;
import io.kestros.cms.components.basic.api.content.KestrosHeading;
import io.kestros.cms.components.basic.api.content.KestrosImage;
import io.kestros.cms.components.basic.api.exceptions.ComponentConfigurationException;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
import io.kestros.cms.components.basic.core.content.heading.KestrosHeadingImpl;
import javax.annotation.Nullable;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sample single-Card datasource.
 *
 * <p>Feeds programmatic (synthetic) data into a single Kestros Card component
 * ({@code /libs/kestros/commons/components/content/card}). It supplies a title heading and a
 * description in code; the image and button group are omitted (null).</p>
 *
 * <p>It is registered to the Card component by the application module, via the
 * {@code apps/kestros/commons/components/content/card/datasources/build-integration-test2-sample-card} node (see the
 * {@code classPath} property on that node). Authors select it on a Card component instance, which
 * stores the datasource name in the {@code kes:datasource} property. The component's common view
 * resolves the selected datasource through {@code CardDataSourceComponent}.</p>
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class SampleCardDataSource extends BaseContainerSlingModelDataSource
    implements KestrosCard {

  private static final Logger LOG = LoggerFactory.getLogger(SampleCardDataSource.class);

  /**
   * Card title heading, built in code as an {@code h3}.
   *
   * @return Title heading element, or null if it could not be built.
   */
  @Nullable
  @Override
  public KestrosHeading getTitleElement() {
    try {
      return new KestrosHeadingImpl("Sample Card from a datasource", "h3", this, "title",
          "titleElement");
    } catch (final ComponentConfigurationException e) {
      LOG.error("Failed to build sample card title. {}", e.getMessage());
      return null;
    }
  }

  /**
   * Card description text.
   *
   * @return Description string.
   */
  @Nullable
  @Override
  public String getDescription() {
    return "This card's title and description are supplied programmatically by "
        + "SampleCardDataSource, selected on the component via kes:datasource.";
  }

  /**
   * No image is provided by this sample.
   *
   * @return null.
   */
  @Nullable
  @Override
  public KestrosImage getImageElement() {
    return null;
  }

  /**
   * No button group is provided by this sample.
   *
   * @return null.
   */
  @Nullable
  @Override
  public KestrosButtonGroup getButtonGroupElement() {
    return null;
  }
}
