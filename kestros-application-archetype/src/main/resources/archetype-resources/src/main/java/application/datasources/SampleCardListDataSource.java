package ${package}.application.datasources;

import ${package}.api.exceptions.SampleModelRetrievalException;
import ${package}.api.models.SampleModel;
import ${package}.api.services.SampleService;
import io.kestros.cms.components.basic.api.content.KestrosCard;
import io.kestros.cms.components.basic.api.exceptions.ComponentConfigurationException;
import io.kestros.cms.components.basic.api.lists.KestrosCardList;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
import io.kestros.cms.components.basic.core.content.card.KestrosCardImpl;
import io.kestros.cms.components.basic.core.content.heading.KestrosHeadingImpl;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sample Card List datasource.
 *
 * <p>Datasources feed data into components that render lists (Card List, Table, etc). This
 * datasource pulls Sample Models from the {@link SampleService} OSGi service and turns each one
 * into a card for the Card List component
 * ({@code /libs/kestros/commons/components/lists/card-list}).</p>
 *
 * <p>It is registered to the Card List component by the application module, via the
 * {@code apps/kestros/commons/components/lists/card-list/datasources/${artifactId}-sample-cards}
 * node (see the {@code classPath} property on that node). Authors select it on a Card List
 * component instance, which stores the datasource name in the {@code kes:datasource} property.</p>
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class SampleCardListDataSource extends BaseContainerSlingModelDataSource
    implements KestrosCardList {

  private static final Logger LOG = LoggerFactory.getLogger(SampleCardListDataSource.class);

  @OSGiService
  @Optional
  private SampleService sampleService;

  /**
   * One card per Sample Model provided by the SampleService.
   *
   * @return Cards to render in the Card List component.
   */
  @Nonnull
  @Override
  public List<KestrosCard> getCardElements() {
    List<KestrosCard> cards = new ArrayList<>();
    if (sampleService == null) {
      LOG.warn("SampleService is not available. Rendering an empty card list.");
      return cards;
    }
    List<SampleModel> sampleModels;
    try {
      sampleModels = sampleService.getSampleModels(getResourceResolver());
    } catch (final SampleModelRetrievalException e) {
      LOG.error("Failed to retrieve Sample Models. Rendering an empty card list. {}",
          e.getMessage());
      return cards;
    }
    int index = 0;
    for (final SampleModel sampleModel : sampleModels) {
      try {
        cards.add(new KestrosCardImpl(sampleModel.getDescription(),
            new KestrosHeadingImpl(sampleModel.getTitle(), "h3", this, "title", "title-" + index),
            null, null, this, "card", "sample-card-" + index));
        index++;
      } catch (final ComponentConfigurationException e) {
        LOG.error("Failed to build card for Sample Model {}. {}", sampleModel.getTitle(),
            e.getMessage());
      }
    }
    return cards;
  }
}
