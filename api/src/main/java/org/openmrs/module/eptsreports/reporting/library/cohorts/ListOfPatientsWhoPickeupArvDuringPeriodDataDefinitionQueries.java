package org.openmrs.module.eptsreports.reporting.library.cohorts;

import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.ListOfPatientsWhoPickeupArvDuringPeriodQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsQueriesUtil;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.patient.definition.SqlPatientDataDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class ListOfPatientsWhoPickeupArvDuringPeriodDataDefinitionQueries {

  private final HivMetadata hivMetadata;

  private final CommonMetadata commonMetadata;

  @Autowired
  public ListOfPatientsWhoPickeupArvDuringPeriodDataDefinitionQueries(
      HivMetadata hivMetadata, CommonMetadata commonMetadata) {
    this.hivMetadata = hivMetadata;
    this.commonMetadata = commonMetadata;
  }

  /**
   * <b>The system will show information from patient’s Last State (column G) and Last Patient State
   * Date (Sheet 1: Column G and Column H) as follows:</b>
   *
   * <ul>
   *   <li>
   *       <p>Last patient’s state informed on most recent active ART Program (Service TARV -
   *       Tratamento) where patient is enrolled by report generation date or
   *   <li>
   *       <p>Last patient’s state marked in “Mudança no Estado de Permanência TARV” in Ficha Resumo
   *       with the most recente date o state by report generation date or
   *   <li>
   *       <p>Patient’s state marked in “Mudança no Estado de Permanência TARV” in Ficha Clínica by
   *       report generation date Note1: The system will consider the most recent date from all
   *       sources listed above.
   * </ul>
   *
   * @param lastState Last State Flag is used to return last state or last state date column
   * @return {@link DataDefinition}
   */
  public DataDefinition getLastStateOrDateFromARTProgramOrFichaResumoOrClinica(boolean lastState) {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName(
        "Last state or state date on ART Program or Ficha Resumo or Ficha Clinica");
    addSqlCohortDefinitionParameters(sqlPatientDataDefinition);

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6272", hivMetadata.getStateOfStayOfPreArtPatient().getConceptId());
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("1706", hivMetadata.getTransferredOutConcept().getConceptId());
    valuesMap.put("1707", hivMetadata.getAbandonedConcept().getConceptId());
    valuesMap.put("1366", hivMetadata.getPatientHasDiedConcept().getConceptId());
    valuesMap.put("1709", hivMetadata.getSuspendedTreatmentConcept().getConceptId());
    valuesMap.put("23903", hivMetadata.getNegativeDiagnosisConcept().getConceptId());
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("6273", hivMetadata.getStateOfStayOfArtPatient().getConceptId());
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
    valuesMap.put("1369", commonMetadata.getTransferFromOtherFacilityConcept().getConceptId());
    valuesMap.put("6269", hivMetadata.getActiveOnProgramConcept().getConceptId());
    valuesMap.put(
        "state_6", hivMetadata.getPateintActiveArtWorkflowState().getProgramWorkflowStateId());
    valuesMap.put(
        "29",
        hivMetadata
            .getPateintTransferedFromOtherFacilityWorkflowState()
            .getProgramWorkflowStateId());
    valuesMap.put("1705", hivMetadata.getRestartConcept().getConceptId());

    String query = " ";
    if (lastState) {
      query += " SELECT p.patient_id, o.value_coded as last_state ";
    } else {
      query +=
          " SELECT p.patient_id, CASE "
              + "   WHEN  e.encounter_type = ${53} THEN o.obs_datetime   "
              + "   WHEN e.encounter_type = ${6} THEN e.encounter_datetime   "
              + "   END AS state_date ";
    }

    query +=
        "      FROM patient p  "
            + "                       INNER JOIN encounter e ON p.patient_id = e.patient_id   "
            + "                       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                       INNER JOIN( "
            + "                           SELECT recent.patient_id, MAX(recent.most_recent) as most_recent FROM ( "
            + getUnionQuery()
            + "                                 ) recent "
            + "                           GROUP BY recent.patient_id "
            + "                       )states on p.patient_id = states.patient_id "
            + " WHERE   "
            + "  e.voided = 0   "
            + "  AND p.voided = 0   "
            + "  AND o.voided = 0   "
            + "  AND e.location_id = :location "
            + "AND (   "
            + "         (   "
            + "                 e.encounter_type = ${53}   "
            + "                 AND        o.concept_id = ${6272}   "
            + "                 AND        o.value_coded IN (${1705}, ${1706}, ${1709}, ${1707}, ${1366}, ${23903}, ${1369})   "
            + "                 AND        o.obs_datetime = states.most_recent   "
            + "         )   "
            + "    OR   "
            + "         (   "
            + "                 e.encounter_type = ${6}   "
            + "                 AND        o.concept_id = ${6273}   "
            + "                 AND        o.value_coded IN (${1705}, ${1706}, ${1709} ,${1707}, ${1366}, ${23903}, ${1369})   "
            + "                 AND        e.encounter_datetime = states.most_recent   "
            + "                 AND NOT EXISTS  (   "
            + "                     SELECT eee.encounter_id FROM encounter eee  "
            + "                           INNER JOIN obs o3 ON eee.encounter_id = o3.encounter_id   "
            + "                     WHERE eee.patient_id = p.patient_id AND   "
            + "                             eee.encounter_type = ${53}   "
            + "                       AND        o3.concept_id = ${6272}   "
            + "                       AND        o3.value_coded IN (${1705}, ${1706}, ${1709}, ${1707}, ${1366}, ${23903}, ${1369})   "
            + "                       AND        o3.obs_datetime = states.most_recent   "
            + "                     GROUP BY eee.patient_id   "
            + "                                 )"
            + "         )   "
            + "   )"
            + "GROUP BY p.patient_id "
            + " UNION ";

    if (lastState) {
      query += " SELECT pg.patient_id, ps.state as last_state ";
    } else {
      query += " SELECT pg.patient_id, ps.start_date as state_date ";
    }

    query +=
        "       FROM "
            + " patient_program pg"
            + "    INNER JOIN patient_state ps"
            + "        ON pg.patient_program_id = ps.patient_program_id"
            + "                               INNER JOIN ("
            + "                                   SELECT recent.patient_id, MAX(recent.most_recent) as most_recent FROM ( "
            + getUnionQuery()
            + "                                         ) recent "
            + "                                   GROUP BY recent.patient_id "
            + "                                )states on pg.patient_id = states.patient_id "
            + "  WHERE pg.program_id = ${2} "
            + "  AND ps.state IN (${state_6}, ${7}, ${8}, ${9}, ${10}, ${29} )"
            + "  AND ps.start_date = states.most_recent"
            + "  AND pg.voided = 0"
            + "  AND ps.voided = 0"
            + "  AND NOT EXISTS ("
            + "        SELECT ee.encounter_id"
            + "        FROM encounter ee"
            + "                 INNER JOIN obs o2 ON ee.encounter_id = o2.encounter_id"
            + "        WHERE ee.patient_id = pg.patient_id"
            + "          AND ee.encounter_type = ${6}"
            + "          AND o2.concept_id = ${6273}"
            + "          AND o2.value_coded IN (${1705},${1706}, ${1709}, ${1707}, ${1366}, ${23903}, ${1369})"
            + "          AND o2.obs_datetime = states.most_recent GROUP BY ee.patient_id)"
            + "          AND NOT EXISTS ( "
            + "                       SELECT eee.encounter_id FROM encounter eee"
            + "                                                    INNER JOIN obs o3 ON eee.encounter_id = o3.encounter_id"
            + "                       WHERE eee.patient_id = pg.patient_id "
            + "                           AND   eee.encounter_type = ${53}"
            + "                           AND        o3.concept_id = ${6272}"
            + "                           AND        o3.value_coded IN (${1705},${1706}, ${1709}, ${1707}, ${1366}, ${23903}, ${1369})"
            + "                           AND        o3.obs_datetime = states.most_recent"
            + "                       GROUP BY eee.encounter_id   "
            + "                       ) "
            + " GROUP BY pg.patient_id ";

    sqlPatientDataDefinition.setQuery(new StringSubstitutor(valuesMap).replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>The system will show information from Last State Source as follows:</b>
   *
   * <p>If the value from Last State and Last State Date is Ficha Resumo (ARV_PICK_FR12) then the
   * value of Last State Source (Column I) should be Ficha Resumo, or
   *
   * <p>If the value from Last State and Last State Date is Clinical Consultation (ARV_PICK_FR12)
   * then the value of Last State Source (Column I) should be Ficha Clínica, or
   *
   * <p>If the value from Last State and Last State Date is Program Enrolment (ARV_PICK_FR12) then
   * the value of Last State Source (Column I) should be Programa SESP
   *
   * @see #getLastStateOrDateFromARTProgramOrFichaResumoOrClinica(boolean) to check how these
   *     (ARV_PICK_FR12) bullets are computed
   * @return {@link DataDefinition}
   */
  public DataDefinition getLastStateSourceFromARTProgramOrFichaResumoOrClinica() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName(
        "Last state source on ART Program or Ficha Resumo or Ficha Clinica");
    addSqlCohortDefinitionParameters(sqlPatientDataDefinition);

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6272", hivMetadata.getStateOfStayOfPreArtPatient().getConceptId());
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("1706", hivMetadata.getTransferredOutConcept().getConceptId());
    valuesMap.put("1707", hivMetadata.getAbandonedConcept().getConceptId());
    valuesMap.put("1366", hivMetadata.getPatientHasDiedConcept().getConceptId());
    valuesMap.put("1709", hivMetadata.getSuspendedTreatmentConcept().getConceptId());
    valuesMap.put("23903", hivMetadata.getNegativeDiagnosisConcept().getConceptId());
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("6273", hivMetadata.getStateOfStayOfArtPatient().getConceptId());
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
    valuesMap.put("1369", commonMetadata.getTransferFromOtherFacilityConcept().getConceptId());
    valuesMap.put("6269", hivMetadata.getActiveOnProgramConcept().getConceptId());
    valuesMap.put(
        "state_6", hivMetadata.getPateintActiveArtWorkflowState().getProgramWorkflowStateId());
    valuesMap.put(
        "29",
        hivMetadata
            .getPateintTransferedFromOtherFacilityWorkflowState()
            .getProgramWorkflowStateId());
    valuesMap.put("1705", hivMetadata.getRestartConcept().getConceptId());

    String query =
        "    SELECT p.patient_id, e.encounter_type  FROM patient p  "
            + "                       INNER JOIN encounter e ON p.patient_id = e.patient_id   "
            + "                       INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                       INNER JOIN( "
            + "                           SELECT recent.patient_id, MAX(recent.most_recent) as most_recent FROM ( "
            + getUnionQuery()
            + "                                 ) recent "
            + "                           GROUP BY recent.patient_id "
            + "                       )states on p.patient_id = states.patient_id "
            + " WHERE   "
            + "  e.voided = 0   "
            + "  AND p.voided = 0   "
            + "  AND o.voided = 0   "
            + "  AND e.location_id = :location "
            + "AND (   "
            + "         (   "
            + "            e.encounter_type = ${53}   "
            + "            AND        o.concept_id = ${6272}   "
            + "            AND        o.value_coded IN (${1705}, ${1706}, ${1709}, ${1707}, ${1366}, ${23903}, ${1369})   "
            + "            AND        o.obs_datetime = states.most_recent   "
            + "        )   "
            + "    OR   "
            + "       (   "
            + "            e.encounter_type = ${6}   "
            + "            AND        o.concept_id = ${6273}   "
            + "            AND        o.value_coded IN (${1705}, ${1706}, ${1709} ,${1707}, ${1366}, ${23903}, ${1369})   "
            + "            AND        e.encounter_datetime = states.most_recent   "
            + "            AND NOT EXISTS  (   "
            + "                           SELECT eee.encounter_id FROM encounter eee  "
            + "                                 INNER JOIN obs o3 ON eee.encounter_id = o3.encounter_id   "
            + "                           WHERE eee.patient_id = p.patient_id AND   "
            + "                                   eee.encounter_type = ${53}   "
            + "                              AND        o3.concept_id = ${6272}   "
            + "                             AND        o3.value_coded IN (${1705}, ${1706}, ${1709}, ${1707}, ${1366}, ${23903}, ${1369})   "
            + "                              AND        o3.obs_datetime = states.most_recent   "
            + "                           GROUP BY eee.patient_id   )"
            + "        )  "
            + " )"
            + "GROUP BY p.patient_id "
            + " UNION "
            + " SELECT pg.patient_id, pg.program_id FROM "
            + " patient_program pg"
            + "    INNER JOIN patient_state ps"
            + "        ON pg.patient_program_id = ps.patient_program_id"
            + "                               INNER JOIN ("
            + "                                      SELECT recent.patient_id, MAX(recent.most_recent) as most_recent FROM ( "
            + getUnionQuery()
            + "                                           ) recent "
            + "                                        GROUP BY recent.patient_id "
            + "                               )states on pg.patient_id = states.patient_id "
            + "  WHERE pg.program_id = ${2} "
            + "  AND ps.state IN (${state_6}, ${7}, ${8}, ${9}, ${10}, ${29} )"
            + "  AND   ps.start_date = states.most_recent"
            + "  AND pg.voided = 0"
            + "  AND ps.voided = 0"
            + "  AND NOT EXISTS ("
            + "        SELECT ee.encounter_id"
            + "        FROM encounter ee"
            + "                 INNER JOIN obs o2 ON ee.encounter_id = o2.encounter_id"
            + "        WHERE ee.patient_id = pg.patient_id"
            + "          AND ee.encounter_type = ${6}"
            + "          AND o2.concept_id = ${6273}"
            + "          AND o2.value_coded IN (${1705},${1706}, ${1709}, ${1707}, ${1366}, ${23903}, ${1369})"
            + "          AND o2.obs_datetime = states.most_recent GROUP BY ee.patient_id)"
            + "  AND NOT EXISTS ( "
            + "                   SELECT eee.encounter_id FROM encounter eee"
            + "                                                    INNER JOIN obs o3 ON eee.encounter_id = o3.encounter_id"
            + "                   WHERE eee.patient_id = pg.patient_id "
            + "                     AND      eee.encounter_type = ${53}"
            + "                     AND        o3.concept_id = ${6272}"
            + "                     AND        o3.value_coded IN (${1705},${1706}, ${1709}, ${1707}, ${1366}, ${23903}, ${1369})"
            + "                     AND        o3.obs_datetime = states.most_recent"
            + "                   GROUP BY eee.patient_id   ) "
            + " GROUP BY pg.patient_id ";

    sqlPatientDataDefinition.setQuery(new StringSubstitutor(valuesMap).replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b>Patient’s Most Recent Drug Pick-Up Date on FILA</b>
   *
   * <p>Art Pickup Date: MAX(encounter.encounter_datetime) between the selected report start date
   * and end date of S.TARV: FARMACIA (ID=18) as Last ARV Pick-Up Date on FILA
   *
   * @return {@link CohortDefinition}
   */
  public DataDefinition getLastDrugPickupDate() {

    SqlPatientDataDefinition sqlPatientDataDefinition = new SqlPatientDataDefinition();
    sqlPatientDataDefinition.setName("Most Recent Drug Pick-Up Date on FILA");
    sqlPatientDataDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("18", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());

    String query =
        " SELECT p.patient_id, MAX(e.encounter_datetime) as last_pickup_date"
            + " FROM   patient p  "
            + "          INNER JOIN encounter e  "
            + "                          ON p.patient_id = e.patient_id  "
            + " WHERE  p.voided = 0  "
            + "          AND e.voided = 0  "
            + "          AND e.location_id = :location "
            + "          AND e.encounter_type = ${18} "
            + "         AND e.encounter_datetime BETWEEN :startDate AND :endDate "
            + " GROUP BY p.patient_id  ";

    sqlPatientDataDefinition.setQuery(new StringSubstitutor(valuesMap).replace(query));

    return sqlPatientDataDefinition;
  }

  /**
   * <b> this method will generate one union separeted query based on the given queries</b>
   *
   * @return {@link String}
   */
  private String getUnionQuery() {

    EptsQueriesUtil queriesUtil = new EptsQueriesUtil();

    return queriesUtil
        .unionBuilder(
            ListOfPatientsWhoPickeupArvDuringPeriodQueries.getLastStateOfStayOnFichaResumo())
        .union(ListOfPatientsWhoPickeupArvDuringPeriodQueries.getLastStateOfStayOnFichaClinica())
        .union(ListOfPatientsWhoPickeupArvDuringPeriodQueries.getLastStateOfStayOnArtProgram())
        .buildQuery();
  }

  private void addSqlCohortDefinitionParameters(SqlPatientDataDefinition sqlPatientDataDefinition) {
    sqlPatientDataDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlPatientDataDefinition.addParameter(new Parameter("location", "location", Location.class));
  }
}
