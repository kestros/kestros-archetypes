package ${groupId}.${artifactIdNoSpecialCharacters}.application.components.samplecomponent;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

/**
 * Child pages datasource for the Sample Component. Renders child pages from
 * a configured root path.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class})
public class SampleComponentChildPagesDataSource extends SampleComponent {

  @Nullable
  public String getPagesPath() {
    return getProperty("pagesPath", (String) null);
  }

  @Nonnull
  public List<Resource> getPageChildren() {
    List<Resource> pages = new ArrayList<>();
    String pagesPath = getPagesPath();
    if (pagesPath != null) {
      Resource rootResource = getResourceResolver().getResource(pagesPath);
      if (rootResource != null) {
        for (Resource child : rootResource.getChildren()) {
          pages.add(child);
        }
      }
    }
    return pages;
  }

}
