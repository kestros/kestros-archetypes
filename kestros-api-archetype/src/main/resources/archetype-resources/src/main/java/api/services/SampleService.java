package ${package}.api.services;

import ${package}.api.exceptions.SampleModelRetrievalException;
import ${package}.api.models.SampleModel;
import io.kestros.commons.osgiserviceutils.services.ManagedService;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.sling.api.resource.ResourceResolver;

/**
 * Sample Service for retrieving Sample Models.
 */
public interface SampleService extends ManagedService {

  /**
   * A sample value to be used by the service.
   *
   * @return Sample value.
   */
  @Nonnull
  String getMyServiceValue();

  /**
   * Retrieves all Sample Models.
   *
   * @param resourceResolver ResourceResolver to use for retrieving Sample Models.
   *
   * @return List of Sample Models.
   * @throws SampleModelRetrievalException Thrown when an error occurs retrieving Sample Models.
   */
  @Nonnull
  List<SampleModel> getSampleModels(@Nonnull ResourceResolver resourceResolver) throws
          SampleModelRetrievalException;

  /**
   * Retrieves a Sample Model by its ID.
   *
   * @param modelId ID of the Sample Model to retrieve.
   * @param resourceResolver ResourceResolver to use for retrieving the Sample Model.
   *
   * @return Sample Model with the provided ID.
   */
  @Nonnull
  SampleModel getSampleModel(@Nonnull String modelId, @Nonnull ResourceResolver resourceResolver);

}