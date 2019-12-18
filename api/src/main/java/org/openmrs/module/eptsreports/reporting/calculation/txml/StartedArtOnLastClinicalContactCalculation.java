package org.openmrs.module.eptsreports.reporting.calculation.txml;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.generic.InitialArtStartDateCalculation;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.data.patient.definition.SqlPatientDataDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;

public class StartedArtOnLastClinicalContactCalculation extends AbstractPatientCalculation {

  @Autowired private HivMetadata hivMetadata;

  private static final String ON_OR_BEFORE = "endDate";

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {
    CalculationResultMap lastClinicalContactMap = getLastClinicalContactMap(cohort, context);

    Boolean lessThan90Days = (Boolean) parameterValues.get("lessThan90Days");

    CalculationResultMap lessThan90DaysMap = new CalculationResultMap();
    CalculationResultMap moreThan90DaysMap = new CalculationResultMap();
    CalculationResultMap artStartDates =
        calculate(
            Context.getRegisteredComponents(InitialArtStartDateCalculation.class).get(0),
            cohort,
            parameterValues,
            context);
    Date endDate = (Date) parameterValues.get(ON_OR_BEFORE);
    if (endDate == null) {
      endDate = (Date) context.getFromCache(ON_OR_BEFORE);
    }
    if (endDate != null) {
      for (Integer patientId : cohort) {
        Date artStartDate =
            InitialArtStartDateCalculation.getArtStartDate(patientId, artStartDates);
        Date lastClinicalContact =
            EptsCalculationUtils.resultForPatient(lastClinicalContactMap, patientId);

        if (artStartDate != null && lastClinicalContact != null) {
          Date onArtForLessThan90Days = EptsCalculationUtils.addDays(artStartDate, 90);

          if (onArtForLessThan90Days.compareTo(lastClinicalContact) <= 0) {
            lessThan90DaysMap.put(patientId, new BooleanResult(true, this));
          }
          if (onArtForLessThan90Days.compareTo(lastClinicalContact) > 0) {
            moreThan90DaysMap.put(patientId, new BooleanResult(true, this));
          }
        }
      }
      if (lessThan90Days) {
        return lessThan90DaysMap;
      } else {
        return moreThan90DaysMap;
      }
    } else {
      throw new IllegalArgumentException(String.format("Parameter %s must be set", ON_OR_BEFORE));
    }
  }

  /**
   * @param cohort
   * @param context
   * @return
   */
  private CalculationResultMap getLastClinicalContactMap(
      Collection<Integer> cohort, PatientCalculationContext context) {
    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.addParameter(new Parameter("endDate", "onOrBefore", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter("location", "location", Location.class));

    String query =
        "SELECT lcc.patient_id, MAX(lcc.value_datetime) FROM ( "
            + "SELECT p.patient_id,MAX(o.value_datetime)value_datetime  FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + "WHERE p.voided =0 AND e.voided=0 AND o.voided=0 AND o.concept_id=%d AND o.value_datetime IS NOT NULL AND e.encounter_type =%d AND e.location_id= :location "
            + "AND e.encounter_datetime <= :endDate GROUP BY p.patient_id "
            + "UNION "
            + "SELECT p.patient_id,MAX(o.value_datetime)value_datetime  FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + "WHERE p.voided =0 AND e.voided=0 AND o.voided=0 AND o.concept_id=%d AND o.value_datetime IS NOT NULL AND e.encounter_type =%d AND e.location_id= :location "
            + "AND e.encounter_datetime <= :endDate GROUP BY p.patient_id "
            + "UNION "
            + "SELECT p.patient_id,DATE_ADD(MAX(o.value_datetime), INTERVAL 30 day) value_datetime  FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + "WHERE p.voided =0 AND e.voided=0 AND o.voided=0 AND o.concept_id=%d AND o.value_datetime IS NOT NULL AND e.encounter_type =%d AND e.location_id= :location "
            + "AND e.encounter_datetime <= :endDate GROUP BY p.patient_id "
            + ")lcc GROUP BY patient_id ";

    sqlPatientDataDefinition.setSql(
        String.format(
            query,
            hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getReturnVisitDateConceptConcept().getConceptId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getArtDatePickup().getConceptId(),
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId()));

    Map<String, Object> params = new HashMap<>();
    params.put("endDate", context.getFromCache("endDate"));
    params.put("location", context.getFromCache("location"));

    return EptsCalculationUtils.evaluateWithReporting(
        sqlPatientDataDefinition, cohort, params, null, context);
  }
}
