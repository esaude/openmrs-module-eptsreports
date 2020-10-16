package org.openmrs.module.eptsreports.reporting.calculation.txml;

import java.util.*;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.BooleanResult;
import org.openmrs.module.eptsreports.reporting.calculation.generic.InitialArtStartDateCalculation;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.data.patient.definition.SqlPatientDataDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

@Component
public class StartedArtOnLastClinicalContactCalculation extends AbstractPatientCalculation {

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {
    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);
    CommonMetadata commonMetadata = Context.getRegisteredComponents(CommonMetadata.class).get(0);

    CalculationResultMap lastClinicalContactMap =
        getLastClinicalContactMap(cohort, context, commonMetadata, hivMetadata);

    Boolean lessThan90Days = (Boolean) parameterValues.get("lessThan90Days");

    CalculationResultMap lessThan90DaysMap = new CalculationResultMap();
    CalculationResultMap moreThan90DaysMap = new CalculationResultMap();
    CalculationResultMap artStartDates =
        calculate(
            Context.getRegisteredComponents(InitialArtStartDateCalculation.class).get(0),
            cohort,
            parameterValues,
            context);

    for (Integer patientId : cohort) {
      Date artStartDate = InitialArtStartDateCalculation.getArtStartDate(patientId, artStartDates);
      Date lastClinicalContact =
          EptsCalculationUtils.resultForPatient(lastClinicalContactMap, patientId);

      if (artStartDate != null && lastClinicalContact != null) {
        int days = EptsCalculationUtils.daysSince(artStartDate, lastClinicalContact);

        if (days < 90) {
          lessThan90DaysMap.put(patientId, new BooleanResult(true, this));
        } else {
          moreThan90DaysMap.put(patientId, new BooleanResult(true, this));
        }
      }
    }
    if (lessThan90Days) {
      return lessThan90DaysMap;
    } else {
      return moreThan90DaysMap;
    }
  }

  /**
   * @param cohort
   * @param context
   * @return
   */
  private CalculationResultMap getLastClinicalContactMap(
      Collection<Integer> cohort,
      PatientCalculationContext context,
      CommonMetadata commonMetadata,
      HivMetadata hivMetadata) {
    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter("location", "location", Location.class));
    Map<String, Integer> valuesMap = new HashMap<>();

    String query =
        " SELECT   patient_id,"
            + "         CASE"
            + "             WHEN Greatest(COALESCE(return_date_fila,0),COALESCE(return_date_consulta,0),COALESCE(return_date_master,0)) = 0 "
            + "         THEN NULL "
            + "         ELSE DATE(Greatest(COALESCE(return_date_fila,0),COALESCE(return_date_consulta,0),COALESCE(return_date_master,0)))"
            + "         END AS return_date"
            + " FROM     ("
            + "                SELECT p.patient_id,"
            + "                       ("
            + "                                SELECT   o.value_datetime"
            + "                                FROM     encounter e"
            + "                                JOIN     obs o"
            + "                                ON       e.encounter_id=o.encounter_id"
            + "                                WHERE    p.patient_id=e.patient_id"
            + "                                AND      e.location_id=:location"
            + "                                AND      e.encounter_type=${aRVPharmaciaEncounterType}"
            + "                                AND      o.concept_id = ${returnVisitDateForArvDrug}"
            + "                                AND      e.voided = 0"
            + "                                AND      o.voided = 0"
            + "                                AND      e.encounter_datetime ="
            + "                                         ("
            + "                                                    SELECT     e.encounter_datetime AS return_date"
            + "                                                    FROM       encounter e"
            + "                                                    INNER JOIN obs o"
            + "                                                    ON         e.encounter_id = o.encounter_id"
            + "                                                    WHERE      p.patient_id = e.patient_id"
            + "                                                    AND        e.voided = 0"
            + "                                                    AND        o.voided = 0"
            + "                                                    AND        e.encounter_type = ${aRVPharmaciaEncounterType}"
            + "                                                    AND        e.location_id = :location"
            + "                                                    AND        e.encounter_datetime <= :onOrBefore"
            + "                                                    ORDER BY   e.encounter_datetime DESC limit 1)"
            + "                                ORDER BY o.value_datetime DESC limit 1) AS return_date_fila,"
            + "                       ("
            + "                                SELECT   o.value_datetime"
            + "                                FROM     encounter e"
            + "                                JOIN     obs o"
            + "                                ON       e.encounter_id=o.encounter_id"
            + "                                WHERE    p.patient_id=e.patient_id"
            + "                                AND      e.location_id=:location"
            + "                                AND      e.encounter_type IN(${adultoSeguimentoEncounterType},"
            + "                                                             ${pediatriaSeguimentoEncounterType})"
            + "                                AND      o.concept_id = ${returnVisitDate}"
            + "                                AND      e.voided = 0"
            + "                                AND      o.voided = 0"
            + "                                AND      e.encounter_datetime ="
            + "                                         ("
            + "                                                    SELECT     e.encounter_datetime AS return_date"
            + "                                                    FROM       encounter e"
            + "                                                    INNER JOIN obs o"
            + "                                                    ON         e.encounter_id = o.encounter_id"
            + "                                                    WHERE      p.patient_id = e.patient_id"
            + "                                                    AND        e.voided = 0"
            + "                                                    AND        o.voided = 0"
            + "                                                    AND        e.encounter_type IN (${adultoSeguimentoEncounterType},"
            + "                                                                                    ${pediatriaSeguimentoEncounterType})"
            + "                                                    AND        e.location_id = :location"
            + "                                                    AND        e.encounter_datetime <= :onOrBefore"
            + "                                                    ORDER BY   e.encounter_datetime DESC limit 1)"
            + "                                ORDER BY o.value_datetime DESC limit 1) AS return_date_consulta,"
            + "                       ("
            + "                                  SELECT     date_add(o.value_datetime, interval 30 day) AS return_date"
            + "                                  FROM       encounter e"
            + "                                  INNER JOIN obs o"
            + "                                  ON         e.encounter_id = o.encounter_id"
            + "                                  WHERE      p.patient_id = e.patient_id"
            + "                                  AND        e.voided = 0"
            + "                                  AND        o.voided = 0"
            + "                                  AND        e.encounter_type = ${masterCardDrugPickupEncounterType} "
            + "                                  AND        e.location_id = :location"
            + "                                  AND        o.concept_id = ${artDatePickupMasterCard}"
            + "                                  AND        o.value_datetime <= :onOrBefore"
            + "                                  ORDER BY   o.value_datetime DESC limit 1) AS return_date_master"
            + "                FROM   patient p"
            + "                WHERE  p.voided=0) e"
            + " GROUP BY e.patient_id";

    StringSubstitutor sub = new StringSubstitutor(valuesMap);
    valuesMap.put(
        "returnVisitDateForArvDrug",
        hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId());
    valuesMap.put(
        "aRVPharmaciaEncounterType",
        hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    valuesMap.put("returnVisitDate", hivMetadata.getReturnVisitDateConcept().getConceptId());
    valuesMap.put(
        "adultoSeguimentoEncounterType",
        hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put(
        "pediatriaSeguimentoEncounterType",
        hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put(
        "artDatePickupMasterCard", hivMetadata.getArtDatePickupMasterCard().getConceptId());
    valuesMap.put(
        "masterCardDrugPickupEncounterType",
        hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());

    sqlPatientDataDefinition.setSql(sub.replace(query));

    Map<String, Object> params = new HashMap<>();
    params.put("onOrBefore", context.getFromCache("onOrBefore"));
    params.put("location", context.getFromCache("location"));

    return EptsCalculationUtils.evaluateWithReporting(
        sqlPatientDataDefinition, cohort, params, null, context);
  }
}
