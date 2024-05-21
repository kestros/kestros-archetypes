package ${groupId}.${artifactIdNoSpecialCharacters}.core.services;

import ${groupId}.${artifactIdNoSpecialCharacters}.api.services.SampleService;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.apache.felix.hc.api.FormattingResultLog;


@Component(immediate = true,
           service = SampleService.class)
public class SampleServiceImpl implements SampleService {

  @Override
  public String getMyServiceValue() {
    return "My Service Value.";
  }

  @Override
  public void activate(ComponentContext componentContext) {

  }

  @Override
  public void deactivate(ComponentContext componentContext) {

  }

  @Override
  public String getDisplayName() {
    return "My Sample Service";
  }

  @Override
  public void runAdditionalHealthChecks(FormattingResultLog log) {
    if (StringUtils.isEmpty(getMyServiceValue())) {
      log.critical("My Service Value is empty.");
    }
  }

}
