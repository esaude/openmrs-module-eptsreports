package org.openmrs.module.eptsreports.reporting.calculation.generic;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import org.joda.time.Interval;
import org.joda.time.Months;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.melhoriaQualidade.MohMQInitiatedARTDuringTheInclusionPeriodCalculation;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.common.Birthdate;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.springframework.stereotype.Component;

@Component
public class AgeInMonthsOnArtStartDateCalculation extends AbstractPatientCalculation {

  private static final String MAX_AGE = "maxAgeInMonths";

  private static final String MIN_AGE = "minAgeInMonths";

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {
    CalculationResultMap map = new CalculationResultMap();

    CalculationResultMap artStartDates =
        calculate(
            Context.getRegisteredComponents(
                    MohMQInitiatedARTDuringTheInclusionPeriodCalculation.class)
                .get(0),
            cohort,
            context);
    CalculationResultMap birthDates =
        EptsCalculationUtils.evaluateWithReporting(
            new BirthdateDataDefinition(), cohort, null, null, context);
    Integer minAge = (Integer) parameterValues.get(MIN_AGE);
    Integer maxAge = (Integer) parameterValues.get(MAX_AGE);

    for (Integer patientId : cohort) {
      Date artStartDate = InitialArtStartDateCalculation.getArtStartDate(patientId, artStartDates);
      Date birthDate = getBirthDate(patientId, birthDates);
      if (artStartDate != null && birthDate != null) {
        final boolean datesConsistent = birthDate.compareTo(artStartDate) <= 0;
        if (datesConsistent) {
          int months =
              Months.monthsIn(new Interval(birthDate.getTime(), artStartDate.getTime()))
                  .getMonths();
          map.put(
              patientId,
              new BooleanResult(isMinAgeOk(minAge, months) && isMaxAgeOk(maxAge, months), this));
        }
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

  private boolean isMaxAgeOk(Integer maxAge, int months) {
    return maxAge == null || months <= maxAge;
  }

  private boolean isMinAgeOk(Integer minAge, int months) {
    return minAge == null || months >= minAge;
  }
}
