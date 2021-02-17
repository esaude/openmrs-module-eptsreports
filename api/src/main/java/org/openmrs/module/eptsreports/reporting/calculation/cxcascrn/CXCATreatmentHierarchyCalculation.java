package org.openmrs.module.eptsreports.reporting.calculation.cxcascrn;

import java.util.*;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.AbstractPatientCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.common.EPTSCalculationService;
import org.openmrs.module.eptsreports.reporting.utils.EPTSMetadataDatetimeQualifier;
import org.openmrs.module.eptsreports.reporting.utils.EptsCalculationUtils;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.patient.definition.SqlPatientDataDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.stereotype.Component;

/** */
@Component
public class CXCATreatmentHierarchyCalculation extends AbstractPatientCalculation {

  private final String ON_OR_AFTER = "onOrAfter";
  private final String ON_OR_BEFORE = "onOrBefore";
  private final String LOCATION = "location";

  @Override
  public CalculationResultMap evaluate(
      Collection<Integer> cohort,
      Map<String, Object> parameterValues,
      PatientCalculationContext context) {
    CalculationResultMap map = new CalculationResultMap();

    Date startDate = (Date) context.getFromCache(ON_OR_AFTER);
    Date endDate = (Date) context.getFromCache(ON_OR_BEFORE);
    Location location = (Location) context.getFromCache(LOCATION);

    EPTSCalculationService eptsCalculationService =
        Context.getRegisteredComponents(EPTSCalculationService.class).get(0);
    HivMetadata hivMetadata = Context.getRegisteredComponents(HivMetadata.class).get(0);

    TreatmentType treatmentType = (TreatmentType) parameterValues.get("type");

    EncounterType e28 = hivMetadata.getRastreioDoCancroDoColoUterinoEncounterType();
    Concept c2149 = hivMetadata.getViaResultOnTheReferenceConcept();
    Concept c23972 = hivMetadata.getThermocoagulationConcept();
    Concept c23970 = hivMetadata.getLeepConcept();
    Concept c23973 = hivMetadata.getconizationConcept();

    CalculationResultMap b5Map = getB5Map(hivMetadata, cohort, context);

    CalculationResultMap b6Map =
        eptsCalculationService.getObs(
            c2149,
            e28,
            cohort,
            location,
            Arrays.asList(c23972),
            TimeQualifier.ANY,
            startDate,
            endDate,
            EPTSMetadataDatetimeQualifier.VALUE_DATETIME,
            context);

    CalculationResultMap b7Map =
        eptsCalculationService.getObs(
            c2149,
            e28,
            cohort,
            location,
            Arrays.asList(c23970, c23973),
            TimeQualifier.ANY,
            startDate,
            endDate,
            EPTSMetadataDatetimeQualifier.VALUE_DATETIME,
            context);

    for (Integer pId : cohort) {

      Date b5Date = EptsCalculationUtils.resultForPatient(b5Map, pId);
      Obs b6Obs = EptsCalculationUtils.resultForPatient(b6Map, pId);
      Obs b7Obs = EptsCalculationUtils.resultForPatient(b7Map, pId);

      // handling b7
      if (treatmentType == TreatmentType.B7 && b7Obs != null) {
        map.put(pId, new SimpleResult(b7Obs.getValueDatetime(), this));
      }
      // handling b6
      if (treatmentType == TreatmentType.B6 && b6Obs != null && b7Obs != null) {
        if (b6Obs.getValueDatetime().compareTo(b7Obs.getValueDatetime()) == 0) {
          continue;
        } else {
          map.put(pId, new SimpleResult(b7Obs.getValueDatetime(), this));
        }
      }
      if (treatmentType == TreatmentType.B6 && b6Obs != null && b7Obs == null) {
        map.put(pId, new SimpleResult(b7Obs.getValueDatetime(), this));
      }
      // handling b5
      if (treatmentType == TreatmentType.B5 && b5Date != null && b6Obs != null && b7Obs != null) {
        if (b5Date.compareTo(b7Obs.getValueDatetime()) == 0) {
          continue;
        }
        if (b5Date.compareTo(b6Obs.getValueDatetime()) == 0) {
          continue;
        }
        if (b5Date.compareTo(b6Obs.getValueDatetime()) != 0
            && b5Date.compareTo(b7Obs.getValueDatetime()) != 0) {
          map.put(pId, new SimpleResult(b5Date, this));
        }
      }
      if (treatmentType == TreatmentType.B5 && b5Date != null && b6Obs != null && b7Obs == null) {
        if (b5Date.compareTo(b6Obs.getValueDatetime()) == 0) {
          continue;
        } else {
          map.put(pId, new SimpleResult(b5Date, this));
        }
      }

      if (treatmentType == TreatmentType.B5 && b5Date != null && b6Obs == null && b7Obs != null) {
        if (b5Date.compareTo(b7Obs.getValueDatetime()) == 0) {
          continue;
        } else {
          map.put(pId, new SimpleResult(b5Date, this));
        }
      }

      if (treatmentType == TreatmentType.B5 && b5Date != null && b6Obs == null && b7Obs == null) {

        map.put(pId, new SimpleResult(b5Date, this));
      }
    }

    return map;
  }

