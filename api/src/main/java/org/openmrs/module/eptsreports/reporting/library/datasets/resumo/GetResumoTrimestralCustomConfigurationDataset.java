package org.openmrs.module.eptsreports.reporting.library.datasets.resumo;

import java.util.List;
import org.openmrs.module.eptsreports.reporting.cohort.definition.ResumoTrimestralCustomConfigurationsDataDefinition;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

@Component
public class GetResumoTrimestralCustomConfigurationDataset extends BaseDataSet {

  public DataSetDefinition configDataSetDefinition(List<Parameter> parameterList) {
    ResumoTrimestralCustomConfigurationsDataDefinition dsd =
        new ResumoTrimestralCustomConfigurationsDataDefinition();
    dsd.setName("RTM");
    dsd.addParameters(parameterList);
    return dsd;
  }
}
