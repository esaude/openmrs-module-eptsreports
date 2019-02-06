package org.openmrs.module.eptsreports.reporting.calculation;

import mockit.Mock;
import mockit.MockUp;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;

public class EptsCalculationUtilsMock extends MockUp<EptsCalculationUtils> {

  PatientCalculation calculation;

  public EptsCalculationUtilsMock(PatientCalculation calculation) {
    this.calculation = calculation;
  }

  @Mock
  public Obs obsResultForPatient(CalculationResultMap results, Integer patientId) {
    return (results == null
            || results.isEmpty()
            || !results.containsKey(patientId)
            || results.get(patientId) == null)
        ? null
        : (Obs) results.get(patientId).getValue();
  }

  @Mock
  public Encounter encounterResultForPatient(CalculationResultMap results, Integer patientId) {
    return (results == null
            || results.isEmpty()
            || !results.containsKey(patientId)
            || results.get(patientId) == null)
        ? null
        : (Encounter) results.get(patientId).getValue();
  }
}
