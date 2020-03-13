package org.openmrs.module.eptsreports.reporting.calculation.resumo;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.common.EPTSCalculationService;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.springframework.stereotype.Component;

@Component
public class MoreThanOneEncounterInStatisticalYearCalculation extends AbstractPatientCalculation {

  private static final String ENCOUNTER_TYPE = "encounter_type";

  private static final String START_DATE = "onOrAfter";

  private static final String END_DATE = "onOrBefore";

  private static final String LOCATION = "location";

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {

    CalculationResultMap calculationResultMap = new CalculationResultMap();

    EPTSCalculationService eptsCalculationService =
        Context.getRegisteredComponents(EPTSCalculationService.class).get(0);

    Date endDate = (Date) context.getFromCache(END_DATE);
    Date startDate = (Date) context.getFromCache(START_DATE);

    Location location = (Location) context.getFromCache(LOCATION);

    EncounterType encounterType = (EncounterType) parameterValues.get(ENCOUNTER_TYPE);

    Date newStartDate = getStartDate(startDate, endDate);
    CalculationResultMap encounterMap =
        eptsCalculationService.allEncounters(
            Arrays.asList(encounterType), cohort, location, newStartDate, startDate, context);

    for (Integer patientId : cohort) {

      List<Encounter> encounters = EptsCalculationUtils.resultForPatient(encounterMap, patientId);

      if (encounters != null && encounters.size() > 1) {
        calculationResultMap.put(patientId, new BooleanResult(true, this));
      }
    }

    return calculationResultMap;
  }

  private Date getStartDate(Date startDate, Date endDate) {

    Calendar startCalendar1 = Calendar.getInstance();
    startCalendar1.setTime(startDate);
    int month = startCalendar1.get(Calendar.MONTH);
    int day = startCalendar1.get(Calendar.DAY_OF_MONTH);

    if (day == 21 && month == Calendar.DECEMBER) {
      return startDate;
    }

    Calendar startCalendar2 = Calendar.getInstance();
    startCalendar2.setTime(startDate);
    startCalendar2.add(Calendar.YEAR, -1);
    startCalendar2.set(Calendar.MONTH, Calendar.DECEMBER);
    startCalendar2.set(Calendar.DAY_OF_MONTH, 21);
    System.out.println(startCalendar2.getTime());
    return startCalendar2.getTime();
  }
}
