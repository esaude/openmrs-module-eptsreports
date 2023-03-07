package org.openmrs.module.eptsreports.reporting.library.queries;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.module.reporting.cohort.definition.BaseObsCohortDefinition.TimeModifier;
import org.openmrs.module.reporting.cohort.definition.CodedObsCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.common.SetComparator;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

public class TXTBQueries {

  /**
   * <b>Description:</b> Copied straight from INICIO DE TRATAMENTO ARV - NUM PERIODO: INCLUI
   * TRANSFERIDOS DE COM DATA DE INICIO CONHECIDA (SQL)
   * SqlCohortDefinition#91787a86-0362-4820-a4ee-025d5501198b in backup
   *
   * @return {@link String}
   */
  public static String arvTreatmentIncludesTransfersFromWithKnownStartData(
      Integer arvPlanConceptId,
      Integer startDrugsConceptId,
      Integer historicalDrugsStartDateConceptId,
      Integer artProgramId,
      Integer pharmacyEncounterTypeId,
      Integer artAdultFollowupEncounterTypeId,
      Integer artPedFollowupEncounterTypeId) {
    String encounterTypeIds =
        StringUtils.join(
            Arrays.asList(
                pharmacyEncounterTypeId,
                artAdultFollowupEncounterTypeId,
                artPedFollowupEncounterTypeId),
            ",");
    return String.format(
        "SELECT patient_id FROM (SELECT patient_id, Min(data_inicio) data_inicio "
            + "FROM (SELECT p.patient_id, Min(e.encounter_datetime) data_inicio FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "WHERE e.voided = 0 AND o.voided = 0 AND p.voided = 0 AND e.encounter_type IN ( %s ) "
            + "AND o.concept_id = %s AND o.value_coded = %s AND e.encounter_datetime <= :endDate "
            + "AND e.location_id = :location GROUP BY p.patient_id "
            + "UNION SELECT p.patient_id, Min(value_datetime) data_inicio FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id = e.patient_id INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "WHERE p.voided = 0 AND e.voided = 0 AND o.voided = 0 AND e.encounter_type IN ( %s ) "
            + "AND o.concept_id = %s AND o.value_datetime IS NOT NULL AND o.value_datetime <= :endDate AND e.location_id = :location "
            + "GROUP BY p.patient_id UNION SELECT pg.patient_id, date_enrolled data_inicio FROM patient p "
            + "INNER JOIN patient_program pg ON p.patient_id = pg.patient_id WHERE pg.voided = 0 AND p.voided = 0 "
            + "AND program_id = %s AND date_enrolled <= :endDate AND location_id = :location "
            + "UNION SELECT e.patient_id, Min(e.encounter_datetime) AS data_inicio FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id = e.patient_id WHERE p.voided = 0 AND e.encounter_type = %s "
            + "AND e.voided = 0 AND e.encounter_datetime <= :endDate AND e.location_id = :location GROUP BY p.patient_id) inicio_real "
            + "GROUP BY patient_id)inicio WHERE data_inicio BETWEEN :startDate AND :endDate ",
        encounterTypeIds,
        arvPlanConceptId,
        startDrugsConceptId,
        encounterTypeIds,
        historicalDrugsStartDateConceptId,
        artProgramId,
        pharmacyEncounterTypeId);
  }

  /**
   * <b>Description:</b> exited by either transfer out, treatment suspension, treatment abandoned or
   * death of patient
   *
   * @return {@link CohortDefinition}
   */
  public static String patientsAtProgramStates(Integer artProgramId, List<Integer> stateIds) {
    return String.format(
        "SELECT pg.patient_id FROM patient p  "
            + "INNER JOIN patient_program pg  ON p.patient_id = pg.patient_id  "
            + "INNER JOIN patient_state ps  ON pg.patient_program_id = ps.patient_program_id "
            + "WHERE pg.voided = 0  AND ps.voided = 0  AND p.voided = 0  AND pg.program_id = %s "
            + "AND ps.state IN ( %s )  AND ps.end_date IS NULL  AND ps.start_date <= :endDate  "
            + "AND location_id = :location ",
        artProgramId, StringUtils.join(stateIds, ","));
  }

