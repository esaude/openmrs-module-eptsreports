package org.openmrs.module.eptsreports.reporting.library.datasets.dqa;

import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.utils.EptsQuerysUtils;
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
}
