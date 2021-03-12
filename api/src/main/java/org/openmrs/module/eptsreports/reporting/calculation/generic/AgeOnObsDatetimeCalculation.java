package org.openmrs.module.eptsreports.reporting.calculation.generic;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.joda.time.Interval;
import org.joda.time.Years;
import org.openmrs.Location;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.common.Birthdate;
import org.openmrs.module.reporting.data.patient.definition.SqlPatientDataDefinition;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The Class calculates the age of the patient based on report obs_datetime <br>
 * <code>PatientAge = obs_datetime - PatientBirthDate</code> <br>
 * The <i>minAge</i> and the <i>maxAge</i> are the boundaries to evaluate if the patient belong to
 * specific renge of age
 */
@Component
public class AgeOnObsDatetimeCalculation extends AbstractPatientCalculation {
  private static final String MAX_AGE = "maxAgeOnObsDatetime";

  private static final String MIN_AGE = "minAgeOnObsDatetime";

  private final String ON_OR_BEFORE = "onOrBefore";

  private final String ON_OR_AFTER = "onOrAfter";

  private final String LOCATION = "location";

  @Autowired private HivMetadata hivMetadata;

  @Autowired private CommonMetadata commonMetadata;

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {

    CalculationResultMap map = new CalculationResultMap();

    CalculationResultMap birthDates =
        EptsCalculationUtils.evaluateWithReporting(
            new BirthdateDataDefinition(), cohort, null, null, context);

    CalculationResultMap PatientAndObsDatetimeMap = getPatientAgeObsDatetimeMap(cohort, context);

    Integer minAge = (Integer) parameterValues.get(MIN_AGE);
    Integer maxAge = (Integer) parameterValues.get(MAX_AGE);

    for (Integer patientId : cohort) {
      Date birthDate = getBirthDate(patientId, birthDates);
      Date patientAndObsDatetime =
          EptsCalculationUtils.resultForPatient(PatientAndObsDatetimeMap, patientId);

      if (patientAndObsDatetime != null && birthDate != null) {
        final boolean datesConsistent = patientAndObsDatetime.compareTo(birthDate) >= 0;
        if (datesConsistent) {
          int years =
              Years.yearsIn(new Interval(patientAndObsDatetime.getTime(), birthDate.getTime()))
                  .getYears();
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

  private CalculationResultMap getPatientAgeObsDatetimeMap(
      Collection<Integer> cohort, PatientCalculationContext context) {
    SqlPatientDataDefinition def = new SqlPatientDataDefinition();
    def.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    def.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    def.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("1982", commonMetadata.getPregnantConcept().getConceptId());
    map.put("21187", hivMetadata.getRegArvSecondLine().getConceptId());
    map.put("1792", hivMetadata.getJustificativeToChangeArvTreatment().getConceptId());

    String sql =
        "SELECT p.patient_id, o.obs_datetime"
            + "   FROM"
            + "   patient p"
            + "   INNER JOIN"
            + "   encounter e ON e.patient_id = p.patient_id"
            + "       INNER JOIN"
            + "   obs o ON o.encounter_id = e.encounter_id"
            + "      INNER JOIN"
            + "   obs o2 ON o2.encounter_id = e.encounter_id"
            + "   WHERE e.voided = 0 AND p.voided = 0"
            + "   AND o.voided = 0"
            + "   AND o2.voided = 0"
            + "   AND e.encounter_type = ${53}"
            + "   AND e.location_id = :location  "
            + "   AND o.concept_id = ${21187}"
            + "   AND o.value_coded IS NOT NULL"
            + "   AND o.obs_datetime >= :onOrAfter"
            + "   AND o.obs_datetime <= :onOrBefore"
            + "   AND o2.concept_id = ${1792}"
            + "   AND o2.value_coded <> ${1982}";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    def.setSql(stringSubstitutor.replace(sql));

    Map<String, Object> params = new HashMap<>();
    params.put(LOCATION, context.getFromCache(LOCATION));
    params.put(ON_OR_AFTER, context.getFromCache(ON_OR_AFTER));
    params.put(ON_OR_BEFORE, context.getFromCache(ON_OR_BEFORE));
    return EptsCalculationUtils.evaluateWithReporting(def, cohort, params, null, context);
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
