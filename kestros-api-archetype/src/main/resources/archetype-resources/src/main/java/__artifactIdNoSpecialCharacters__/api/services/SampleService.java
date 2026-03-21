package ${groupId}.${artifactIdNoSpecialCharacters}.api.services;

import javax.annotation.Nonnull;

/**
 * Sample Service interface demonstrating a basic OSGi service contract.
 */
public interface SampleService {

  /**
   * Returns the display name of the service.
   *
   * @return Display name.
   */
  @Nonnull
  String getDisplayName();

  /**
   * A sample value provided by the service.
   *
   * @return Sample value.
   */
  @Nonnull
  String getMyServiceValue();

}
