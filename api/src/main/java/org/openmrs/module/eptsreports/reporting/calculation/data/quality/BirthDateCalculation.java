package org.openmrs.module.eptsreports.reporting.calculation.data.quality;

import java.util.Collection;
import java.util.Map;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.common.Birthdate;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.springframework.stereotype.Component;

@Component
public class BirthDateCalculation extends AbstractPatientCalculation {
  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

    CalculationResultMap map = new CalculationResultMap();
    CalculationResultMap birthDates =
        EptsCalculationUtils.evaluateWithReporting(
            new BirthdateDataDefinition(), cohort, null, null, context);
    for (Integer ptId : cohort) {
      boolean birthDateEstimated = false;
      CalculationResult patientBirthDateResult = birthDates.get(ptId);
      if (patientBirthDateResult != null) {
        Birthdate birthdate = (Birthdate) patientBirthDateResult.getValue();
        if (birthdate != null && birthdate.isEstimated()) {
          birthDateEstimated = true;
        }
      }
      map.put(ptId, new BooleanResult(birthDateEstimated, this));
    }
    return map;
  }
}
