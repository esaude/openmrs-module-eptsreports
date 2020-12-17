package org.openmrs.module.eptsreports.reporting.calculation.generic;

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
import org.openmrs.module.eptsreports.reporting.calculation.melhoriaQualidade.MohMQInitiatedARTDuringTheInclusionPeriodCalculation;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.common.Birthdate;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.springframework.stereotype.Component;

@Component
public class AgeOnMOHArtStartDateCalculation extends AbstractPatientCalculation {

  private static final String MAX_AGE = "maxAge";

  private static final String MIN_AGE = "minAge";

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
    Boolean considerPatientThatStartedBeforeWasBorn =
        (Boolean) parameterValues.get("considerPatientThatStartedBeforeWasBorn");
    if (considerPatientThatStartedBeforeWasBorn == null) {
      considerPatientThatStartedBeforeWasBorn = false;
    }
    for (Integer patientId : cohort) {
      Date artStartDate = InitialArtStartDateCalculation.getArtStartDate(patientId, artStartDates);
      Date birthDate = getBirthDate(patientId, birthDates);
      if (artStartDate != null && birthDate != null) {
        final boolean datesConsistent = birthDate.compareTo(artStartDate) <= 0;
        if (datesConsistent) {
          int years =
              Years.yearsIn(new Interval(birthDate.getTime(), artStartDate.getTime())).getYears();
          map.put(
              patientId,
              new BooleanResult(isMinAgeOk(minAge, years) && isMaxAgeOk(maxAge, years), this));
        } else if (considerPatientThatStartedBeforeWasBorn) {
          map.put(
              patientId, new BooleanResult(isMinAgeOk(minAge, 0) && isMaxAgeOk(maxAge, 0), this));
        }
      }
    }
    return map;
  }

  private boolean isMaxAgeOk(Integer maxAge, int years) {
    return maxAge == null || years <= maxAge.intValue();
  }

  private boolean isMinAgeOk(Integer minAge, int years) {
    return minAge == null || years >= minAge.intValue();
  }

  private Date getBirthDate(Integer patientId, CalculationResultMap birthDates) {
    CalculationResult result = birthDates.get(patientId);
    if (result != null) {
      Birthdate birthDate = (Birthdate) result.getValue();
      return birthDate.getBirthdate();
    }
    return null;
  }
}
