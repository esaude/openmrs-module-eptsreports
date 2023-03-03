package org.openmrs.module.eptsreports.reporting.calculation.dsd;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.generic.InitialArtStartDateCalculation;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.common.Age;
import org.openmrs.module.reporting.common.Birthdate;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.springframework.stereotype.Component;

@Component
public class OnArtForAtleastXmonthsCalculation extends AbstractPatientCalculation {
  private static final String monthsAmount = "atLeastXMonthsOnART";

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {

    //    Date onOrBefore = context.getNow(); //
    Date onOrBefore = (Date) context.getFromCache("onOrBefore");
    CalculationResultMap map = new CalculationResultMap();

    CalculationResultMap arvsInitiationDateMap =
        calculate(
            Context.getRegisteredComponents(InitialArtStartDateCalculation.class).get(0),
            cohort,
            context);
    CalculationResultMap birthDates =
        EptsCalculationUtils.evaluateWithReporting(
            new BirthdateDataDefinition(), cohort, null, null, context);

    for (Integer ptId : cohort) {
      boolean onArtAtleastMonths = false;
      Date patientBirthdate = null;
      SimpleResult artStartDateResult = (SimpleResult) arvsInitiationDateMap.get(ptId);
      CalculationResult patientBirthDateResult = birthDates.get(ptId);
      if (patientBirthDateResult != null) {
        Birthdate birthdate = (Birthdate) patientBirthDateResult.getValue();
        if (birthdate != null) {
          patientBirthdate = birthdate.getBirthdate();
        }
      }

      if (artStartDateResult != null && patientBirthdate != null) {
        Date artStartDate = (Date) artStartDateResult.getValue();
        if (artStartDate != null && onOrBefore != null && patientBirthdate != null) {
          Integer months = (Integer) parameterValues.get(monthsAmount);
          Date onArtForXMonths = EptsCalculationUtils.addMonths(artStartDate, months);

          Integer ageInYears = ageInYearsAtDate(patientBirthdate, onOrBefore);

          if ((onArtForXMonths.compareTo(onOrBefore) <= 0 && ageInYears >= 2)) {
            onArtAtleastMonths = true;
          }
        }
      }
      map.put(ptId, new BooleanResult(onArtAtleastMonths, this));
    }
    return map;
  }

  private Integer ageInYearsAtDate(Date birthDate, Date artInitiationDate) {

    Age age = new Age(birthDate, artInitiationDate);

    return age.getFullYears();
  }
}
