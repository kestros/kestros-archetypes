package ${groupId}.${artifactIdNoSpecialCharacters}.components.samplecomponent;

import io.kestros.commons.structuredslingmodels.validation.ModelValidationMessageType;
import io.kestros.commons.structuredslingmodels.validation.ModelValidationService;
import io.kestros.commons.structuredslingmodels.validation.ModelValidator;
import org.apache.commons.lang3.StringUtils;

public class SampleComponentValidationService extends ModelValidationService {

  @Override
  public SampleComponent getModel() {
    return (SampleComponent) getGenericModel();
  }

  @Override
  public void registerBasicValidators() {
    hasSampleProperty();
  }

  @Override
  public void registerDetailedValidators() {

  }

  public ModelValidator hasSampleProperty() {
    return new ModelValidator() {
      @Override
      public boolean isValid() {
        return StringUtils.isEmpty(getModel().getSampleProperty());
      }

      @Override
      public String getMessage() {
        return "Sample value is configured";
      }

      @Override
      public ModelValidationMessageType getType() {
        return ModelValidationMessageType.ERROR;
      }
    };
  }
}
