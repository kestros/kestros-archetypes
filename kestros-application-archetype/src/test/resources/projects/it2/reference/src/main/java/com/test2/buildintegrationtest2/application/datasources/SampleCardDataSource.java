package com.test2.buildintegrationtest2.application.datasources;

import io.kestros.cms.components.basic.api.content.KestrosButtonGroup;
import io.kestros.cms.components.basic.api.content.KestrosCard;
import io.kestros.cms.components.basic.api.content.KestrosHeading;
import io.kestros.cms.components.basic.api.content.KestrosImage;
import io.kestros.cms.components.basic.core.content.heading.KestrosHeadingImpl;
import javax.annotation.Nullable;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

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
public class SampleCardDataSource extends BaseSampleContainerDataSource
    implements KestrosCard {

  /**
   * Card title heading, built in code as an {@code h3}.
   *
   * @return Title heading element, or null if it could not be built.
   */
  @Nullable
  @Override
  public KestrosHeading getTitleElement() {
    return buildOrNull(() -> new KestrosHeadingImpl(SampleContent.CARD_TITLE, "h3", this, "title",
        "titleElement"), "sample card title");
  }

  /**
   * Card description text.
   *
   * @return Description string.
   */
  @Nullable
  @Override
  public String getDescription() {
    return SampleContent.CARD_DESCRIPTION;
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
