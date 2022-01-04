package org.openmrs.module.eptsreports.reporting.calculation.pvls;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openmrs.EncounterType;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportConstants.PregnantOrBreastfeedingWomen;
import org.springframework.stereotype.Component;

/**
 * <b>Description</b> Combines Pregancy and breastfeeding calculation Filters the results based on
 * the state passed as a calculation variable
 *
 * @return CalculationResultMap
 */
@Component
public class BreastfeedingPregnantCalculation extends AbstractPatientCalculation {

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {
    List<EncounterType> encounterTypeList =
        (List<EncounterType>) parameterValues.get("encounterTypeList");
    parameterValues.put("encounterList", encounterTypeList);

    // External Dependencies
    CalculationResultMap pregnantDateMap =
        calculate(
            Context.getRegisteredComponents(PregnantDateCalculation.class).get(0),
            cohort,
            parameterValues,
            context);
    CalculationResultMap breastfeedingDateMap =
        calculate(
            Context.getRegisteredComponents(BreastfeedingDateCalculation.class).get(0),
            cohort,
            parameterValues,
            context);
    /*PregnantDateCalculation pregnantDateCalculation =
        Context.getRegisteredComponents(PregnantDateCalculation.class).get(0);
    BreastfeedingDateCalculation breastfeedingDateCalculation =
        Context.getRegisteredComponents(BreastfeedingDateCalculation.class).get(0);*/

    CalculationResultMap resultMap = new CalculationResultMap();

    PregnantOrBreastfeedingWomen state =
        (PregnantOrBreastfeedingWomen) parameterValues.get("state");

    /*CalculationResultMap pregnantDateMap = calculate(pregnantDateCalculation, cohort, context);
    CalculationResultMap breastfeedingDateMap =
        calculate(breastfeedingDateCalculation, cohort, context);*/
    // get female patients only
    Set<Integer> femaleCohort = EptsCalculationUtils.female(cohort, context);
    for (Integer ptId : cohort) {

      boolean isCandidate = false;
      Date pregnancyDate = (Date) pregnantDateMap.get(ptId).getValue();
      Date breastfeedingDate = (Date) breastfeedingDateMap.get(ptId).getValue();
      if (state.equals(PregnantOrBreastfeedingWomen.PREGNANTWOMEN)
          && pregnancyDate != null
          && femaleCohort.contains(ptId)
          && (breastfeedingDate == null || breastfeedingDate.compareTo(pregnancyDate) <= 0)) {

        isCandidate = true;

      } else if (state.equals(PregnantOrBreastfeedingWomen.BREASTFEEDINGWOMEN)
          && breastfeedingDate != null
          && femaleCohort.contains(ptId)
          && (pregnancyDate == null || pregnancyDate.compareTo(breastfeedingDate) < 0)) {
        isCandidate = true;
      }
      resultMap.put(ptId, new BooleanResult(isCandidate, this));
    }
    return resultMap;
  }
}
