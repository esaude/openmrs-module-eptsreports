package org.openmrs.module.eptsreports.reporting.calculation.mq;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.joda.time.LocalDate;
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
public class BreastfeedingPregnantCalculation4MQ extends AbstractPatientCalculation {

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
            Context.getRegisteredComponents(PregnantDateCalculation4MQ.class).get(0),
            cohort,
            parameterValues,
            context);
    CalculationResultMap breastfeedingDateMap =
        calculate(
            Context.getRegisteredComponents(BreastfeedingDateCalculation4MQ.class).get(0),
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
      Date pregnancyDate = null;
      if (pregnantDateMap.get(ptId) != null && pregnantDateMap.get(ptId).getValue() != null) {
        pregnancyDate = new LocalDate(pregnantDateMap.get(ptId).getValue()).toDate();
      }
      Date breastfeedingDate = null;
      if (breastfeedingDateMap.get(ptId) != null
          && breastfeedingDateMap.get(ptId).getValue() != null) {
        breastfeedingDate = new LocalDate(breastfeedingDateMap.get(ptId).getValue()).toDate();
      }
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
