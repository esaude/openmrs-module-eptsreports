package org.openmrs.module.eptsreports.reporting.library.datasets;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.generic.InitialArtStartDateCalculation;
import org.openmrs.module.eptsreports.reporting.data.converter.CalculationResultConverter;
import org.openmrs.module.eptsreports.reporting.data.converter.EncounterDatetimeConverter;
import org.openmrs.module.eptsreports.reporting.data.converter.GenderConverter;
import org.openmrs.module.eptsreports.reporting.data.definition.CalculationDataDefinition;
import org.openmrs.module.eptsreports.reporting.library.cohorts.TPTEligiblePatientListCohortQueries;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.converter.ObsValueConverter;
import org.openmrs.module.reporting.data.patient.definition.ConvertedPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.EncountersForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.SqlPatientDataDefinition;
import org.openmrs.module.reporting.data.person.definition.AgeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ConvertedPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.ObsForPersonDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonIdDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TPTListOfPatientsEligibleDataSet extends BaseDataSet {
  private HivMetadata hivMetadata;
  private TPTEligiblePatientListCohortQueries tPTEligiblePatientListCohortQueries;

  @Autowired
  public TPTListOfPatientsEligibleDataSet(
      HivMetadata hivMetadata,
      TPTEligiblePatientListCohortQueries tPTEligiblePatientListCohortQueries) {
    this.hivMetadata = hivMetadata;
    this.tPTEligiblePatientListCohortQueries = tPTEligiblePatientListCohortQueries;
  }

  public DataSetDefinition constructDataset() throws EvaluationException {

    PatientDataSetDefinition pdd = new PatientDataSetDefinition();

    pdd.setName("TPT");

    PatientIdentifierType identifierType =
        Context.getPatientService()
            .getPatientIdentifierTypeByUuid("e2b966d0-1d5f-11e0-b929-000c29ad1d07");
    DataConverter identifierFormatter = new ObjectFormatter("{identifier}");
    DataDefinition identifierDef =
        new ConvertedPatientDataDefinition(
            "identifier",
            new PatientIdentifierDataDefinition(identifierType.getName(), identifierType),
            identifierFormatter);
    DataConverter formatter = new ObjectFormatter("{familyName}, {givenName}");
    DataDefinition nameDef =
        new ConvertedPersonDataDefinition("name", new PreferredNameDataDefinition(), formatter);
    pdd.setParameters(getParameters());

    pdd.addRowFilter(
        tPTEligiblePatientListCohortQueries.getTxCurrWithoutTPT(),
        "endDate=${endDate},location=${location}");

    pdd.addColumn("id", new PersonIdDataDefinition(), "");
    pdd.addColumn("name", nameDef, "");
    pdd.addColumn("nid", this.getNID(identifierType.getPatientIdentifierTypeId()), "");
    pdd.addColumn("gender", new GenderDataDefinition(), "", new GenderConverter());
    pdd.addColumn("age", new AgeDataDefinition(), "", null);
    pdd.addColumn(
        "inicio_tarv",
        getArtStartDate(),
        "onOrBefore=${endDate},location=${location}",
        new CalculationResultConverter());
    pdd.addColumn(
        "date_next_consultation",
        getObsForPersonData("e1e2efd8-1d5f-11e0-b929-000c29ad1d07"),
        "onOrBefore=${endDate},locationList=${location}",
        new ObsValueConverter());
    pdd.addColumn(
        "date_last_segment",
        getLastEncounterDate(),
        "onOrBefore=${endDate},locationList=${location}",
        new EncounterDatetimeConverter());
    pdd.addColumn(
        "pregnant_or_breastfeeding", pregnantBreasfeediDefinition(), "location=${location}", null);

    return pdd;
  }

  @Override
  public List<Parameter> getParameters() {
    List<Parameter> parameters = new ArrayList<>();
    parameters.add(ReportingConstants.END_DATE_PARAMETER);
    parameters.add(ReportingConstants.LOCATION_PARAMETER);
    return parameters;
  }

  private DataDefinition getLastEncounterDate() {
    EncountersForPatientDataDefinition def =
        new EncountersForPatientDataDefinition("Last encounter");
    def.addParameter(new Parameter("onOrBefore", "On or before", Date.class));
    def.addParameter(new Parameter("locationList", "Location", Location.class));
    def.setWhich(TimeQualifier.LAST);
    return def;
  }

  private DataDefinition getArtStartDate() {
    CalculationDataDefinition cd =
        new CalculationDataDefinition(
            "Art start date",
            Context.getRegisteredComponents(InitialArtStartDateCalculation.class).get(0));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addParameter(new Parameter("onOrBefore", "On Or Before", Date.class));
    return cd;
  }

  private DataDefinition getObsForPersonData(String uuid) {
    ObsForPersonDataDefinition obsForPersonDataDefinition = new ObsForPersonDataDefinition();
    obsForPersonDataDefinition.addParameter(
        new Parameter("onOrBefore", "On or before", Date.class));
    obsForPersonDataDefinition.addParameter(
        new Parameter("locationList", "Location", Location.class));
    obsForPersonDataDefinition.setQuestion(Context.getConceptService().getConceptByUuid(uuid));
    obsForPersonDataDefinition.setWhich(TimeQualifier.LAST);
    return obsForPersonDataDefinition;
  }

  public DataDefinition getNID(int identifierType) {
    SqlPatientDataDefinition spdd = new SqlPatientDataDefinition();
    spdd.setName("NID");

    Map<String, Integer> valuesMap = new HashMap<>();

    String sql =
        " SELECT p.patient_id,pi.identifier  FROM patient p INNER JOIN patient_identifier pi ON p.patient_id=pi.patient_id "
            + " INNER JOIN patient_identifier_type pit ON pit.patient_identifier_type_id=pi.identifier_type "
            + " WHERE p.voided=0 AND pi.voided=0 AND pit.retired=0 AND pit.patient_identifier_type_id ="
            + identifierType;

    StringSubstitutor substitutor = new StringSubstitutor(valuesMap);

    spdd.setQuery(substitutor.replace(sql));
    return spdd;
  }

  public DataDefinition pregnantBreasfeediDefinition() {
    SqlPatientDataDefinition spdd = new SqlPatientDataDefinition();
    spdd.setName("Nome da definition");
    spdd.addParameter(new Parameter("location", "location", Location.class));
    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("1190", hivMetadata.getARVStartDateConcept().getConceptId());
    valuesMap.put("2", hivMetadata.getARTProgram().getProgramId());
    valuesMap.put("1065", hivMetadata.getYesConcept().getConceptId());
    valuesMap.put("1982", hivMetadata.getPregnantConcept().getConceptId());
    valuesMap.put("5", hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId());
    valuesMap.put("1279", hivMetadata.getNumberOfWeeksPregnant().getConceptId());
    valuesMap.put("1600", hivMetadata.getPregnancyDueDate().getConceptId());
    valuesMap.put("6334", hivMetadata.getCriteriaForArtStart().getConceptId());
    valuesMap.put("6331", hivMetadata.getBpostiveConcept().getConceptId());
    valuesMap.put("8", hivMetadata.getPtvEtvProgram().getProgramId());
    valuesMap.put("1465", hivMetadata.getDateOfLastMenstruationConcept().getConceptId());
    valuesMap.put("5599", hivMetadata.getPriorDeliveryDateConcept().getConceptId());
    valuesMap.put("6332", hivMetadata.getBreastfeeding().getConceptId());
    valuesMap.put("27", hivMetadata.getPatientGaveBirthWorkflowState().getProgramWorkflowStateId());

    String sql =
        "SELECT p.patient_id, CASE "
            + "                       WHEN (pregnancy_date IS NULL AND breastfeeding_date IS NULL) THEN '' "
            + "                       WHEN pregnancy_date IS NOT NULL THEN 'Grávida' "
            + "                       WHEN breastfeeding_date IS NOT NULL THEN 'Lactante' "
            + "                       END AS pregnance_state"
            + " FROM patient p "
            + " LEFT JOIN"
            + " ( Select max_pregnant.patient_id, pregnancy_date FROM   "
            + "                       (SELECT pregnant.patient_id, MAX(pregnant.pregnancy_date) AS pregnancy_date FROM   "
            + "                        ( SELECT p.patient_id , MAX(e.encounter_datetime) AS pregnancy_date   "
            + "                            FROM patient p   "
            + "                            INNER JOIN person pe ON p.patient_id=pe.person_id   "
            + "                            INNER JOIN encounter e ON p.patient_id=e.patient_id   "
            + "                            INNER JOIN obs o ON e.encounter_id=o.encounter_id   "
            + "                            WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id= ${1982}"
            + "                            AND value_coded= ${1065} "
            + "                            AND e.encounter_type in (${5},${6}) AND e.encounter_datetime between DATE_SUB(curdate(), INTERVAL 9 MONTH) AND curdate()"
            + "                            AND e.location_id=:location AND pe.gender='F' GROUP BY p.patient_id   "
            + "                            UNION   "
            + "                            SELECT p.patient_id, MAX(historical_date.value_datetime) as pregnancy_date FROM patient p   "
            + "                            INNER JOIN person pe ON p.patient_id=pe.person_id   "
            + "                            INNER JOIN encounter e ON p.patient_id=e.patient_id   "
            + "                            INNER JOIN obs pregnancy ON e.encounter_id=pregnancy.encounter_id   "
            + "                            INNER JOIN obs historical_date ON e.encounter_id = historical_date.encounter_id   "
            + "                            WHERE p.voided=0 AND e.voided=0 AND pregnancy.voided=0 AND pregnancy.concept_id= ${1982}"
            + "                            AND pregnancy.value_coded=  ${1065} "
            + "                            AND historical_date.voided=0 AND historical_date.concept_id= ${1190} "
            + "                            AND historical_date.value_datetime IS NOT NULL   "
            + "                            AND e.encounter_type = ${53} "
            + "                            AND historical_date.value_datetime between DATE_SUB(curdate(), INTERVAL 9 MONTH) AND curdate() "
            + "                            AND e.location_id=:location AND pe.gender='F' GROUP BY p.patient_id   "
            + "                            UNION   "
            + "                            SELECT p.patient_id,  MAX(e.encounter_datetime) as pregnancy_date   "
            + "                            FROM patient p   "
            + "                            INNER JOIN person pe ON p.patient_id=pe.person_id   "
            + "                            INNER JOIN encounter e ON p.patient_id=e.patient_id   "
            + "                            INNER JOIN obs o ON e.encounter_id=o.encounter_id   "
            + "                            WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id= ${1279} "
            + "                            AND e.encounter_type in (${5},${6}) AND e.encounter_datetime "
            + "                            between DATE_SUB(curdate(), INTERVAL 9 MONTH) AND curdate() "
            + "                            AND e.location_id=:location  AND pe.gender='F' GROUP BY p.patient_id   "
            + "                            UNION   "
            + "                            SELECT p.patient_id,  MAX(e.encounter_datetime) as pregnancy_date   "
            + "                            FROM patient p   "
            + "                            INNER JOIN person pe ON p.patient_id=pe.person_id   "
            + "                            INNER JOIN encounter e ON p.patient_id=e.patient_id   "
            + "                            INNER JOIN obs o ON e.encounter_id=o.encounter_id   "
            + "                            WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id=  ${1600} "
            + "                            and  e.encounter_type in (${5},${6}) AND e.encounter_datetime "
            + "                            between DATE_SUB(curdate(), INTERVAL 9 MONTH) AND curdate() "
            + "                            AND e.location_id=:location AND pe.gender='F' GROUP BY p.patient_id   "
            + "                            UNION   "
            + "                            Select p.patient_id, MAX(e.encounter_datetime) as pregnancy_date   "
            + "                            FROM patient p   "
            + "                            INNER JOIN person pe ON p.patient_id=pe.person_id   "
            + "                            INNER JOIN encounter e ON p.patient_id=e.patient_id   "
            + "                            INNER JOIN obs o ON e.encounter_id=o.encounter_id   "
            + "                            WHERE p.voided=0 AND pe.voided=0 AND e.voided=0 AND o.voided=0 "
            + "                            AND concept_id= ${6334} AND value_coded=  ${6331} "
            + "                            AND e.encounter_type in (${5},${6}) AND e.encounter_datetime "
            + "                            between DATE_SUB(curdate(), INTERVAL 9 MONTH) AND curdate() "
            + "                            AND e.location_id=:location AND pe.gender='F' GROUP BY p.patient_id   "
            + "                            UNION  "
            + "                            select pp.patient_id,  MAX(pp.date_enrolled) as pregnancy_date   "
            + "                            FROM patient_program pp   "
            + "                            INNER JOIN person pe ON pp.patient_id=pe.person_id   "
            + "                            WHERE pp.program_id= ${8} "
            + "                            AND pp.voided=0 AND pp.date_enrolled "
            + "                            between  DATE_SUB(curdate(), INTERVAL 9 MONTH) AND curdate() "
            + "                            AND pp.location_id=:location AND pe.gender='F' GROUP BY pp.patient_id   "
            + "                            UNION "
            + "                            SELECT p.patient_id,  MAX(o.value_datetime) as pregnancy_date  FROM patient p   "
            + "                            INNER JOIN person pe ON p.patient_id=pe.person_id   "
            + "                            INNER JOIN encounter e ON p.patient_id=e.patient_id   "
            + "                            INNER JOIN obs o ON e.encounter_id=o.encounter_id   "
            + "                            WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id= ${1465}   "
            + "                            AND e.encounter_type = ${6}  AND pe.gender='F' AND o.value_datetime "
            + "                            BETWEEN DATE_SUB(curdate(), INTERVAL 9 MONTH) AND curdate() AND e.location_id=:location"
            + "                            GROUP BY p.patient_id) as pregnant   "
            + "                            GROUP BY pregnant.patient_id) max_pregnant   "
            + "                LEFT JOIN   "
            + "                            (SELECT breastfeeding.patient_id, max(breastfeeding.last_date) as breastfeeding_date "
            + "                            FROM (   "
            + "                            SELECT p.patient_id, MAX(o.value_datetime) AS last_date   "
            + "                            FROM patient p   "
            + "                            INNER JOIN person pe ON p.patient_id=pe.person_id   "
            + "                            INNER JOIN encounter e ON p.patient_id=e.patient_id   "
            + "                            INNER JOIN obs o ON e.encounter_id=o.encounter_id   "
            + "                            WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id= ${5599}"
            + "                            AND  e.encounter_type in (${5},${6})  AND o.value_datetime "
            + "                            BETWEEN DATE_SUB(curdate(), INTERVAL 18 MONTH) AND curdate()    "
            + "                            AND e.location_id=:location AND pe.gender='F'   "
            + "                            GROUP BY p.patient_id   "
            + "                            UNION   "
            + "                            SELECT p.patient_id, MAX(e.encounter_datetime) AS last_date   "
            + "                            FROM patient p   "
            + "                            INNER JOIN person pe ON p.patient_id=pe.person_id   "
            + "                            INNER JOIN encounter e ON p.patient_id=e.patient_id   "
            + "                            INNER JOIN obs o ON e.encounter_id=o.encounter_id   "
            + "                            WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND o.concept_id= ${6332} "
            + "                            AND o.value_coded= ${1065} "
            + "                            AND e.encounter_type in (${5},${6})    "
            + "                            AND e.encounter_datetime BETWEEN DATE_SUB(curdate(), INTERVAL 18 MONTH) AND curdate()   "
            + "                            AND e.location_id=:location AND pe.gender='F'   "
            + "                            GROUP BY p.patient_id  "
            + "                            UNION   "
            + "                            SELECT     p.patient_id, MAX(e.encounter_datetime) AS last_date   "
            + "                            FROM patient p   "
            + "                            INNER JOIN person pe ON p.patient_id=pe.person_id   "
            + "                            INNER JOIN encounter e ON p.patient_id=e.patient_id   "
            + "                            INNER JOIN obs o ON e.encounter_id=o.encounter_id   "
            + "                            WHERE p.voided=0 AND pe.voided=0 AND e.voided=0 AND o.voided=0 AND o.concept_id= ${6334} "
            + "                            AND o.value_coded=  ${6332} "
            + "                            AND e.encounter_type in (${5},${6})   "
            + "                            AND e.encounter_datetime BETWEEN DATE_SUB(curdate(), INTERVAL 18 MONTH) AND curdate()    "
            + "                            AND e.location_id=:location AND pe.gender='F' GROUP BY p.patient_id   "
            + "                            UNION   "
            + "                            SELECT pp.patient_id, MAX(ps.start_date) AS last_date   "
            + "                            FROM patient_program pp   "
            + "                            INNER JOIN person pe ON pp.patient_id=pe.person_id   "
            + "                            INNER JOIN patient_state ps ON pp.patient_program_id=ps.patient_program_id   "
            + "                            WHERE pp.program_id=${8} "
            + "                            AND ps.state=  ${27} "
            + "                            AND pp.voided=0 AND    "
            + "                            ps.start_date BETWEEN DATE_SUB(curdate(), INTERVAL 18 MONTH) AND curdate()    "
            + "                            AND pp.location_id=:location AND pe.gender='F'   "
            + "                            GROUP BY pp.patient_id   "
            + "                            UNION   "
            + "                            SELECT p.patient_id, MAX(hist.value_datetime) AS last_date   "
            + "                            FROM patient p   "
            + "                            INNER JOIN person pe ON p.patient_id=pe.person_id   "
            + "                            INNER JOIN encounter e ON p.patient_id=e.patient_id   "
            + "                            INNER JOIN obs o ON e.encounter_id=o.encounter_id   "
            + "                            INNER JOIN obs hist ON e.encounter_id=hist.encounter_id   "
            + "                            WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND o.concept_id= ${6332} "
            + "                            AND o.value_coded= ${1065} AND e.location_id=:location"
            + "                            AND e.encounter_type = ${53} "
            + "                            AND hist.concept_id=  ${1190} "
            + "                            AND pe.gender='F' AND hist.value_datetime IS NOT NULL    "
            + "                            AND hist.value_datetime BETWEEN DATE_SUB(curdate(), INTERVAL 18 MONTH) AND curdate()    "
            + "                                    GROUP BY p.patient_id ) AS breastfeeding   "
            + "                            GROUP BY breastfeeding.patient_id) max_breastfeeding   "
            + "                                            ON max_pregnant.patient_id = max_breastfeeding.patient_id   "
            + "                                            WHERE (max_pregnant.pregnancy_date Is NOT NULL AND max_pregnant.pregnancy_date >= max_breastfeeding.breastfeeding_date)   "
            + "                                            OR (max_breastfeeding.breastfeeding_date Is NULL)) pregnancy ON pregnancy.patient_id = p.patient_id "
            + "         LEFT JOIN"
            + "                            (Select max_breastfeeding.patient_id, breastfeeding_date FROM   "
            + "                            (SELECT breastfeeding.patient_id, max(breastfeeding.last_date) as breastfeeding_date FROM (   "
            + "                            SELECT p.patient_id, MAX(o.value_datetime) AS last_date   "
            + "                            FROM patient p   "
            + "                            INNER JOIN person pe ON p.patient_id=pe.person_id   "
            + "                            INNER JOIN encounter e ON p.patient_id=e.patient_id   "
            + "                            INNER JOIN obs o ON e.encounter_id=o.encounter_id   "
            + "                            WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id= ${5599}"
            + "                            AND  e.encounter_type in (${5},${6})  AND o.value_datetime "
            + "                            BETWEEN DATE_SUB(curdate(), INTERVAL 18 MONTH) AND curdate()    "
            + "                                        AND e.location_id=:location AND pe.gender='F'   "
            + "                            GROUP BY p.patient_id   "
            + "                            UNION   "
            + "                            SELECT p.patient_id, MAX(e.encounter_datetime) AS last_date   "
            + "                            FROM patient p   "
            + "                            INNER JOIN person pe ON p.patient_id=pe.person_id   "
            + "                            INNER JOIN encounter e ON p.patient_id=e.patient_id   "
            + "                            INNER JOIN obs o ON e.encounter_id=o.encounter_id   "
            + "                            WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND o.concept_id= ${6332} "
            + "                            AND o.value_coded= ${1065} "
            + "                            AND e.encounter_type in (${5},${6})    "
            + "                            AND e.encounter_datetime BETWEEN DATE_SUB(curdate(), INTERVAL 18 MONTH) AND curdate()   "
            + "                            AND e.location_id=:location AND pe.gender='F'   "
            + "                            GROUP BY p.patient_id  "
            + "                            UNION   "
            + "                            SELECT     p.patient_id, MAX(e.encounter_datetime) AS last_date   "
            + "                            FROM patient p   "
            + "                            INNER JOIN person pe ON p.patient_id=pe.person_id   "
            + "                            INNER JOIN encounter e ON p.patient_id=e.patient_id   "
            + "                            INNER JOIN obs o ON e.encounter_id=o.encounter_id   "
            + "                            WHERE p.voided=0 AND pe.voided=0 AND e.voided=0 AND o.voided=0 AND o.concept_id= ${6334} "
            + "                            AND o.value_coded=  ${6332} "
            + "                            AND e.encounter_type in (${5},${6})   "
            + "                                    AND e.encounter_datetime BETWEEN DATE_SUB(curdate(), INTERVAL 18 MONTH) AND curdate()    "
            + "                                AND e.location_id=:location AND pe.gender='F' GROUP BY p.patient_id   "
            + "                            UNION   "
            + "                            SELECT pp.patient_id, MAX(ps.start_date) AS last_date   "
            + "                            FROM patient_program pp   "
            + "                            INNER JOIN person pe ON pp.patient_id=pe.person_id   "
            + "                            INNER JOIN patient_state ps ON pp.patient_program_id=ps.patient_program_id   "
            + "                            WHERE pp.program_id=${8} "
            + "                            AND ps.state=  ${27} "
            + "                            AND pp.voided=0 AND    "
            + "                                ps.start_date BETWEEN DATE_SUB(curdate(), INTERVAL 18 MONTH) AND curdate()    "
            + "                                AND pp.location_id=:location AND pe.gender='F'   "
            + "                            GROUP BY pp.patient_id   "
            + "                                            UNION   "
            + "                            SELECT p.patient_id, MAX(hist.value_datetime) AS last_date   "
            + "                            FROM patient p   "
            + "                            INNER JOIN person pe ON p.patient_id=pe.person_id   "
            + "                            INNER JOIN encounter e ON p.patient_id=e.patient_id   "
            + "                            INNER JOIN obs o ON e.encounter_id=o.encounter_id   "
            + "                            INNER JOIN obs hist ON e.encounter_id=hist.encounter_id   "
            + "                            WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND o.concept_id= ${6332} "
            + "                            AND o.value_coded= ${1065} "
            + "                            AND e.encounter_type = ${53} AND e.location_id=:location"
            + "                            AND hist.concept_id=  ${1190} "
            + "                            AND pe.gender='F' AND hist.value_datetime IS NOT NULL    "
            + "                            AND hist.value_datetime BETWEEN DATE_SUB(curdate(), INTERVAL 18 MONTH) AND curdate()    "
            + "                                    GROUP BY p.patient_id   "
            + "                            ) AS breastfeeding   "
            + "                            GROUP BY patient_id) max_breastfeeding   "
            + "                            LEFT JOIN   "
            + "                            (SELECT pregnant.patient_id, MAX(pregnant.pregnancy_date) AS pregnancy_date FROM   "
            + "                            (SELECT p.patient_id , MAX(e.encounter_datetime) AS pregnancy_date   "
            + "                            FROM patient p   "
            + "                            INNER JOIN person pe ON p.patient_id=pe.person_id   "
            + "                            INNER JOIN encounter e ON p.patient_id=e.patient_id   "
            + "                            INNER JOIN obs o ON e.encounter_id=o.encounter_id   "
            + "                            WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id= ${1982}"
            + "                            AND value_coded= ${1065} "
            + "                            AND e.encounter_type in (${5},${6}) AND e.encounter_datetime between DATE_SUB(curdate(), INTERVAL 9 MONTH) AND curdate() "
            + "                            AND e.location_id=:location AND pe.gender='F' GROUP BY p.patient_id   "
            + "                            UNION   "
            + "                            SELECT p.patient_id, MAX(historical_date.value_datetime) as pregnancy_date FROM patient p   "
            + "                            INNER JOIN person pe ON p.patient_id=pe.person_id   "
            + "                            INNER JOIN encounter e ON p.patient_id=e.patient_id   "
            + "                            INNER JOIN obs pregnancy ON e.encounter_id=pregnancy.encounter_id   "
            + "                            INNER JOIN obs historical_date ON e.encounter_id = historical_date.encounter_id   "
            + "                            WHERE p.voided=0 AND e.voided=0 AND pregnancy.voided=0 AND pregnancy.concept_id= ${1982}"
            + "                            AND pregnancy.value_coded=  ${1065} "
            + "                            AND historical_date.voided=0 AND historical_date.concept_id= ${1190} "
            + "                            AND historical_date.value_datetime IS NOT NULL   "
            + "                            AND e.encounter_type = ${53} "
            + "                            AND historical_date.value_datetime between DATE_SUB(curdate(), INTERVAL 9 MONTH) AND curdate() "
            + "                            AND e.location_id=:location AND pe.gender='F' GROUP BY p.patient_id   "
            + "                            UNION   "
            + "                            SELECT p.patient_id,  MAX(e.encounter_datetime) as pregnancy_date   "
            + "                            FROM patient p   "
            + "                            INNER JOIN person pe ON p.patient_id=pe.person_id   "
            + "                            INNER JOIN encounter e ON p.patient_id=e.patient_id   "
            + "                            INNER JOIN obs o ON e.encounter_id=o.encounter_id   "
            + "                            WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id= ${1279} "
            + "                            AND   "
            + "                            e.encounter_type in (${5},${6}) AND e.encounter_datetime between DATE_SUB(curdate(), INTERVAL 9 MONTH) AND curdate() "
            + "                            AND e.location_id=:location  AND pe.gender='F' GROUP BY p.patient_id   "
            + "                            UNION   "
            + "                            Select p.patient_id,  MAX(e.encounter_datetime) as pregnancy_date   "
            + "                            FROM patient p   "
            + "                            INNER JOIN person pe ON p.patient_id=pe.person_id   "
            + "                            INNER JOIN encounter e ON p.patient_id=e.patient_id   "
            + "                            INNER JOIN obs o ON e.encounter_id=o.encounter_id   "
            + "                            WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id=  ${1600} "
            + "                            AND  e.encounter_type in (${5},${6}) AND e.encounter_datetime between DATE_SUB(curdate(), INTERVAL 9 MONTH) AND curdate() "
            + "                            AND e.location_id=:location AND pe.gender='F' GROUP BY p.patient_id   "
            + "                            UNION   "
            + "                            Select p.patient_id, MAX(e.encounter_datetime) as pregnancy_date   "
            + "                            FROM patient p   "
            + "                            INNER JOIN person pe ON p.patient_id=pe.person_id   "
            + "                            INNER JOIN encounter e ON p.patient_id=e.patient_id   "
            + "                            INNER JOIN obs o ON e.encounter_id=o.encounter_id   "
            + "                            WHERE p.voided=0 AND pe.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id= ${6334} "
            + "                            AND value_coded=  ${6331} "
            + "                            AND e.encounter_type in (${5},${6}) AND e.encounter_datetime between DATE_SUB(curdate(), INTERVAL 9 MONTH) AND curdate() "
            + "                            AND e.location_id=:location AND pe.gender='F' GROUP BY p.patient_id   "
            + "                            UNION  "
            + "                            select pp.patient_id,  MAX(pp.date_enrolled) as pregnancy_date   "
            + "                            FROM patient_program pp   "
            + "                            INNER JOIN person pe ON pp.patient_id=pe.person_id   "
            + "                            WHERE pp.program_id= ${8} "
            + "                            AND pp.voided=0 AND pp.date_enrolled between  DATE_SUB(curdate(), INTERVAL 9 MONTH) AND curdate() "
            + "                            AND pp.location_id=:location AND pe.gender='F' GROUP BY pp.patient_id   "
            + "                            UNION "
            + "                            SELECT p.patient_id,  MAX(o.value_datetime) as pregnancy_date  FROM patient p   "
            + "                            INNER JOIN person pe ON p.patient_id=pe.person_id   "
            + "                            INNER JOIN encounter e ON p.patient_id=e.patient_id   "
            + "                            INNER JOIN obs o ON e.encounter_id=o.encounter_id   "
            + "                            WHERE p.voided=0 AND e.voided=0 AND o.voided=0 AND concept_id= ${1465} "
            + "                            AND e.encounter_type = ${6}  AND e.location_id=:location"
            + "                            AND pe.gender='F' AND o.value_datetime BETWEEN DATE_SUB(curdate(), INTERVAL 9 MONTH) AND curdate() GROUP BY p.patient_id) as pregnant   "
            + "                            GROUP BY patient_id) max_pregnant "
            + "              "
            + "                            ON max_pregnant.patient_id = max_breastfeeding.patient_id   "
            + "                            WHERE (max_breastfeeding.breastfeeding_date Is NOT NULL AND max_breastfeeding.breastfeeding_date > max_pregnant.pregnancy_date)   "
            + "                            OR (max_pregnant.pregnancy_date Is NULL) ) breastfeeding  "
            + "                            ON breastfeeding.patient_id = p.patient_id";
    StringSubstitutor substitutor = new StringSubstitutor(valuesMap);

    spdd.setQuery(substitutor.replace(sql));
    return spdd;
  }
}
