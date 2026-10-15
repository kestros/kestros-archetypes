package ${package}.application.datasources;

import io.kestros.cms.components.basic.api.exceptions.ComponentConfigurationException;
import io.kestros.cms.components.basic.core.BaseContainerSlingModelDataSource;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Shared base for the sample container datasources (Card, Card List and Table).
 *
 * <p>Holds the logger and the "build one element, log and skip it on failure" step that each of
 * them would otherwise repeat. The logger is created from the concrete class, so log output is
 * still categorised under the datasource that wrote it.</p>
 */
public abstract class BaseSampleContainerDataSource extends BaseContainerSlingModelDataSource {

  private final Logger log = LoggerFactory.getLogger(getClass());

  /**
   * Builds one element of the datasource's output.
   *
   * @param <T> Element type.
   */
  @FunctionalInterface
  protected interface ElementBuilder<T> {

    /**
     * Builds the element.
     *
     * @return The built element.
     * @throws ComponentConfigurationException The element could not be built.
     */
    T build() throws ComponentConfigurationException;
  }

  /**
   * Logger for the concrete datasource.
   *
   * @return Logger categorised under the concrete datasource class.
   */
  protected Logger getLog() {
    return log;
  }

  /**
   * Builds an element, logging and returning null if it cannot be built. Callers skip a null
   * result rather than adding it.
   *
   * @param builder Builds the element.
   * @param what Description of the element, used in the error log.
   * @param <T> Element type.
   * @return The built element, or null if it could not be built.
   */
  @Nullable
  protected <T> T buildOrNull(final ElementBuilder<T> builder, final String what) {
    try {
      return builder.build();
    } catch (final ComponentConfigurationException e) {
      getLog().error("Failed to build {}. {}", what, e.getMessage());
      return null;
    }
  }
}
