package com.test.buildintegrationtest.core.services;

import com.test.buildintegrationtest.api.exceptions.SampleModelRetrievalException;
import com.test.buildintegrationtest.api.models.SampleModel;
import com.test.buildintegrationtest.api.services.SampleService;
import com.test.buildintegrationtest.core.models.SampleModelImpl;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.hc.api.FormattingResultLog;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OSGI Component Implementation for SampleService.
 */
@Component(immediate = true,
           service = SampleService.class)
public class SampleServiceImpl implements SampleService {

  private static final Logger LOG = LoggerFactory.getLogger(SampleServiceImpl.class);

  @Override
  public void activate(ComponentContext componentContext) {
    LOG.info("Activating Sample Service");
  }

  @Override
  public void deactivate(ComponentContext componentContext) {
    LOG.info("Deactivating Sample Service");
  }

  @Override
  public String getDisplayName() {
    return "My Sample Service";
  }

  @Override
  public void runAdditionalHealthChecks(FormattingResultLog log) {
    // In the current version there is a bug where no logs added will result in a WARN status.
    log.debug("Running additional health checks.");
    if (StringUtils.isEmpty(getMyServiceValue())) {
      log.critical("My Service Value is empty.");
    }
  }

  @Nonnull
  @Override
  public String getMyServiceValue() {
    return "Hello World!";
  }

  @Nonnull
  @Override
  public List<SampleModel> getSampleModels(@Nonnull ResourceResolver resourceResolver) throws
          SampleModelRetrievalException {
    return Arrays.asList(new SampleModelImpl("1", "Sample Model 1"),
                         new SampleModelImpl("2", "Sample Model 2"));
  }

  @Nonnull
  @Override
  public SampleModel getSampleModel(@Nonnull String modelId,
          @Nonnull ResourceResolver resourceResolver) {
    return new SampleModelImpl(modelId, "Sample Model " + modelId);
  }
}