package org.openmrs.module.eptsreports.reporting.library.datasets.data.quality;

import java.util.List;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CustomConfigurationsDataDefinition;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

@Component
public class GetCustomConfigurationDataset extends BaseDataSet {

  public DataSetDefinition configDataSetDefinition(List<Parameter> parameterList) {
    CustomConfigurationsDataDefinition dsd = new CustomConfigurationsDataDefinition();
    dsd.setName("EC01");
    dsd.addParameters(parameterList);
    return dsd;
  }
}
