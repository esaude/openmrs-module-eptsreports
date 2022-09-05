package org.openmrs.module.eptsreports.reporting.library.datasets;

import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.springframework.stereotype.Component;

@Component
public class TotalOfPatientsWhoPickedupArvDuringPeriodDataSet extends BaseDataSet {

  public DataSetDefinition constructDataSet() {

    PatientDataSetDefinition pdd = new PatientDataSetDefinition();
    pdd.setName("Total of patients who picked up ARV during the period");

    return pdd;
  }
}
