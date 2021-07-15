package org.openmrs.module.eptsreports.reporting.calculation.formulations;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.ListResult;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.data.patient.definition.SqlPatientDataDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

@Component
public class ListOfChildrenOnARTFormulation2Calculation extends AbstractPatientCalculation {

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {
    CalculationResultMap map = new CalculationResultMap();

    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);
    CalculationResultMap formulation2 = getFormulationMap(cohort, context, hivMetadata);

    CalculationResultMap formulation1 =
        calculate(
            Context.getRegisteredComponents(ListOfChildrenOnARTFormulation1Calculation.class)
                .get(0),
            cohort,
            context);

    for (Integer patientId : cohort) {
      ListResult listResult = (ListResult) formulation2.get(patientId);
      List<Integer> conceptIdListResult = EptsCalculationUtils.extractResultValues(listResult);

      Integer formulation1Result = EptsCalculationUtils.resultForPatient(formulation1, patientId);

      if (conceptIdListResult != null
          && !conceptIdListResult.isEmpty()
          && formulation1Result != null) {
        for (Integer result : conceptIdListResult) {
          if (result != formulation1Result) {
            map.put(patientId, new SimpleResult(result, this));
            break;
          }
        }
      }
    }
    return map;
  }

  private CalculationResultMap getFormulationMap(
      Collection<Integer> cohort, PatientCalculationContext context, HivMetadata hivMetadata) {
    SqlPatientDataDefinition def = new SqlPatientDataDefinition();
    def.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    def.addParameter(new Parameter("location", "location", Location.class));
    Map<String, Integer> map = new HashMap<>();
    map.put("18", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    map.put("165256", hivMetadata.getArtDrugFormulationConcept().getConceptId());
    map.put("165252", hivMetadata.getDrugAndQuantityConcept().getConceptId());
    String sql =
        "SELECT p.patient_id, cn.name "
            + "FROM patient p "
            + "         INNER JOIN encounter e on p.patient_id = e.patient_id "
            + "         INNER JOIN obs o on e.encounter_id = o.encounter_id "
            + "         INNER JOIN drug d on d.drug_id = o.value_drug "
            + "         INNER JOIN concept c on d.concept_id = c.concept_id "
            + "         INNER JOIN concept_name cn  on cn.concept_id = c.concept_id "
            + "         INNER JOIN( "
            + "                    SELECT p.patient_id, MAX(e.encounter_datetime) AS e_encounter_date "
            + "                    FROM patient p "
            + "                             INNER JOIN encounter e on p.patient_id = e.patient_id "
            + "                             INNER JOIN obs o on e.encounter_id = o.encounter_id "
            + "                    WHERE p.voided = 0 "
            + "                      AND e.voided = 0 "
            + "                      AND o.voided = 0 "
            + "                      AND e.encounter_type = ${18} "
            + "                      AND e.encounter_datetime <= :onOrBefore  "
            + "                      AND e.location_id = :location  "
            + "                    GROUP BY p.patient_id "
            + "             ) AS max_farmacia ON max_farmacia.patient_id= p.patient_id "
            + "WHERE p.voided = 0 "
            + "    AND  e.voided = 0 "
            + "    AND  o.voided  = 0 "
            + "    AND  cn.voided  = 0 "
            + "    AND  cn.locale  = 'pt' "
            + "    AND o.concept_id =  ${165256} "
            + "    AND e.encounter_type = ${18} "
            + "    AND e.encounter_datetime <= :onOrBefore "
            + "    AND e.location_id = :location  "
            + "  AND e.encounter_datetime = max_farmacia.e_encounter_date "
            + "  AND o.obs_group_id = ${165252}";
    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    def.setQuery(stringSubstitutor.replace(sql));
    Map<String, Object> params = new HashMap<>();
    params.put("location", context.getFromCache("location"));
    params.put("onOrBefore", context.getFromCache("onOrBefore"));
    return EptsCalculationUtils.evaluateWithReporting(def, cohort, params, null, context);
  }
}