  public static String inTBProgramWithinReportingPeriodAtLocation(Integer tbProgramId) {
    return String.format(
        "select pg.patient_id from patient p inner join "
            + "patient_program pg on p.patient_id=pg.patient_id "
            + "where pg.voided=0 and p.voided=0 and program_id=%s "
            + "  and date_enrolled between :startDate and :endDate and location_id=:location",
        tbProgramId);
  }

  /**
   * Patients with Pulmonary TB Date in Patient Clinical Record of ART date TB (Condicoes medicas
   * importantes – Ficha Resumo Mastercard during reporting period.
   *
   * <ul>
   *   <li>Encounter Type ID = 53
   *   <li>Concept ID for Other Diagnosis = 1406
   *   <li>Answer = Pulmonary TB (value_coded 42)
   *   <li>Obs_datetime >= startDate and <=endDate
   * </ul>
   *
   * @param encounterTypeId
   * @param otherDiagnosisConcept
   * @param pulmonaryTBConcept
   * @return String
   */
  public static String pulmonaryTB(
      Integer encounterTypeId, Integer otherDiagnosisConcept, Integer pulmonaryTBConcept) {
    return String.format(
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e "
            + "ON p.patient_id = e.patient_id "
            + "INNER JOIN obs o "
            + "ON e.encounter_id = o.encounter_id "
            + "WHERE e.location_id = :location AND e.encounter_type = %s AND o.concept_id = %s AND o.value_coded = %s AND o.obs_datetime BETWEEN :startDate AND :endDate "
            + "AND p.voided = 0 AND e.voided = 0 AND o.voided = 0",
        encounterTypeId, otherDiagnosisConcept, pulmonaryTBConcept);
  }

  /**
   * Patients marked as “Tratamento TB= Inicio (I) ” in Ficha Clinica Master Card
   *
   * @param encounterTypeId
   * @param tbTreatmentPlan
   * @param startDrugs
   * @return String
   */
  public static String tbTreatmentStart(
      Integer encounterTypeId, Integer tbTreatmentPlan, Integer startDrugs) {
    return String.format(
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e "
            + "ON p.patient_id = e.patient_id "
            + "INNER JOIN obs o "
            + "ON e.encounter_id = o.encounter_id "
            + "WHERE e.location_id = :location AND e.encounter_type = %s AND o.concept_id = %s  AND o.value_coded = %s AND o.obs_datetime BETWEEN :startDate AND :endDate "
            + "AND p.voided = 0 AND e.voided = 0 AND o.voided = 0",
        encounterTypeId, tbTreatmentPlan, startDrugs);
  }

  /**
   * TUBERCULOSIS SYMPTOMS
   *
   * @param encounterTypeId
   * @param tbSymptomsId
   * @param yesConcept
   * @param noConcept
   * @return String
   */
  public static String tuberculosisSymptoms(
      Integer encounterTypeId, Integer tbSymptomsId, Integer yesConcept, Integer noConcept) {

    StringBuilder s = new StringBuilder();
    s.append("SELECT p.patient_id FROM patient p INNER JOIN encounter e ");
    s.append("ON p.patient_id = e.patient_id ");
    s.append("INNER JOIN obs o ");
    s.append("ON e.encounter_id = o.encounter_id ");
    s.append("WHERE e.location_id = :location AND e.encounter_type = ${encounterTypeId} ");
    s.append("AND (o.concept_id = ${tbSymptomsId}  ");
    s.append("AND (o.value_coded = ${yesConcept} ");
    if (noConcept != null) {
      s.append("OR o.value_coded = ${noConcept} ");
    }
    s.append(")) ");
    s.append("AND e.encounter_datetime BETWEEN :startDate AND :endDate ");
    s.append("AND p.voided = 0 AND e.voided = 0 AND o.voided = 0");

    Map<String, Integer> values = new HashMap<>();
    values.put("encounterTypeId", encounterTypeId);
    values.put("tbSymptomsId", tbSymptomsId);
    values.put("yesConcept", yesConcept);
    values.put("noConcept", noConcept);
    StringSubstitutor sb = new StringSubstitutor(values);
    return sb.replace(s.toString());
  }

