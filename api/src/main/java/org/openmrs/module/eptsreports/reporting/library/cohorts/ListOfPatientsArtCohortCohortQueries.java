package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.CommonQueries;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.patient.definition.SqlPatientDataDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ListOfPatientsArtCohortCohortQueries {

  private CommonQueries commonQueries;

  private HivMetadata hivMetadata;

  @Autowired
  public ListOfPatientsArtCohortCohortQueries(
      CommonQueries commonQueries, HivMetadata hivMetadata) {
    this.commonQueries = commonQueries;
    this.hivMetadata = hivMetadata;
  }

  public CohortDefinition getPatientsInitiatedART() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Patients who initiated the ART between the cohort period");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "Cohort Start Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "Cohort End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    String arvStart = commonQueries.getARTStartDate(true);

    String query =
        "SELECT patient_id FROM ( "
            + arvStart
            + " ) initiated_art"
            + "   WHERE initiated_art.first_pickup BETWEEN :startDate AND :endDate "
            + "   GROUP BY patient_id";

    sqlCohortDefinition.setQuery(query);
    return sqlCohortDefinition;
  }

  /**
   * <b> 4 - Idade </b>
   *
   * @return
   */
  public DataDefinition getAge() {

    SqlPatientDataDefinition spdd = new SqlPatientDataDefinition();

    spdd.setName("Patient Age at Reporting Evaluation Date");
    spdd.addParameter(new Parameter("evaluationDate", "evaluationDate", Date.class));

    Map<String, Integer> valuesMap = new HashMap<>();

    String sql =
        "SELECT p.patient_id, FLOOR(DATEDIFF(:evaluationDate,ps.birthdate)/365) AS age FROM patient p "
            + "    INNER JOIN person ps ON p.patient_id=ps.person_id WHERE p.voided=0 AND ps.voided=0";

    StringSubstitutor substitutor = new StringSubstitutor(valuesMap);

    spdd.setQuery(substitutor.replace(sql));
    return spdd;
  }

  /**
   * <b>10 - Last Drug Pick-up Date on Ficha Mestre </b>
   *
   * <blockquote>
   *
   * <p>Print the Date (concept id 23866, value_datetime) of the most recent “Recepcao Levantou ARV”
   * (encounter type 52) until report evaluation date (concept id 23866, value_datetime<=
   * evaluationDate)
   *
   * </blockquote>
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getPatientsAndLastDrugPickUpDateOnFichaMestre() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("Last Drug Pick-up Date on Ficha Mestre");
    addSqlPatientDataDefinitionParameters(sqlPatientDataDefinition);

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("52", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    valuesMap.put("23866", hivMetadata.getArtDatePickupMasterCard().getConceptId());

    String query =
        " SELECT p.patient_id, "
            + "       Max(o.value_datetime) recent_date "
            + " FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON e.patient_id = p.patient_id "
            + "       INNER JOIN obs o "
            + "               ON o.encounter_id = e.encounter_id "
            + " WHERE  e.encounter_type = ${52} "
            + "       AND o.concept_id = ${23866} "
            + "       AND o.value_datetime <= :startDate "
            + "       AND e.voided = 0 "
            + "       AND e.location_id = :location "
            + "       AND p.voided = 0 "
            + "       AND o.voided = 0 "
            + " GROUP  BY p.patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>11 - Next Drug pick-up Date on Ficha Mestre</b>
   *
   * <blockquote>
   *
   * <p>Return the Date (concept id 23866, value_datetime) +30 days of the most recent “Recepcao
   * Levantou ARV” (encounter type 52) by report evaluation date ( concept id 23866,
   * value_datetime<= evaluationDate)
   *
   * </blockquote>
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getPatientsAndNextpickUpDateOnFichaMestre() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("Next Drug pick-up Date on Ficha Mestre");
    addSqlPatientDataDefinitionParameters(sqlPatientDataDefinition);

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("52", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    valuesMap.put("23866", hivMetadata.getArtDatePickupMasterCard().getConceptId());

    String query =
        " SELECT patient_id, DATE_ADD(recent_date, INTERVAL 30 DAY) AS next_pickup "
            + " FROM ( "
            + " SELECT p.patient_id, "
            + "       Max(o.value_datetime) recent_date "
            + " FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON e.patient_id = p.patient_id "
            + "       INNER JOIN obs o "
            + "               ON o.encounter_id = e.encounter_id "
            + " WHERE  e.encounter_type = ${52} "
            + "       AND o.concept_id = ${23866} "
            + "       AND o.value_datetime <= :startDate "
            + "       AND e.voided = 0 "
            + "       AND e.location_id = :location "
            + "       AND p.voided = 0 "
            + "       AND o.voided = 0 "
            + " GROUP  BY p.patient_id "
            + "    ) pickup";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Print the “Data da próxima consulta” (concept id 1410, value_datetime) of the most recent
   * clinical consultation registered on Ficha Clínica – MasterCard or Ficha de Seguimento
   * (encounter type 6 or 9) until report start date (encounter_datetime <= evaluationDate)
   *
   * </blockquote>
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getPatientsAndNextFollowUpConsultationDate() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("Next Follow up Consultation Date");
    sqlPatientDataDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("1410", hivMetadata.getReturnVisitDateConcept().getConceptId());

    String query =
        "SELECT     p.patient_id, "
            + "           Max(o.value_datetime) AS next_consultation "
            + " FROM       patient p "
            + " INNER JOIN encounter e "
            + " ON         p.patient_id = e.patient_id "
            + " INNER JOIN obs o "
            + " ON         e.encounter_id = o.encounter_id "
            + " INNER JOIN "
            + "           ( "
            + "                      SELECT     p.patient_id, "
            + "                                 Max(e.encounter_datetime) AS last_consultation_date "
            + "                      FROM       patient p "
            + "                      INNER JOIN encounter e "
            + "                      ON         e.patient_id = p.patient_id "
            + "                      INNER JOIN obs o "
            + "                      ON         o.encounter_id = e.encounter_id "
            + "                      WHERE      e.encounter_type IN(${6}, "
            + "                                                     ${9}) "
            + "                      AND        e.encounter_datetime <= :endDate "
            + "                      AND        e.location_id = :location "
            + "                      AND        e.voided = 0 "
            + "                      AND        p.voided = 0 "
            + "                      AND        o.voided = 0 "
            + "                      GROUP BY   p.patient_id ) last "
            + " ON         last.patient_id = p.patient_id "
            + " WHERE      e.encounter_type IN(${6}, "
            + "                               ${9}) "
            + " AND        e.encounter_datetime = last.last_consultation_date "
            + "AND        e.location_id = :location "
            + " AND        o.concept_id = ${1410} "
            + " AND        e.voided = 0 "
            + " AND        p.voided = 0 "
            + " AND        o.voided = 0 "
            + " GROUP BY   p.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>14 - Last state on Program Enrollment</b>
   *
   * <p>Select the most recent state for patients enrolled on program ARV (program id =2) and
   * patient state in ( 7 = Transferred Out; 8 = Suspended; 9 = Abandoned; 10 = Died). Tip: MAX
   * (Patient_State.start_date ) <= evaluationDate Note: If patient’s last state is different from
   * the ones identified for this source, the corresponding columns will be left ‘blank’.
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getLastStateOnProgramEnrollment() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("Last state on Program Enrollment");
    addSqlPatientDataDefinitionParameters(sqlPatientDataDefinition);

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("2", hivMetadata.getARTProgram().getProgramId());
    valuesMap.put(
        "7",
        hivMetadata
            .getTransferredOutToAnotherHealthFacilityWorkflowState()
            .getProgramWorkflowStateId());
    valuesMap.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("10", hivMetadata.getArtDeadWorkflowState().getProgramWorkflowStateId());
    valuesMap.put(
        "8", hivMetadata.getSuspendedTreatmentWorkflowState().getProgramWorkflowStateId());

    String query =
        "SELECT p.patient_id,"
            + "       ps.state"
            + " FROM   patient p"
            + "       INNER JOIN patient_program pg"
            + "               ON p.patient_id = pg.patient_id"
            + "       INNER JOIN patient_state ps"
            + "               ON pg.patient_program_id = ps.patient_program_id"
            + "       INNER JOIN (SELECT p.patient_id,"
            + "                          Max(ps.start_date) start_date"
            + "                   FROM   patient p"
            + "                          INNER JOIN patient_program pg"
            + "                                  ON p.patient_id = pg.patient_id"
            + "                          INNER JOIN patient_state ps"
            + "                                  ON pg.patient_program_id ="
            + "                                     ps.patient_program_id"
            + "                   WHERE  pg.program_id = ${2} AND pg.voided = 0 AND ps.voided = 0 "
            + "                          AND ps.start_date < :startDate"
            + "                      AND pg.location_id= :location "
            + "                   GROUP  BY p.patient_id)most_recent"
            + "               ON most_recent.patient_id = p.patient_id"
            + " WHERE  ps.start_date = most_recent.start_date"
            + "       AND pg.location_id= :location AND pg.voided = 0 AND ps.voided = 0 "
            + "                          AND ps.state IN ( ${7}, ${8}, ${9}, ${10} )"
            + "       AND pg.program_id = ${2}";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>15 - Last state date on Program Enrollment</b>
   *
   * <p>Select the last state date (MAX (Patient_State.start_date )) of the most recent state for
   * patients enrolled on program ARV (program id =2) and patient state in ( 7 = Transferred Out; 8
   * = Suspended; 9 = Abandoned; 10 = Died). Tip: MAX (Patient_State.start_date ) <= evaluationDate
   * Note: If patient’s last state is different from the ones identified for this source, the
   * corresponding columns will be left ‘blank’.
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getLastStateDateOnProgramEnrollment() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("Last state date on Program Enrollment");
    addSqlPatientDataDefinitionParameters(sqlPatientDataDefinition);

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("2", hivMetadata.getARTProgram().getProgramId());
    valuesMap.put(
        "7",
        hivMetadata
            .getTransferredOutToAnotherHealthFacilityWorkflowState()
            .getProgramWorkflowStateId());
    valuesMap.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("10", hivMetadata.getArtDeadWorkflowState().getProgramWorkflowStateId());
    valuesMap.put(
        "8", hivMetadata.getSuspendedTreatmentWorkflowState().getProgramWorkflowStateId());

    String query =
        "SELECT p.patient_id,"
            + "       ps.start_date"
            + " FROM   patient p"
            + "       INNER JOIN patient_program pg"
            + "               ON p.patient_id = pg.patient_id"
            + "       INNER JOIN patient_state ps"
            + "               ON pg.patient_program_id = ps.patient_program_id"
            + "       INNER JOIN (SELECT p.patient_id,"
            + "                          Max(ps.start_date) start_date"
            + "                   FROM   patient p"
            + "                          INNER JOIN patient_program pg"
            + "                                  ON p.patient_id = pg.patient_id"
            + "                          INNER JOIN patient_state ps"
            + "                                  ON pg.patient_program_id ="
            + "                                     ps.patient_program_id"
            + "                   WHERE  pg.program_id = ${2} AND pg.voided = 0 AND ps.voided = 0 "
            + "                          AND ps.start_date < :startDate"
            + "                      AND pg.location_id= :location "
            + "                   GROUP  BY p.patient_id)most_recent"
            + "               ON most_recent.patient_id = p.patient_id"
            + " WHERE  ps.start_date = most_recent.start_date"
            + "       AND pg.location_id= :location AND pg.voided = 0 AND ps.voided = 0 "
            + "                          AND ps.state IN ( ${7}, ${8}, ${9}, ${10} )"
            + "       AND pg.program_id = ${2}";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>16 - Last state on Clinical Consultation</b>
   *
   * <p>Select the most recent ficha clinica (encounter type 6) with Last STATE OF STAY ART PATIENT
   * (concept id 6273) value code ransferred Out or Suspended or Abandoned or Died (concept id IN
   * [1706, 1709, 1707, 1366 ]). Tip: MAX (encounter datetime ) <= evaluationDate’.
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getLastStateOnClinicalConsultation() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("Last state on Clinical Consultation");
    addSqlPatientDataDefinitionParameters(sqlPatientDataDefinition);

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("6273", hivMetadata.getStateOfStayOfArtPatient().getConceptId());
    valuesMap.put("1706", hivMetadata.getTransferredOutConcept().getConceptId());
    valuesMap.put("1707", hivMetadata.getAbandonedConcept().getConceptId());
    valuesMap.put("1366", hivMetadata.getPatientHasDiedConcept().getConceptId());
    valuesMap.put("1709", hivMetadata.getSuspendedTreatmentConcept().getConceptId());

    String query =
        "SELECT     p.patient_id, o.value_coded "
            + "FROM patient p INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "          INNER JOIN obs o ON  e.encounter_id = o.encounter_id "
            + "INNER JOIN( "
            + "          SELECT p.patient_id, Max(e.encounter_datetime) AS most_recent "
            + "      FROM patient p "
            + "               INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "               INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "      WHERE p.voided = 0 "
            + "        AND e.voided = 0 "
            + "        AND o.voided = 0 "
            + "        AND e.location_id = :location"
            + "        AND e.encounter_type = ${6} "
            + "        AND e.encounter_datetime <= :startDate "
            + "        AND o.concept_id = ${6273} "
            + "        AND o.value_coded IN (${1706}, ${1709}, ${1707}, ${1366}) "
            + "      GROUP BY p.patient_id ) recent_state ON recent_state.patient_id = p.patient_id "
            + "WHERE      p.voided = 0 "
            + "AND        e.voided = 0 "
            + "AND        o.voided = 0 "
            + "AND        e.location_id = :location "
            + "AND        e.encounter_type = ${6} "
            + "AND        o.concept_id = ${6273} "
            + "AND        o.value_coded IN (${1706}, ${1709} ,${1707}, ${1366}) "
            + "AND        e.encounter_datetime = recent_state.most_recent";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>17 - Last state date on Clinical Consultation</b>
   *
   * <p>Select the state date (MAX (encounter datetime ) ) of the most recent state registered on
   * ficha clinica (encounter type 6) and Last STATE OF STAY ART PATIENT (concept id 6273) value
   * code Transferred Out or Suspended or Abandoned or Died (concept id IN [1706, 1709, 1707, 1366
   * ]). Tip: MAX (encounter datetime ) <= evaluationDate
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getLastStateDateOnClinicalConsultation() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("Last state date on Clinical Consultation");
    addSqlPatientDataDefinitionParameters(sqlPatientDataDefinition);

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("6273", hivMetadata.getStateOfStayOfArtPatient().getConceptId());
    valuesMap.put("1706", hivMetadata.getTransferredOutConcept().getConceptId());
    valuesMap.put("1707", hivMetadata.getAbandonedConcept().getConceptId());
    valuesMap.put("1366", hivMetadata.getPatientHasDiedConcept().getConceptId());
    valuesMap.put("1709", hivMetadata.getSuspendedTreatmentConcept().getConceptId());

    String query =
        "SELECT p.patient_id, Max(e.encounter_datetime) AS most_recent "
            + "      FROM patient p "
            + "               INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "               INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "      WHERE p.voided = 0 "
            + "        AND e.voided = 0 "
            + "        AND o.voided = 0 "
            + "        AND e.location_id = :location "
            + "        AND e.encounter_type = ${6} "
            + "        AND e.encounter_datetime <= :startDate "
            + "        AND o.concept_id = ${6273} "
            + "        AND o.value_coded IN (${1706}, ${1709}, ${1707}, ${1366}) "
            + "      GROUP BY p.patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>18 - Last state on Ficha Resumo</b>
   *
   * <p>Select the most recent state registered on ficha resumo(encounter type 53) and Last STATE OF
   * STAY ART PATIENT (concept id 6272) value code Transferred Out or Suspended or Abandoned or Died
   * (concept id IN [1706, 1709, 1707, 1366 ]). Tip: MAX (obs datetime of concept id 6272 ) <=
   * evaluationDate Note 1: If patient’s last state is different from the ones identified for this
   * source, the corresponding columns will be left ‘blank’.
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getLastStateOnFichaResumo() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("Last state on Ficha Resumo");
    addSqlPatientDataDefinitionParameters(sqlPatientDataDefinition);

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6272", hivMetadata.getStateOfStayOfPreArtPatient().getConceptId());
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("1706", hivMetadata.getTransferredOutConcept().getConceptId());
    valuesMap.put("1707", hivMetadata.getAbandonedConcept().getConceptId());
    valuesMap.put("1366", hivMetadata.getPatientHasDiedConcept().getConceptId());
    valuesMap.put("1709", hivMetadata.getSuspendedTreatmentConcept().getConceptId());

    String query =
        "SELECT     p.patient_id, o.value_coded "
            + "FROM patient p INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "          INNER JOIN obs o ON  e.encounter_id = o.encounter_id "
            + "INNER JOIN( "
            + "          SELECT     p.patient_id, "
            + "                     max(o.obs_datetime) AS most_recent "
            + "          FROM       patient p "
            + "          INNER JOIN encounter e "
            + "          ON         p.patient_id = e.patient_id "
            + "          INNER JOIN obs o "
            + "          ON         e.encounter_id = o.encounter_id "
            + "          WHERE      p.voided = 0 "
            + "          AND        e.voided = 0 "
            + "          AND        o.voided = 0 "
            + "          AND        e.encounter_type = ${53} "
            + "          AND        e.location_id = :location "
            + "          AND        o.concept_id = ${6272} "
            + "          and        o.obs_datetime <= :startDate "
            + "          GROUP BY   p.patient_id ) recent_state ON recent_state.patient_id = p.patient_id "
            + "WHERE      p.voided = 0 "
            + "AND        e.voided = 0 "
            + "AND        o.voided = 0 "
            + "AND        e.location_id = :location "
            + "AND        e.encounter_type = ${53} "
            + "AND        o.concept_id = ${6272} "
            + "AND        o.value_coded IN (${1706}, ${1709}, ${1707}, ${1366}) "
            + "AND        o.obs_datetime = recent_state.most_recent";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>19 - Last state date on Ficha Resumo</b>
   *
   * <p>Select the state date (MAX obs datetime of concept id 6272 ) ) of the most recent state
   * registered on ficha clinica (encounter type 6) and Last STATE OF STAY ART PATIENT (concept id
   * 6273) value code Transferred Out or Suspended or Abandoned or Died (concept id IN [1706, 1709,
   * 1707, 1366 ]). Tip: MAX (obs datetime of concept id 6272 ) Note 1: If patient’s last state is
   * different from the ones identified for this source, the corresponding columns will be left
   * ‘blank’.
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getLastStateDateOnFichaResumo() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("Last state date on Ficha Resumo");
    addSqlPatientDataDefinitionParameters(sqlPatientDataDefinition);

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6272", hivMetadata.getStateOfStayOfPreArtPatient().getConceptId());
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("1706", hivMetadata.getTransferredOutConcept().getConceptId());
    valuesMap.put("1707", hivMetadata.getAbandonedConcept().getConceptId());
    valuesMap.put("1366", hivMetadata.getPatientHasDiedConcept().getConceptId());
    valuesMap.put("1709", hivMetadata.getSuspendedTreatmentConcept().getConceptId());

    String query =
        "SELECT     p.patient_id, o.obs_datetime "
            + "FROM patient p INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "          INNER JOIN obs o ON  e.encounter_id = o.encounter_id "
            + "INNER JOIN( "
            + "          SELECT     p.patient_id, "
            + "                     max(o.obs_datetime) AS most_recent "
            + "          FROM       patient p "
            + "          INNER JOIN encounter e "
            + "          ON         p.patient_id = e.patient_id "
            + "          INNER JOIN obs o "
            + "          ON         e.encounter_id = o.encounter_id "
            + "          WHERE      p.voided = 0 "
            + "          AND        e.voided = 0 "
            + "          AND        o.voided = 0 "
            + "          AND        e.encounter_type = ${53} "
            + "          AND        e.location_id = :location "
            + "          AND        o.concept_id = ${6272} "
            + "          and        o.obs_datetime <= :startDate "
            + "          GROUP BY   p.patient_id ) recent_state ON recent_state.patient_id = p.patient_id "
            + "WHERE      p.voided = 0 "
            + "AND        e.voided = 0 "
            + "AND        o.voided = 0 "
            + "AND        e.location_id = :location "
            + "AND        e.encounter_type = ${53} "
            + "AND        o.concept_id = ${6272} "
            + "AND        o.value_coded IN (${1706}, ${1709}, ${1707}, ${1366}) "
            + "AND        o.obs_datetime = recent_state.most_recent";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>20 - Last state on Home Visit Card </b>
   *
   * <p>Select the most recent Home visit card (encounter type 21) with Reason not found (concept id
   * IN [2031, 23944, 23945]) and value code Transferred Out or AUTO TRANSFER or Died (concept id IN
   * [1706, 23863, 1366 ]). Tip: MAX (encounter datetime ) <= evaluationDate
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getLastStateOnHomeVisitCard() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("Last state on Home Visit Card");
    addSqlPatientDataDefinitionParameters(sqlPatientDataDefinition);

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("21", hivMetadata.getBuscaActivaEncounterType().getEncounterTypeId());
    valuesMap.put("2031", hivMetadata.getReasonPatientNotFound().getConceptId());
    valuesMap.put(
        "23944", hivMetadata.getReasonPatientNotFoundByActivist2ndVisitConcept().getConceptId());
    valuesMap.put(
        "23945", hivMetadata.getReasonPatientNotFoundByActivist3rdVisitConcept().getConceptId());
    valuesMap.put("1366", hivMetadata.getPatientHasDiedConcept().getConceptId());
    valuesMap.put("23863", hivMetadata.getAutoTransferConcept().getConceptId());
    valuesMap.put("1706", hivMetadata.getTransferredOutConcept().getConceptId());

    String query =
        "SELECT     p.patient_id, o.value_coded "
            + " FROM patient p INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "           INNER JOIN obs o ON  e.encounter_id = o.encounter_id "
            + " INNER JOIN( SELECT  p.patient_id, max(e.encounter_datetime) AS most_recent "
            + "           FROM       patient p INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "           INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "           WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "           AND        e.encounter_type = ${21} "
            + "           AND        e.location_id = :location "
            + "           AND        o.concept_id IN ( ${2031}, ${23944}, ${23945}) "
            + "           AND        o.value_coded IN ( ${1706}, ${23863}, ${1366}) "
            + "           AND        e.encounter_datetime <= :startDate "
            + "           GROUP BY   p.patient_id ) recent_state ON recent_state.patient_id = p.patient_id "
            + "           WHERE  p.voided = 0 AND e.voided = 0 AND o.voided = 0 "
            + "           AND        e.encounter_type = ${21} "
            + "           AND        e.location_id = :location "
            + "           AND        o.concept_id IN ( ${2031}, ${23944}, ${23945}) "
            + "           AND        o.value_coded IN ( ${1706}, ${23863}, ${1366}) "
            + " AND        e.encounter_datetime = recent_state.most_recent";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>21 - Last state date on Home Visit Card </b>
   *
   * <p>Select the state date (MAX (encounter datetime ) ) of the most recent state registered on
   * Home visit card (encounter type 21) and Reason not found (concept id IN [2031, 23944, 23945])
   * and value code Transferred Out or AUTO TRANSFER or Died (concept id IN [1706, 23863, 1366 ]).
   * Tip: MAX (encounter datetime ) <= evaluationDate
   *
   * @return {@link DataDefinition}
   */
  public DataDefinition getLastStateDateOnHomeVisitCard() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("Last state date on Home Visit Card");
    addSqlPatientDataDefinitionParameters(sqlPatientDataDefinition);

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("21", hivMetadata.getBuscaActivaEncounterType().getEncounterTypeId());
    valuesMap.put("2031", hivMetadata.getReasonPatientNotFound().getConceptId());
    valuesMap.put(
        "23944", hivMetadata.getReasonPatientNotFoundByActivist2ndVisitConcept().getConceptId());
    valuesMap.put(
        "23945", hivMetadata.getReasonPatientNotFoundByActivist3rdVisitConcept().getConceptId());
    valuesMap.put("1366", hivMetadata.getPatientHasDiedConcept().getConceptId());
    valuesMap.put("23863", hivMetadata.getAutoTransferConcept().getConceptId());
    valuesMap.put("1706", hivMetadata.getTransferredOutConcept().getConceptId());

    String query =
        " SELECT     p.patient_id,  max(e.encounter_datetime) AS most_recent "
            + "           FROM       patient p "
            + "           INNER JOIN encounter e "
            + "           ON         p.patient_id = e.patient_id "
            + "           INNER JOIN obs o "
            + "           ON         e.encounter_id = o.encounter_id "
            + "           WHERE      p.voided = 0 "
            + "           AND        e.voided = 0 "
            + "           AND        o.voided = 0 "
            + "           AND        e.encounter_type = ${21} "
            + "           AND        e.location_id = :location "
            + "           AND        o.concept_id IN (${2031}, ${23944}, ${23945}) "
            + "           AND        o.value_coded IN (${1706}, ${23863}, ${1366}) "
            + "           AND        e.encounter_datetime <= :startDate "
            + "           GROUP BY   p.patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlPatientDataDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlPatientDataDefinition;
  }

  private void addSqlPatientDataDefinitionParameters(
      SqlPatientDataDefinition sqlPatientDataDefinition) {
    sqlPatientDataDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter("location", "location", Location.class));
  }
}
