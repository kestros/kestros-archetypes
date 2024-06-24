package ${groupId}.${artifactIdNoSpecialCharacters}.core.models;

import ${groupId}.${artifactIdNoSpecialCharacters}.api.models.SampleModel;
import javax.annotation.Nonnull;

/**
 * POJO implementation of SampleModel.
 */
public class SampleModelImpl implements SampleModel {

  private String title;
  private String description;

  /**
   * Creates a new SampleModelImpl.
   *
   * @param title Title of the Sample Model.
   * @param description Description of the Sample Model.
   */
  public SampleModelImpl(@Nonnull String title, @Nonnull String description) {
    this.title = title;
    this.description = description;
  }


  @Nonnull
  @Override
  public String getTitle() {
    return title;
  }

  @Nonnull
  @Override
  public String getDescription() {
    return description;
  }
}
