package org.openmrs.module.eptsreports.reporting.calculation.pvls;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.springframework.stereotype.Component;

@Component
public class BreastfeedingCalculation extends AbstractPatientCalculation {

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {

    CalculationResultMap ret = new CalculationResultMap();

    // get female patients only
    Set<Integer> femaleCohort = EptsCalculationUtils.female(cohort, context);

    CalculationResultMap breastfeedingDateMap =
        calculate(
            Context.getRegisteredComponents(BreastfeedingDateCalculation.class).get(0),
            femaleCohort,
            context);
    CalculationResultMap pregnancyDateMap =
        calculate(
            Context.getRegisteredComponents(PregnantDateCalculation.class).get(0),
            femaleCohort,
            context);

    for (Integer pId : femaleCohort) {
      boolean isBreastfeedingCandidate = false;
      Date breastfeedingDate = (Date) breastfeedingDateMap.get(pId).getValue();
      Date pregnancyDate = (Date) pregnancyDateMap.get(pId).getValue();
      if (breastfeedingDate != null) {
        isBreastfeedingCandidate = true;
      }
      if (breastfeedingDate != null
          && pregnancyDate != null
          && pregnancyDate.after(breastfeedingDate)) {
        isBreastfeedingCandidate = false;
      }
      ret.put(pId, new BooleanResult(isBreastfeedingCandidate, this));
    }
    return ret;
  }
}
