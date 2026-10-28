package ${package}.core.models;

import ${package}.api.models.SampleModel;
import java.util.Objects;
import javax.annotation.CheckForNull;
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

  @Override
  public boolean equals(@CheckForNull Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    SampleModelImpl that = (SampleModelImpl) other;
    return title.equals(that.title) && description.equals(that.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, description);
  }

  @Nonnull
  @Override
  public String toString() {
    return "SampleModelImpl{title='" + title + "', description='" + description + "'}";
  }
}
