package org.openmrs.module.eptsreports.reporting.library.datasets.txnew;

import java.util.List;
import org.openmrs.module.eptsreports.reporting.library.datasets.BaseDataSet;
import org.openmrs.module.eptsreports.reporting.utils.EptsQuerysUtils;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

@Component
public class ListOfPatientWhoStartArtDataSet extends BaseDataSet {

  private static final String FIND_PATIENTS_IN_ART =
      "ART-INITIATION/LIST_OF_PATIENTS_IN_ART_COHORT.sql";

  public DataSetDefinition constructDataset(List<Parameter> list) {

    SqlDataSetDefinition dsd = new SqlDataSetDefinition();
    dsd.setName("TX NEW");
    dsd.addParameters(list);
    dsd.setSqlQuery(EptsQuerysUtils.loadQuery(FIND_PATIENTS_IN_ART));
    return dsd;
  }
}
