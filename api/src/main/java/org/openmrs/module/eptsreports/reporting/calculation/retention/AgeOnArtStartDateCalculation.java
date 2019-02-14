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
public class AgeOnArtStartDateCalculation extends AbstractPatientCalculation {

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
    Integer minAge = (Integer) parameterValues.get("minAge");
    Integer maxAge = (Integer) parameterValues.get("maxAge");
    for (Integer patientId : cohort) {
      Date artStartDate = getArtStartDate(patientId, artStartDates);
      Date birthDate = getBirthDate(patientId, birthDates);
      if (artStartDate != null && birthDate != null && birthDate.compareTo(artStartDate) <= 0) {
        int years =
            Years.yearsIn(new Interval(birthDate.getTime(), artStartDate.getTime())).getYears();
        map.put(
            patientId,
            new BooleanResult(isMinAgeOk(minAge, years) && isMaxAgeOk(maxAge, years), this));
      }
    }
    return map;
  }

  private boolean isMaxAgeOk(Integer maxAge, int years) {
    if (maxAge != null) {
      return years <= maxAge.intValue();
    }
    // if no max age specified any age will do
    return true;
  }

  private boolean isMinAgeOk(Integer minAge, int years) {
    if (minAge != null) {
      return years >= minAge.intValue();
    }
    // if no min age specified any age will do
    return true;
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
