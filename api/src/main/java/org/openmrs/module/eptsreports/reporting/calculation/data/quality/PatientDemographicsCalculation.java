package org.openmrs.module.eptsreports.reporting.calculation.data.quality;

import java.util.Collection;
import java.util.Map;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.springframework.stereotype.Component;

@Component
public class PatientDemographicsCalculation extends AbstractPatientCalculation {

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

    CalculationResultMap map = new CalculationResultMap();

    for (Integer ptId : cohort) {
      Patient patient = Context.getPatientService().getPatient(ptId);
      map.put(ptId, new SimpleResult(patient, this));
    }
    return map;
  }
}
