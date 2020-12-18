package org.openmrs.module.eptsreports.reporting.calculation.generic;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.joda.time.Interval;
import org.joda.time.Years;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.common.Birthdate;
import org.openmrs.module.reporting.data.patient.definition.SqlPatientDataDefinition;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

@Component
public class AgeOnPreArtStartDateCalculation extends AbstractPatientCalculation {

  private static final String MAX_AGE = "maxAge";

  private static final String MIN_AGE = "minAge";

  private static final String ON_OR_AFTER = "onOrAfter";

  private static final String ON_OR_BEFORE = "onOrBefore";

  private static final String LOCATION = "location";

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {
    CalculationResultMap map = new CalculationResultMap();

    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);

    CalculationResultMap birthDatesMap =
        EptsCalculationUtils.evaluateWithReporting(
            new BirthdateDataDefinition(), cohort, null, null, context);

    CalculationResultMap aRTCareEnrollmentDateMap =
        getARTCareEnrollmentDate(hivMetadata, cohort, context);

    Integer minAge = (Integer) parameterValues.get(MIN_AGE);
    Integer maxAge = (Integer) parameterValues.get(MAX_AGE);

    for (Integer pId : cohort) {

      Date birthDate = getBirthDate(pId, birthDatesMap);
      Date artCareEnrollmentDate =
          EptsCalculationUtils.resultForPatient(aRTCareEnrollmentDateMap, pId);

      if (birthDate != null && artCareEnrollmentDate != null) {
        final boolean datesConsistent = birthDate.compareTo(artCareEnrollmentDate) <= 0;
        if (datesConsistent) {
          int years =
              Years.yearsIn(new Interval(birthDate.getTime(), artCareEnrollmentDate.getTime()))
                  .getYears();
          boolean b = isMinAgeOk(minAge, years) && isMaxAgeOk(maxAge, years);
          map.put(pId, new BooleanResult(b, this));
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

  private CalculationResultMap getARTCareEnrollmentDate(
      HivMetadata hivMetadata, Collection<Integer> cohort, PatientCalculationContext context) {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.addParameter(new Parameter(ON_OR_AFTER, "On Or After", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter(ON_OR_BEFORE, "On Or Before", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter(LOCATION, "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("hivCareProgram", hivMetadata.getHIVCareProgram().getId());
    map.put(
        "masterCardEncounterType", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("pPreArtStartDate", hivMetadata.getPreArtStartDate().getConceptId());

    String query =
        "SELECT earliest_date.patient_id ,MIN(earliest_date.min_date) "
            + "    FROM "
            + "        ( "
            + "            SELECT p.patient_id, MIN(pp.date_enrolled) AS min_date "
            + "            FROM patient p "
            + "                INNER JOIN patient_program pp "
            + "                    ON pp.patient_id = p.patient_id "
            + "                INNER JOIN program pg "
            + "                    ON pg.program_id = pp.program_id "
            + "            WHERE "
            + "                p.voided = 0 "
            + "                AND pp.voided = 0  "
            + "                AND pp.date_enrolled <= :onOrBefore "
            + "                AND pg.program_id = ${hivCareProgram} "
            + "                AND pp.location_id = :location "
            + "            GROUP BY p.patient_id "
            + "            UNION "
            + "            SELECT p.patient_id, MIN(o.value_datetime) AS min_date "
            + "            FROM patient p "
            + "                INNER JOIN encounter e "
            + "                    ON e.patient_id = p.patient_id "
            + "                INNER JOIN obs o "
            + "                    ON o.encounter_id = e.encounter_id "
            + "            WHERE  "
            + "                p.voided =0 "
            + "                AND e.voided = 0 "
            + "                AND o.voided = 0 "
            + "                AND e.encounter_type = ${masterCardEncounterType} "
            + "                AND e.location_id = :location "
            + "                AND o.concept_id = ${pPreArtStartDate} "
            + "                AND o.value_datetime <= :onOrBefore "
            + "            GROUP BY p.patient_id "
            + "        ) earliest_date "
            + "    WHERE earliest_date.min_date  "
            + "        BETWEEN :onOrAfter AND :onOrBefore "
            + "    GROUP BY earliest_date.patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlPatientDataDefinition.setSql(stringSubstitutor.replace(query));

    Map<String, Object> param = new HashMap<>();
    param.put(ON_OR_AFTER, context.getFromCache(ON_OR_AFTER));
    param.put(ON_OR_BEFORE, context.getFromCache(ON_OR_BEFORE));
    param.put(LOCATION, context.getFromCache(LOCATION));

    return EptsCalculationUtils.evaluateWithReporting(
        sqlPatientDataDefinition, cohort, param, null, context);
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