  /**
   * ACTIVE TUBERCULOSIS
   *
   * @param encounterTypeId
   * @param activeTuberculosis
   * @param yesConcept
   * @return String
   */
  public static String activeTuberculosis(
      Integer encounterTypeId, Integer activeTuberculosis, Integer yesConcept) {
    return String.format(
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e "
            + "ON p.patient_id = e.patient_id "
            + "INNER JOIN obs o "
            + "ON e.encounter_id = o.encounter_id "
            + "WHERE e.location_id = :location AND e.encounter_type = %s "
            + "AND o.concept_id = %s  AND o.value_coded = %s "
            + "AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "AND p.voided = 0 AND e.voided = 0 AND o.voided = 0",
        encounterTypeId, activeTuberculosis, yesConcept);
  }

  /**
   * TB OBSERVATIONS
   *
   * @param encounterTypeId
   * @param tbObservation
   * @param fever
   * @param weight
   * @param nightweats
   * @param cough
   * @param asthenia
   * @param cohabitant
   * @param lymphadenopathy
   * @return String
   */
  public static String tbObservation(
      Integer encounterTypeId,
      Integer tbObservation,
      Integer fever,
      Integer weight,
      Integer nightweats,
      Integer cough,
      Integer asthenia,
      Integer cohabitant,
      Integer lymphadenopathy) {
    return String.format(
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e "
            + "ON p.patient_id = e.patient_id "
            + "INNER JOIN obs o "
            + "ON e.encounter_id = o.encounter_id "
            + "WHERE e.location_id = :location AND e.encounter_type = %s "
            + "AND (o.concept_id = %s  AND (o.value_coded = %s OR o.value_coded = %s OR o.value_coded = %s OR o.value_coded = %s "
            + "OR o.value_coded = %s OR o.value_coded = %s OR o.value_coded = %s)) "
            + "AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "AND p.voided = 0 AND e.voided = 0 AND o.voided = 0",
        encounterTypeId,
        tbObservation,
        fever,
        weight,
        nightweats,
        cough,
        asthenia,
        cohabitant,
        lymphadenopathy);
  }

  /**
   * APPLICATION FOR LABORATORY RESEARCH
   *
   * @param encounterTypeId
   * @param applicationForLaboratory
   * @param tbGenexpertTest
   * @param cultureTest
   * @param testTBLAM
   * @param testBK
   * @param xRayChest
   * @return String
   */
  public static String applicationForLaboratoryResearch(
      Integer encounterTypeId,
      Integer applicationForLaboratory,
      Integer tbGenexpertTest,
      Integer cultureTest,
      Integer testTBLAM,
      Integer testBK,
      Integer xRayChest) {
    return String.format(
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e "
            + "ON p.patient_id = e.patient_id "
            + "INNER JOIN obs o "
            + "ON e.encounter_id = o.encounter_id "
            + "WHERE e.location_id = :location AND e.encounter_type = %s "
            + "AND (o.concept_id = %s  AND (o.value_coded = %s OR o.value_coded = %s OR o.value_coded = %s OR o.value_coded = %s OR o.value_coded = %s)) "
            + "AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "AND p.voided = 0 AND e.voided = 0 AND o.voided = 0",
        encounterTypeId,
        applicationForLaboratory,
        tbGenexpertTest,
        cultureTest,
        testTBLAM,
        testBK,
        xRayChest);
  }

