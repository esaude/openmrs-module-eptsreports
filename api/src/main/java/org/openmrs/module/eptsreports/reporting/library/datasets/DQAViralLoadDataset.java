package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.ArrayList;
import java.util.List;
import org.openmrs.module.eptsreports.reporting.utils.EptsQuerysUtils;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

@Component
public class DQAViralLoadDataset extends BaseDataSet {

  private static final String NUMBER_OF_ACTIVE_PATIENTS_IN_ART_WHO_RECEIVED_VIRAL_LOAD_TEST_RESULT =
      "DQA/NUMBER_OF_ACTIVE_PATIENTS_IN_ART_WHO_RECEIVED_VIRAL_LOAD_TEST_RESULT.sql";

  public DataSetDefinition constructDataset(List<Parameter> list) {

    SqlDataSetDefinition dsd = new SqlDataSetDefinition();
    dsd.setName("DQA CV");
    dsd.addParameters(list);
    dsd.setSqlQuery(
        EptsQuerysUtils.loadQuery(
            NUMBER_OF_ACTIVE_PATIENTS_IN_ART_WHO_RECEIVED_VIRAL_LOAD_TEST_RESULT));
    return dsd;
  }

  public List<Parameter> getParameters() {
    List<Parameter> parameters = new ArrayList<Parameter>();
    parameters.add(ReportingConstants.START_DATE_PARAMETER);
    parameters.add(ReportingConstants.END_DATE_PARAMETER);
    parameters.add(ReportingConstants.LOCATION_PARAMETER);
    return parameters;
  }
}
