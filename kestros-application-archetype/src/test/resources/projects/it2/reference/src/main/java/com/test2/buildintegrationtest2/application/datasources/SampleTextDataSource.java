package com.test2.buildintegrationtest2.application.datasources;

import io.kestros.cms.components.basic.api.content.KestrosText;
import io.kestros.cms.components.basic.core.BaseSlingModelDataSource;
import javax.annotation.Nullable;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

/**
 * Sample Text datasource.
 *
 * <p>Feeds programmatic data into the Kestros Text component
 * ({@code /libs/kestros/commons/components/content/text}). It supplies the paragraph text in code
 * rather than from the authored {@code text} property.</p>
 *
 * <p>It is registered to the Text component by the application module, via the
 * {@code apps/kestros/commons/components/content/text/datasources/build-integration-test2-sample-text} node (see the
 * {@code classPath} property on that node). Authors select it on a Text component instance, which
 * stores the datasource name in the {@code kes:datasource} property. The component's common view
 * resolves the selected datasource through {@code TextDataSourceComponent}.</p>
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class SampleTextDataSource extends BaseSlingModelDataSource implements KestrosText {

  @Nullable
  @Override
  public String getText() {
    return "This paragraph text is supplied programmatically by SampleTextDataSource, "
        + "selected on the Text component via the kes:datasource property.";
  }
}
