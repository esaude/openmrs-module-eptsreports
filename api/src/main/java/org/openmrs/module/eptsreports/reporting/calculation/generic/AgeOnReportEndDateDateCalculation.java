package org.openmrs.module.eptsreports.reporting.calculation.generic;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import org.joda.time.Interval;
import org.joda.time.Years;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.common.Birthdate;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.springframework.stereotype.Component;

/**
 * The Class calculates the age of the patient based on report EndDate <br>
 * <code>PatientAge = ReportEndDate - PatientBirthDate</code> <br>
 * The <i>minAge</i> and the <i>maxAge</i> are the boundaries to evaluate if the patient belong to
 * specific renge of age
 */
@Component
public class AgeOnReportEndDateDateCalculation extends AbstractPatientCalculation {

  private static final String MAX_AGE = "maxAge";

  private static final String MIN_AGE = "minAge";

  private final String ON_OR_BEFORE = "onOrBefore";

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {
    CalculationResultMap map = new CalculationResultMap();

    CalculationResultMap birthDates =
        EptsCalculationUtils.evaluateWithReporting(
            new BirthdateDataDefinition(), cohort, null, null, context);
    Integer minAge = (Integer) parameterValues.get(MIN_AGE);
    Integer maxAge = (Integer) parameterValues.get(MAX_AGE);
    Date endDate = (Date) context.getFromCache(ON_OR_BEFORE);

    for (Integer patientId : cohort) {
      Date birthDate = getBirthDate(patientId, birthDates);
      if (endDate != null && birthDate != null) {
        final boolean datesConsistent = birthDate.compareTo(endDate) <= 0;
        if (datesConsistent) {
          int years =
              Years.yearsIn(new Interval(birthDate.getTime(), endDate.getTime())).getYears();
          boolean b = isMinAgeOk(minAge, years) && isMaxAgeOk(maxAge, years);
          map.put(patientId, new BooleanResult(b, this));
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
