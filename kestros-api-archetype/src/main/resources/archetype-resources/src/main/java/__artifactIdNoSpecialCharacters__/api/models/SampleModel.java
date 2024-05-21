package ${groupId}.${artifactIdNoSpecialCharacters}.api.models;

import javax.annotation.Nonnull;

/**
 * Sample Model.
 */
public interface SampleModel {

  /**
   * Retrieves the title of the Sample Model.
   *
   * @return Title of the Sample Model.
   */
  @Nonnull
  String getTitle();

  /**
   * Retrieves the description of the Sample Model.
   *
   * @return Description of the Sample Model.
   */
  @Nonnull
  String getDescription();

}