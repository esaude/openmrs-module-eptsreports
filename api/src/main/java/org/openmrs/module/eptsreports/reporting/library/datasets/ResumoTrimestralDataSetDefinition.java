package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

@Component
public class ResumoTrimestralDataSetDefinition extends BaseDataSet {

  public DataSetDefinition constructResumoTrimestralDataset() {
    return new CohortIndicatorDataSetDefinition();
  }

  @Override
  public List<Parameter> getParameters() {
    ArrayList<Parameter> parameters = new ArrayList<>();
    parameters.add(new Parameter("quarter", "Quarter", Integer.class));
    parameters.add(new Parameter("endDate", "End date", Date.class));
    return parameters;
  }
}
