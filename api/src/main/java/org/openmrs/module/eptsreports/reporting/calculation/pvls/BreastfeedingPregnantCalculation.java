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
import org.openmrs.module.eptsreports.reporting.utils.EptsReportConstants.PregnantOrBreastfeedingWomen;
import org.springframework.stereotype.Component;

@Component
public class BreastfeedingPregnantCalculation extends AbstractPatientCalculation {

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {

    // External Dependencies
    PregnantDateCalculation pregnantDateCalculation =
        Context.getRegisteredComponents(PregnantDateCalculation.class).get(0);
    BreastfeedingDateCalculation breastfeedingDateCalculation =
        Context.getRegisteredComponents(BreastfeedingDateCalculation.class).get(0);

    CalculationResultMap resultMap = new CalculationResultMap();

    PregnantOrBreastfeedingWomen state =
        (PregnantOrBreastfeedingWomen) parameterValues.get("state");

    // get female patients only
    Set<Integer> femaleCohort = EptsCalculationUtils.female(cohort, context);

    CalculationResultMap pregnantDateMap =
        calculate(pregnantDateCalculation, femaleCohort, context);
    CalculationResultMap breastfeedingDateMap =
        calculate(breastfeedingDateCalculation, femaleCohort, context);
    for (Integer ptId : femaleCohort) {
      boolean isCandidate = false;
      Date pregnancyDate = (Date) pregnantDateMap.get(ptId).getValue();
      Date breastfeedingDate = (Date) breastfeedingDateMap.get(ptId).getValue();
      if (state.equals(PregnantOrBreastfeedingWomen.PREGNANTWOMEN) && pregnancyDate != null) {

        isCandidate = breastfeedingDate == null || breastfeedingDate.before(pregnancyDate);

      } else if (state.equals(PregnantOrBreastfeedingWomen.BREASTFEEDINGWOMEN)
          && breastfeedingDate != null) {
        isCandidate = pregnancyDate == null || pregnancyDate.before(breastfeedingDate);
      }
      resultMap.put(ptId, new BooleanResult(isCandidate, this));
    }
    return resultMap;
  }
}