  /**
   * TB GENEXPERT TEST
   *
   * @param encounterTypeId
   * @param tbGenexpertTest
   * @param positive
   * @param negative
   * @return String
   */
  public static String tbGenexpertTest(
      Integer encounterTypeId, Integer tbGenexpertTest, Integer positive, Integer negative) {

    Map<String, Integer> map = new HashMap<>();
    map.put("encounterTypeId", encounterTypeId);
    map.put("tbGenexpertTest", tbGenexpertTest);
    map.put("positive", positive);
    map.put("negative", negative);

    StringBuilder query = new StringBuilder();
    query.append(" SELECT p.patient_id ");
    query.append(" FROM patient p ");
    query.append("		INNER JOIN encounter e ");
    query.append("			ON p.patient_id = e.patient_id ");
    query.append("		INNER JOIN obs o ");
    query.append("			ON e.encounter_id = o.encounter_id ");
    query.append(" WHERE e.location_id = :location ");
    query.append("       AND e.encounter_type = ${encounterTypeId} ");
    query.append("		AND o.concept_id = ${tbGenexpertTest}   ");
    if (positive != null && negative != null) {
      query.append("   AND o.value_coded IN (${positive} , ${negative} ) ");
    } else if (positive != null) {
      query.append("   AND o.value_coded = ${positive}  ");
    } else if (negative != null) {
      query.append("   AND o.value_coded = ${negative} ");
    }
    query.append("		AND e.encounter_datetime BETWEEN :startDate AND :endDate ");
    query.append("		AND p.voided = 0 AND e.voided = 0 AND o.voided = 0");

    StringSubstitutor sb = new StringSubstitutor(map);
    String rep = sb.replace(query.toString());
    System.out.println(rep);
    return rep;
  }

  /**
   * <b>Description:</b> CULTURE TEST
   *
   * @param encounterTypeId
   * @param cultureTest
   * @param positive
   * @param negative
   * @return String
   */
  public static String cultureTest(
      Integer encounterTypeId, Integer cultureTest, Integer positive, Integer negative) {
    return String.format(
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e "
            + "ON p.patient_id = e.patient_id "
            + "INNER JOIN obs o "
            + "ON e.encounter_id = o.encounter_id "
            + "WHERE e.location_id = :location AND e.encounter_type = %s "
            + "AND (o.concept_id = %s  AND (o.value_coded = %s OR o.value_coded = %s)) "
            + "AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "AND p.voided = 0 AND e.voided = 0 AND o.voided = 0",
        encounterTypeId, cultureTest, positive, negative);
  }

  /**
   * @param encounterTypeId
   * @param bkTest
   * @param positive
   * @param negative
   * @return
   */
  public static String bkTest(
      Integer encounterTypeId, Integer bkTest, Integer positive, Integer negative) {
    return String.format(
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e "
            + "ON p.patient_id = e.patient_id "
            + "INNER JOIN obs o "
            + "ON e.encounter_id = o.encounter_id "
            + "WHERE e.location_id = :location AND e.encounter_type = %s "
            + "AND (o.concept_id = %s  AND (o.value_coded = %s OR o.value_coded = %s)) "
            + "AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "AND p.voided = 0 AND e.voided = 0 AND o.voided = 0",
        encounterTypeId, bkTest, positive, negative);
  }

  /**
   * @param encounterTypeId
   * @param rxTorax
   * @param positive
   * @param negative
   * @return
   */
  public static String rxTorax(
      Integer encounterTypeId,
      Integer rxTorax,
      Integer positive,
      Integer negative,
      Integer indeterminado) {
    return String.format(
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e "
            + "ON p.patient_id = e.patient_id "
            + "INNER JOIN obs o "
            + "ON e.encounter_id = o.encounter_id "
            + "WHERE e.location_id = :location AND e.encounter_type = %s "
            + "AND (o.concept_id = %s  AND (o.value_coded = %s OR o.value_coded = %s OR o.value_coded = %s)) "
            + "AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "AND p.voided = 0 AND e.voided = 0 AND o.voided = 0",
        encounterTypeId, rxTorax, positive, negative, indeterminado);
  }

