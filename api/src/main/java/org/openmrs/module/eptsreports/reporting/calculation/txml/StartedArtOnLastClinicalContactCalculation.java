package org.openmrs.module.eptsreports.reporting.calculation.txml;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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

    String query =
        "SELECT lcc.patient_id, MAX(lcc.value_datetime) FROM ( "
            + "SELECT p.patient_id,MAX(o.value_datetime)value_datetime  FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + "WHERE p.voided =0 AND e.voided=0 AND o.voided=0 AND o.concept_id=%d AND o.value_datetime IS NOT NULL AND e.encounter_type =%d AND e.location_id= :location "
            + "AND e.encounter_datetime <= :onOrBefore GROUP BY p.patient_id "
            + "UNION "
            + "SELECT p.patient_id,MAX(o.value_datetime)value_datetime  FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + "WHERE p.voided =0 AND e.voided=0 AND o.voided=0 AND o.concept_id=%d AND o.value_datetime IS NOT NULL AND e.encounter_type IN (%d,%d) AND e.location_id= :location "
            + "AND e.encounter_datetime <= :onOrBefore GROUP BY p.patient_id "
            + "UNION "
            + "SELECT p.patient_id,DATE_ADD(MAX(o.value_datetime), INTERVAL 30 day) value_datetime  FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + "WHERE p.voided =0 AND e.voided=0 AND o.voided=0 AND o.concept_id=%d AND o.value_datetime IS NOT NULL AND e.encounter_type =%d AND e.location_id= :location "
            + "AND o.value_datetime <= :onOrBefore GROUP BY p.patient_id "
            + ")lcc GROUP BY patient_id ";

    sqlPatientDataDefinition.setSql(
        String.format(
            query,
            hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            commonMetadata.getReturnVisitDateConcept().getConceptId(),
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getArtDatePickupMasterCard().getConceptId(),
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId()));

    Map<String, Object> params = new HashMap<>();
    params.put("onOrBefore", context.getFromCache("onOrBefore"));
    params.put("location", context.getFromCache("location"));

    return EptsCalculationUtils.evaluateWithReporting(
        sqlPatientDataDefinition, cohort, params, null, context);
  }
}
