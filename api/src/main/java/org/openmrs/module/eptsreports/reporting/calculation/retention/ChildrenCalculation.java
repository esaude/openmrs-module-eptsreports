package org.openmrs.module.eptsreports.reporting.calculation.retention;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import org.joda.time.Interval;
import org.joda.time.Years;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.pvls.InitialArtStartDateCalculation;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.common.Birthdate;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.springframework.stereotype.Component;

@Component
public class ChildrenCalculation extends AbstractPatientCalculation {

  private static final int CHILDREN_MAXIMUM_AGE = 14;

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
            context);
    CalculationResultMap birthDates =
        EptsCalculationUtils.evaluateWithReporting(
            new BirthdateDataDefinition(), cohort, null, null, context);
    for (Integer patientId : cohort) {
      Date artStartDate = getArtStartDate(patientId, artStartDates);
      Date birthDate = getBirthDate(patientId, birthDates);
      if (artStartDate != null && birthDate != null && birthDate.compareTo(artStartDate) <= 0) {
        int years =
            Years.yearsIn(new Interval(birthDate.getTime(), artStartDate.getTime())).getYears();
        map.put(patientId, new BooleanResult(years <= CHILDREN_MAXIMUM_AGE, this));
      }
    }
    return map;
  }

  private Date getBirthDate(Integer patientId, CalculationResultMap birthDates) {
    CalculationResult result = birthDates.get(patientId);
    if (result != null) {
      Birthdate birthDate = (Birthdate) result.getValue();
      return birthDate.getBirthdate();
    }
    return null;
  }

  private Date getArtStartDate(Integer patientId, CalculationResultMap artStartDates) {
    CalculationResult calculationResult = artStartDates.get(patientId);
    if (calculationResult != null) {
      return (Date) calculationResult.getValue();
    }
    return null;
  }
}