  /**
   * Test TB LAM
   *
   * @param encounterTypeId
   * @param testTBLAM
   * @param positive
   * @param negative
   * @return String
   */
  public static String testTBLAM(
      Integer encounterTypeId,
      Integer testTBLAM,
      Integer positive,
      Integer negative,
      Integer indeterminate) {
    return String.format(
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e "
            + "ON p.patient_id = e.patient_id "
            + "INNER JOIN obs o "
            + "ON e.encounter_id = o.encounter_id "
            + "WHERE e.location_id = :location AND e.encounter_type = %s "
            + "AND (o.concept_id = %s  AND (o.value_coded = %s OR o.value_coded = %s OR o.value_coded = %s)) "
            + "AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "AND p.voided = 0 AND e.voided = 0 AND o.voided = 0",
        encounterTypeId, testTBLAM, positive, negative, indeterminate);
  }

  /**
   * <b>Description:</b> Result For Basiloscopia
   *
   * @return {@link String}
   */
  public static String resultForBasiloscopia(Integer encounterTypeId, Integer basiloscopia) {
    return String.format(
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e "
            + "ON p.patient_id = e.patient_id "
            + "INNER JOIN obs o "
            + "ON e.encounter_id = o.encounter_id "
            + "WHERE e.location_id = :location AND e.encounter_type = %s "
            + "AND o.concept_id = %s "
            + "AND o.value_coded IS NOT NULL "
            + "AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "AND p.voided = 0 AND e.voided = 0 AND o.voided = 0",
        encounterTypeId, basiloscopia);
  }

  /**
   * <b>Description:</b> Test Results for any given concept
   *
   * <p>And all combinations of answers: positive, negative, undetermined
   *
   * @return {@link String}
   */
  public static String getTest(
      Integer encounterTypeId,
      Integer test,
      Integer positive,
      Integer negative,
      Integer undetermined) {

    Map<String, Integer> map = new HashMap<>();
    map.put("6", encounterTypeId);
    map.put("test", test);
    map.put("703", positive);
    map.put("664", negative);
    map.put("1138", undetermined);

    String query =
        "SELECT p.patient_id FROM patient p INNER JOIN encounter e "
            + "ON p.patient_id = e.patient_id "
            + "INNER JOIN obs o "
            + "ON e.encounter_id = o.encounter_id "
            + "WHERE e.location_id = :location AND e.encounter_type = ${6} "
            + "AND (o.concept_id = ${test}   ";

    if (negative == null && undetermined == null) {
      query += "AND o.value_coded = ${703}) ";
    } else if (positive == null && undetermined == null) {
      query += "AND o.value_coded = ${664}) ";
    } else if (positive == null && negative == null) {
      query += "AND o.value_coded = ${1138}) ";
    } else if (undetermined == null) {
      query += "AND (o.value_coded = ${703} OR o.value_coded = ${664})) ";
    } else if (negative == null) {
      query += "AND (o.value_coded = ${703} OR o.value_coded = ${1138})) ";
    } else if (positive == null) {
      query += "AND (o.value_coded = ${664} OR o.value_coded = ${1138})) ";
    } else {
      query +=
          "AND (o.value_coded = ${703} OR o.value_coded = ${664} OR o.value_coded = ${1138})) ";
    }

    query +=
        "AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + "AND p.voided = 0 AND e.voided = 0 AND o.voided = 0";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    return stringSubstitutor.replace(query);
  }