  private CalculationResultMap getB5Map(
      HivMetadata hivMetadata, Collection<Integer> cohort, PatientCalculationContext context) {
    SqlPatientDataDefinition def = new SqlPatientDataDefinition();
    def.addParameter(new Parameter("onOrAfter", "onOrAfter", Date.class));
    def.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    def.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("28", hivMetadata.getRastreioDoCancroDoColoUterinoEncounterType().getEncounterTypeId());
    map.put("2117", hivMetadata.getCryotherapyPerformedOnTheSameDayASViaConcept().getConceptId());
    map.put("1065", hivMetadata.getPatientFoundYesConcept().getConceptId());
    map.put("23967", hivMetadata.getCryotherapyDateConcept().getConceptId());
    map.put("2149", hivMetadata.getViaResultOnTheReferenceConcept().getConceptId());
    map.put("23874", hivMetadata.getPediatricNursingConcept().getConceptId());

    String sql =
        ""
            + " SELECT p.patient_id, e.encounter_datetime "
            + " FROM patient p "
            + "    INNER JOIN encounter e "
            + "        ON e.patient_id = p.patient_id "
            + "    INNER JOIN obs o "
            + "        ON e.encounter_id = o.encounter_id "
            + " WHERE  "
            + "    p.voided = 0 "
            + "    AND e.voided = 0 "
            + "    AND o.voided = 0 "
            + "    AND e.encounter_type = ${28} "
            + "    AND o.concept_id = ${2117} AND o.value_coded = ${1065} "
            + "      AND e.encounter_datetime BETWEEN :onOrAfter AND :onOrBefore"
            + "    AND e.location_id = :location"
            + " UNION "
            + " SELECT p.patient_id, o.value_datetime "
            + " FROM patient p "
            + "    INNER JOIN encounter e "
            + "        ON e.patient_id = p.patient_id "
            + "    INNER JOIN obs o "
            + "        ON e.encounter_id = o.encounter_id "
            + " WHERE  "
            + "    p.voided = 0 "
            + "    AND e.voided = 0 "
            + "    AND o.voided = 0 "
            + "    AND e.encounter_type = ${28} "
            + "    AND ( "
            + "            (o.concept_id = ${23967} ) "
            + "            OR "
            + "            (o.concept_id = ${2149} AND o.value_coded = ${23874}) "
            + "        )    "
            + "      AND o.value_datetime BETWEEN :onOrAfter AND :onOrBefore"
            + "    AND e.location_id = :location";

    StringSubstitutor sb = new StringSubstitutor(map);
    def.setQuery(sb.replace(sql));

    Map<String, Object> params = new HashMap<>();
    params.put("location", context.getFromCache("location"));
    params.put("onOrAfter", context.getFromCache("onOrAfter"));
    params.put("onOrBefore", context.getFromCache("onOrBefore"));

    return EptsCalculationUtils.evaluateWithReporting(def, cohort, params, null, context);
  }
}
