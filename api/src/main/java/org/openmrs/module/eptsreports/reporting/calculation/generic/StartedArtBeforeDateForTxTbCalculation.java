package org.openmrs.module.eptsreports.reporting.calculation.generic;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;
import org.springframework.stereotype.Component;

@Component
public class StartedArtBeforeDateForTxTbCalculation extends AbstractPatientCalculation {

  private static final String ON_OR_BEFORE = "onOrBefore";
  private static final String ON_OR_AFTER = "onOrAfter";

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {
    CalculationResultMap map = new CalculationResultMap();
    CalculationResultMap artStartDates =
        calculate(
            Context.getRegisteredComponents(InitialArtStartDateCalculation.class).get(0),
            cohort,
            parameterValues,
            context);
    Date endDate = (Date) parameterValues.get(ON_OR_BEFORE);
    Date onOrAfter = (Date) context.getFromCache(ON_OR_AFTER);
    if (onOrAfter != null) {
      onOrAfter = DateUtil.adjustDate(onOrAfter, -1, DurationUnit.DAYS);
    }
    if (endDate == null) {
      endDate = (Date) context.getFromCache(ON_OR_BEFORE);
    }
    if (endDate != null) {
      for (Integer patientId : cohort) {
        Date artStartDate =
            InitialArtStartDateCalculation.getArtStartDate(patientId, artStartDates);
        if (artStartDate != null && artStartDate.compareTo(onOrAfter) <= 0) {
          map.put(patientId, new BooleanResult(true, this));
        }
      }
      return map;
    } else {
      throw new IllegalArgumentException(String.format("Parameter %s must be set", ON_OR_BEFORE));
    }
  }
}