  /**
   * <b>Description:</b> Date Obs
   *
   * @return {@link String}
   */
  public static String dateObs(
      Integer questionId, List<Integer> encounterTypeIds, boolean startDate) {
    String sql =
        String.format(
            "select person_id from obs "
                + "where concept_id = %s and encounter_id in("
                + "select distinct encounter_id "
                + "from encounter "
                + "where encounter_type in(%s)) and location_id = :location and ",
            questionId, StringUtils.join(encounterTypeIds, ","));
    if (startDate) {
      sql += "value_datetime >= :startDate and value_datetime <= :endDate and voided=0";
    } else {
      sql += "value_datetime <= :endDate and voided=0";
    }
    return sql;
  }

  /**
   * Patients who have a {questionConcept} Obs with {valueCodedConcept} value between ${onOrAfter}
   * and ${onOrBefore}
   *
   * @param cohortDefinitionName Name for the cohort definition to return
   * @param questionConcept The question concept
   * @param valueCodedConcept The valueCoded concept
   * @param encounterTypesList The encounter types to consider
   * @return The cohort definition
   */
  public static CohortDefinition getPatientsWithObsBetweenDates(
      String cohortDefinitionName,
      Concept questionConcept,
      Concept valueCodedConcept,
      List<EncounterType> encounterTypesList) {
    CodedObsCohortDefinition cd = new CodedObsCohortDefinition();
    cd.setName(cohortDefinitionName);
    cd.addParameter(new Parameter("locationList", "Location", Location.class));
    cd.addParameter(new Parameter("onOrAfter", "After Date", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "Before Date", Date.class));
    cd.setQuestion(questionConcept);
    cd.setValueList(Collections.singletonList(valueCodedConcept));
    cd.setOperator(SetComparator.IN);
    cd.setTimeModifier(TimeModifier.ANY);
    cd.setEncounterTypeList(encounterTypesList);
    return cd;
  }

  /**
   * <b>Description:</b> Gets patients with a coded obs between dates. This method though might seem
   * like a duplicate of the above method, We are adding it because we faced a situation where a
   * patient had an obs with a wrong obs date time format (0209-10-22 00:00:00) Using
   * CodedObsCohortDefinition from the above method was counting this patient. We had to resort to
   * using an SQL query to get around this issue.
   *
   * @param questionId the obs concept Id
   * @param valueId the obs value coded Id
   * @param encounterTypeIds the obs's encounter enounter-type
   * @return the query to execute. TODO Investigate why CodedObsCohortDefinition was not able to
   *     handle this wrong date time format (0209-10-22 00:00:00)
   */
  public static String getPatientsWithObsBetweenDates(
      Integer questionId, Integer valueId, List<Integer> encounterTypeIds) {
    StringBuilder s = new StringBuilder();
    s.append("SELECT p.patient_id FROM patient p INNER JOIN encounter e ");
    s.append("ON p.patient_id = e.patient_id ");
    s.append("INNER JOIN obs o ");
    s.append("ON e.encounter_id = o.encounter_id ");
    s.append("WHERE e.location_id = :location AND e.encounter_type in (${encounterTypeIds}) ");
    s.append("AND o.concept_id = ${questionId}  ");
    s.append("AND o.value_coded = ${valueId} ");
    s.append("AND o.obs_datetime >= :startDate AND o.obs_datetime <= :endDate ");
    s.append("AND p.voided = 0 AND e.voided = 0 AND o.voided = 0");

    Map<String, String> values = new HashMap<>();
    values.put("encounterTypeIds", StringUtils.join(encounterTypeIds, ","));
    // Just convert the conceptId to String so it can be added to the map
    values.put("questionId", String.valueOf(questionId));
    values.put("valueId", String.valueOf(valueId));
    StringSubstitutor sb = new StringSubstitutor(values);
    return sb.replace(s.toString());
  }

