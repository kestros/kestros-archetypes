package ${groupId}.${artifactIdNoSpecialCharacters}.core.services;

import ${groupId}.${artifactIdNoSpecialCharacters}.api.services.SampleService;
import javax.annotation.Nonnull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of the SampleService.
 */
@Component(service = SampleService.class,
           immediate = true)
public class SampleServiceImpl implements SampleService {

  private static final Logger LOG = LoggerFactory.getLogger(SampleServiceImpl.class);

  @Nonnull
  @Override
  public String getDisplayName() {
    return "Sample Service";
  }

  @Nonnull
  @Override
  public String getMyServiceValue() {
    return "My Service Value.";
  }

  @Activate
  protected void activate() {
    LOG.info("SampleService activated.");
  }

  @Deactivate
  protected void deactivate() {
    LOG.info("SampleService deactivated.");
  }

}