  /**
   * Patients with Pulmonary TB Date in Patient Clinical Record of ART date TB (Condicoes medicas
   * importantes – Ficha Resumo Mastercard during reporting period
   *
   * @return String
   */
  public static String tbPulmonaryTBDate(
      Integer encounterTypeId, Integer pulmonaryTb, Integer answer) {
    return String.format(
        "SELECT p.patient_id "
            + "FROM   patient p "
            + "           inner  join encounter e on p.patient_id = e.patient_id "
            + "           inner join obs o on e.encounter_id = o.encounter_id "
            + "WHERE  p.voided = 0 "
            + "  and  e.voided = 0 "
            + "  and o.voided = 0 "
            + "  and e.encounter_type = %s "
            + "  and o.concept_id = %s "
            + "  and o.value_coded = %s "
            + "  and o.obs_datetime between :startDate AND :endDate",
        encounterTypeId, pulmonaryTb, answer);
  }

  /**
   * Patients marked as “Tratamento TB– Inicio (I) ” – Ficha Clinica MasterCard Patients on
   * treatment for TB
   *
   * @return String
   */
  public static String markedAsTratamentoTBInicio(
      Integer adultoSeguimentoEncounterType,
      Integer masterCardEncounterType,
      Integer tbTreatmentPlan,
      Integer startDrugs) {
    return String.format(
        "SELECT  p.patient_id "
            + "from   patient p"
            + "           inner  join encounter e on p.patient_id = e.patient_id "
            + "           inner join obs o on e.encounter_id = o.encounter_id "
            + "WHERE  p.voided = 0 "
            + "   and e.voided = 0 "
            + "   and o.voided = 0 "
            + "   and e.encounter_type IN (%s,%s) "
            + "   and o.concept_id = %s "
            + "   and o.value_coded = %s "
            + "   and o.obs_datetime between :startDate AND :endDate",
        adultoSeguimentoEncounterType, masterCardEncounterType, tbTreatmentPlan, startDrugs);
  }

  public static class AbandonedWithoutNotificationParams {
    protected Integer programId;
    protected Integer returnVisitDateConceptId;
    protected Integer returnVisitDateForARVDrugConceptId;
    protected Integer pharmacyEncounterTypeId;
    protected Integer artAdultFollowupEncounterTypeId;
    protected Integer artPedInicioEncounterTypeId;
    protected Integer transferOutStateId;
    protected Integer treatmentSuspensionStateId;
    protected Integer treatmentAbandonedStateId;
    protected Integer deathStateId;

    public AbandonedWithoutNotificationParams programId(Integer programId) {
      this.programId = programId;
      return this;
    }

    public AbandonedWithoutNotificationParams returnVisitDateConceptId(
        Integer returnVisitDateConceptId) {
      this.returnVisitDateConceptId = returnVisitDateConceptId;
      return this;
    }

    public AbandonedWithoutNotificationParams pharmacyEncounterTypeId(
        Integer pharmacyEncounterTypeId) {
      this.pharmacyEncounterTypeId = pharmacyEncounterTypeId;
      return this;
    }

    public AbandonedWithoutNotificationParams artPedInicioEncounterTypeId(
        Integer artPedInicioEncounterTypeId) {
      this.artPedInicioEncounterTypeId = artPedInicioEncounterTypeId;
      return this;
    }

    public AbandonedWithoutNotificationParams transferOutStateId(Integer transferOutStateId) {
      this.transferOutStateId = transferOutStateId;
      return this;
    }

    public AbandonedWithoutNotificationParams treatmentSuspensionStateId(
        Integer treatmentSuspensionStateId) {
      this.treatmentSuspensionStateId = treatmentSuspensionStateId;
      return this;
    }

    public AbandonedWithoutNotificationParams treatmentAbandonedStateId(
        Integer treatmentAbandonedStateId) {
      this.treatmentAbandonedStateId = treatmentAbandonedStateId;
      return this;
    }

    public AbandonedWithoutNotificationParams deathStateId(Integer deathStateId) {
      this.deathStateId = deathStateId;
      return this;
    }
  }
}
