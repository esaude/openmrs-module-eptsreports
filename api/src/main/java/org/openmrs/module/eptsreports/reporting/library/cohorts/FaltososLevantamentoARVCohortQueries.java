package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FaltososLevantamentoARVCohortQueries {

  private HivMetadata hivMetadata;
  private ListOfPatientsEligibleForVLCohortQueries listOfPatientsEligibleForVLCohortQueries;

  @Autowired
  public FaltososLevantamentoARVCohortQueries(
      ListOfPatientsEligibleForVLCohortQueries listOfPatientsEligibleForVLCohortQueries,
      HivMetadata hivMetadata) {
    this.listOfPatientsEligibleForVLCohortQueries = listOfPatientsEligibleForVLCohortQueries;
    this.hivMetadata = hivMetadata;
  }

  public CohortDefinition getDenominator() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Relatório de Faltosos ao Levantamento de ARV");
    addParameters(cd);

    CohortDefinition chdA = getA();
    CohortDefinition chdB = getB();

    cd.addSearch(
        "A",
        EptsReportUtils.map(
            chdA, "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "B",
        EptsReportUtils.map(
            chdB, "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("A AND NOT B");

    return cd;
  }

  public CohortDefinition getNumerator() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Numerator - Select all patients from the A (Denominator) and filter");
    addParameters(cd);

    CohortDefinition chdDenominator = getDenominator();
    CohortDefinition chdMoreThan7Days =
        getPatientsWithMoreThan7DaysBetweenPickupDateAndLastNextScheduled();

    cd.addSearch(
        "denominator",
        EptsReportUtils.map(
            chdDenominator, "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "moreThan7Days",
        EptsReportUtils.map(
            chdMoreThan7Days, "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("denominator AND moreThan7Days");

    return cd;
  }

  public CohortDefinition getA() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Denominator - Last Next Schedulled Pickup");
    addParameters(cd);

    CohortDefinition chdProximoLevantamentoFila = getPatientsWithProximoLevantamentoOnFila();
    CohortDefinition chdLevantamentoPlus30Days =
        getPatientsWithMostRecentDataDeLevantamentoPlus30Days();

    cd.addSearch(
        "A1",
        EptsReportUtils.map(
            chdProximoLevantamentoFila,
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "A2",
        EptsReportUtils.map(
            chdLevantamentoPlus30Days,
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("A1 OR A2");

    return cd;
  }

  public CohortDefinition getB() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName(
        "E1- exclude all patients who are transferred OR E2- exclude all patients who died ");
    addParameters(cd);

    CohortDefinition chdRegisteredInProgramState =
        getPatientsTransferredOutRegisteredInProgramState();
    CohortDefinition chdRegisteredInFichaResumoAndClinica =
        getPatientsTransferredOutRegisteredInFichaResumoAndMasterCard();
    CohortDefinition chdWhoHaveDrugsPickUp = getPatientsWhoHaveDrugsPickUpOrConsultation();
    CohortDefinition chdWhoDied = getPatientsWhoDiedE2();

    cd.addSearch(
        "E11",
        EptsReportUtils.map(
            chdRegisteredInProgramState,
            "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.addSearch(
        "E12",
        EptsReportUtils.map(
            chdRegisteredInFichaResumoAndClinica,
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "E13",
        EptsReportUtils.map(
            chdWhoHaveDrugsPickUp,
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch("E20", EptsReportUtils.map(chdWhoDied, "endDate=${endDate},location=${location}"));

    cd.setCompositionString("((E11 OR E12) AND (E13)) OR E20");
    return cd;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Select all patients with the most recent date from the following sources as <b>Last Next
   * Scheduled Pick Up</b>
   *
   * <ul>
   *   <li>the “Data do próximo levantamento” (concept id 5096, value_datetime) from the most recent
   *       FILA (encounter type 18) by report start date(encounter_datetime <= startDate) and the
   *       “Data do próximo levantamento” between startdate and enddate
   * </ul>
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWithProximoLevantamentoOnFila() {
    return listOfPatientsEligibleForVLCohortQueries.getLastNextScheduledPickUpDate();
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Select all patients with the most recent date from the following sources as <b>Last Next
   * Scheduled Pick Up</b>
   *
   * <ul>
   *   <li>the most recent “Data de Levantamento” (concept_id 23866, value_datetime) + 30 days, from
   *       “Recepcao Levantou ARV” (encounter type 52) with concept “Levantou ARV” (concept_id
   *       23865) set to “SIM” (Concept id 1065) by report start date (value_datetime <= startDate)
   * </ul>
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWithMostRecentDataDeLevantamentoPlus30Days() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Demoninator - Most recent ARV pickup");
    addSqlCohortDefinitionParameters(sqlCohortDefinition);
    ;

    String sql = getPatientsWithMostRecentDataDeLevantamentoPlus30Days(false);
    sqlCohortDefinition.setQuery(sql);
    return sqlCohortDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>All transferred-outs registered in Patient Program State by reporting end date, i.e LAST
   * Transferred out state in program enrollment by end of period
   *
   * <p>Patient_program.program_id =2 = SERVICO TARV-TRATAMENTO and Patient_State.state = 7
   * (Transferred-out) and max(Patient_State.start_date) <= enddate Patient_state.end_date is null
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsTransferredOutRegisteredInProgramState() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Demoninator - Most recent ARV pickup");
    addSqlCohortDefinitionParameters(sqlCohortDefinition);

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("2", hivMetadata.getARTProgram().getProgramId());
    valuesMap.put(
        "7",
        hivMetadata
            .getTransferredOutToAnotherHealthFacilityWorkflowState()
            .getProgramWorkflowStateId());

    String query =
        " SELECT patient_id "
            + "FROM   (SELECT p.patient_id, "
            + "               Max(ps.start_date) recent_startdate "
            + "        FROM   patient p "
            + "               INNER JOIN patient_program pg "
            + "                       ON pg.patient_id = p.patient_id "
            + "               INNER JOIN patient_state ps "
            + "                       ON ps.patient_program_id = pg.patient_program_id "
            + "        WHERE  pg.program_id = ${2} "
            + "               AND pg.location_id = :location "
            + "               AND ps.state = ${7} "
            + "               AND ps.end_date IS NULL "
            + "               AND p.voided = 0 "
            + "               AND pg.voided = 0 "
            + "               AND ps.voided = 0 "
            + "        GROUP  BY p.patient_id "
            + "        HAVING recent_startdate <= :endDate) transfered_out "
            + "GROUP  BY patient_id ";
    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));
    return sqlCohortDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>All transferred-outs registered in Ficha Resumo and Ficha Clinica of Master Card by
   * reporting end date
   *
   * <ul>
   *   <li>Encounter Type ID= 6 Estado de Permanencia (Concept Id 6273) = Transferred-out (Concept
   *       ID 1706) Encounter_datetime <= Report EndDate
   *   <li><b>OR</b> Encounter Type ID= 53 Estado de Permanencia (Concept Id 6272) = Transferred-out
   *       (Concept ID 1706) obs_datetime <= Report EndDate)
   * </ul>
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsTransferredOutRegisteredInFichaResumoAndMasterCard() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "All transferred-outs registered in Ficha Resumo and Ficha Clinica of Master Card ");
    addSqlCohortDefinitionParameters(sqlCohortDefinition);

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("6273", hivMetadata.getStateOfStayOfArtPatient().getConceptId());
    valuesMap.put("1706", hivMetadata.getTransferredOutConcept().getConceptId());
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("6272", hivMetadata.getStateOfStayOfPreArtPatient().getConceptId());

    String query =
        " SELECT patient_id "
            + "FROM   (SELECT p.patient_id "
            + "        FROM   patient p "
            + "               INNER JOIN encounter e "
            + "                       ON e.patient_id = p.patient_id "
            + "               INNER JOIN obs o "
            + "                       ON o.encounter_id = e.encounter_id "
            + "        WHERE  e.encounter_type = ${6} "
            + "               AND e.location_id = :location "
            + "               AND o.concept_id = ${6273} "
            + "               AND o.value_coded = ${1706} "
            + "               AND e.encounter_datetime <= :endDate "
            + "               AND p.voided = 0 "
            + "               AND e.voided = 0 "
            + "               AND o.voided = 0 "
            + "        GROUP  BY p.patient_id "
            + "        UNION "
            + "        SELECT p.patient_id "
            + "        FROM   patient p "
            + "               INNER JOIN encounter e "
            + "                       ON e.patient_id = p.patient_id "
            + "               INNER JOIN obs o "
            + "                       ON o.encounter_id = e.encounter_id "
            + "        WHERE  e.encounter_type = ${53} "
            + "               AND o.concept_id = ${6272} "
            + "               AND o.value_coded = ${1706} "
            + "               AND e.location_id = :location "
            + "               AND o.obs_datetime <= :endDate "
            + "               AND p.voided = 0 "
            + "               AND e.voided = 0 "
            + "               AND o.voided = 0 "
            + "        GROUP  BY p.patient_id) trasfered_out "
            + "GROUP  BY patient_id  ";
    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));
    return sqlCohortDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Exclude all patients who after the most recent date from 1.1 to 1.2, have a drugs pick up or
   * Consultation
   *
   * <ul>
   *   <li>Encounter Type ID= 6, 9, 18 and encounter_datetime> the most recent date and <= Report
   *       EndDate
   *   <li>Encounter Type ID = 52 and “Data de Levantamento” (Concept Id 23866 value_datetime) > the
   *       most recent date and <= Report EndDate
   * </ul>
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWhoHaveDrugsPickUpOrConsultation() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Exclude all patients who after the most recent date");
    addSqlCohortDefinitionParameters(sqlCohortDefinition);

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("18", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    valuesMap.put("52", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    valuesMap.put("23866", hivMetadata.getArtDatePickupMasterCard().getConceptId());

    String query =
        "SELECT pickedup_drugs.patient_id "
            + "FROM   (SELECT p.patient_id, "
            + "               Max(e.encounter_datetime) recent_date "
            + "        FROM   patient p "
            + "               INNER JOIN encounter e "
            + "                       ON e.patient_id = p.patient_id "
            + "               INNER JOIN obs o "
            + "                       ON o.encounter_id = e.encounter_id "
            + "        WHERE  e.encounter_type IN ( ${6}, ${9}, ${18} ) "
            + "               AND e.location_id = :location "
            + "               AND p.voided = 0 "
            + "               AND e.voided = 0 "
            + "               AND o.voided = 0 "
            + "        GROUP  BY p.patient_id "
            + "        HAVING recent_date <= :endDate "
            + "        UNION "
            + "        SELECT p.patient_id, "
            + "               Max(o.value_datetime) recent_date "
            + "        FROM   patient p "
            + "               INNER JOIN encounter e "
            + "                       ON e.patient_id = p.patient_id "
            + "               INNER JOIN obs o "
            + "                       ON o.encounter_id = e.encounter_id "
            + "        WHERE  e.encounter_type = ${52} "
            + "               AND e.location_id = :location "
            + "               AND o.concept_id = ${23866} "
            + "               AND p.voided = 0 "
            + "               AND e.voided = 0 "
            + "               AND o.voided = 0 "
            + "        GROUP  BY p.patient_id "
            + "        HAVING recent_date <= :endDate) pickedup_drugs "
            + "GROUP  BY patient_id  ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));
    return sqlCohortDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Select all patients from the A (Denominator) and filter
   *
   * <ul>
   *   <li>All patients with more than 7 days between
   *   <li>The last pick up between Fila (encounter type 18, encounter datetime) and Master card
   *       Levantou ARV (encounter type 52,(concept_id 23866, value_datetime) ) by report enddate as
   *       <b>data de levantamento</b> minus “Last Next Scheduled Pick Up” should be > 7
   * </ul>
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWithMoreThan7DaysBetweenPickupDateAndLastNextScheduled() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Select all patients from the A (Denominator) and filter ");
    addSqlCohortDefinitionParameters(sqlCohortDefinition);

    String mostRecentDataLevantamento = getPatientsWithMostRecentDataDeLevantamentoPlus30Days(true);
    String mostRecentDateFromFila =
        listOfPatientsEligibleForVLCohortQueries.getLastNextScheduledPickUpDate(true);

    String lastPickupBetweenFilaAndMasterCard =
        getPatientsAndLastPickupDateBetweenFilaAndMasterCard();
    String query =
        " SELECT more_days.patient_id FROM( "
            + " "
            + "                SELECT schedule.patient_id,    MAX(recent_datetime) scheduled_date "
            + "                FROM( "
            + mostRecentDataLevantamento
            + "                        UNION "
            + mostRecentDateFromFila
            + "                    ) AS schedule "
            + "                GROUP BY "
            + "                schedule.patient_id "
            + "                 "
            + "                ) more_days "
            + "                 "
            + "                INNER JOIN ( "
            + lastPickupBetweenFilaAndMasterCard
            + "                            ) last_pickup ON last_pickup.patient_id = more_days.patient_id "
            + "                WHERE TIMESTAMPDIFF(DAY ,more_days.scheduled_date, last_pickup.pickup_date) > 7 "
            + "                 "
            + "GROUP BY more_days.patient_id ";

    sqlCohortDefinition.setQuery(query);
    return sqlCohortDefinition;
  }

  /**
   * <b>2.5</b> - Exclude all patients who after the most recent date from 2.1 to 2.4, have a drugs
   * pick up or consultation: Encounter Type ID= 6, 9, 18 and encounter_datetime> the most recent
   * date OR Encounter Type ID = 52 and “Data de Levantamento” (Concept Id 23866 value_datetime) >
   * the most recent date.
   *
   * @return
   */
  public CohortDefinition getPatientsWhoDiedE2() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" All patients that started ART during inclusion period ");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "EndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();

    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    map.put("18", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    map.put("52", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    map.put("23866", hivMetadata.getArtDatePickupMasterCard().getConceptId());

    String deathDayInProgramState = getPatientsDeathDayRegisteredInProgramState();
    String deathsRegisteredInDemographics = getPatientsDeathsRegisteredInDemographics();
    String deathRegisteredInFichaClinica = getPatientsAndDeathDayRegisteredInFichaClinica();
    String deathRegisteredInFichaResumo = getPatientsAndDeathDayRegisteredInFichaResumo();
    String deathDayRegisteredInLastHomeVisit =
        getPatientsAndDeathsDayRegisteredInLastHomeVisitCard();

    String query =
        "SELECT patient_id "
            + "FROM   (SELECT transferout.patient_id,   Max(transferout.transferout_date) transferout_date "
            + "        FROM   ( "
            + deathDayRegisteredInLastHomeVisit
            + "                UNION "
            + deathRegisteredInFichaClinica
            + "                UNION "
            + deathDayInProgramState
            + "                UNION "
            + deathsRegisteredInDemographics
            + "                UNION "
            + deathRegisteredInFichaResumo
            + "                ) transferout "
            + "        GROUP  BY transferout.patient_id) max_transferout "
            + " WHERE  max_transferout.patient_id NOT IN (SELECT p.patient_id "
            + "                                          FROM   patient p "
            + "                                                     JOIN encounter e "
            + "                                                          ON p.patient_id = "
            + "                                                             e.patient_id "
            + "                                          WHERE  p.voided = 0 "
            + "                                            AND e.voided = 0 "
            + "                                            AND e.encounter_type IN (${6},${9},${18})  "
            + "                                            AND e.location_id = :location "
            + "                                            AND "
            + "                                                  e.encounter_datetime > max_transferout.transferout_date AND max_transferout.patient_id = p.patient_id "
            + "                                            AND "
            + "                                                  e.encounter_datetime <= :endDate "
            + "                                          UNION "
            + "                                          SELECT p.patient_id "
            + "                                          FROM   patient p "
            + "                                                     JOIN encounter e "
            + "                                                          ON p.patient_id = "
            + "                                                             e.patient_id "
            + "                                                     JOIN obs o "
            + "                                                          ON e.encounter_id = "
            + "                                                             o.encounter_id "
            + "                                          WHERE  p.voided = 0"
            + "                                            AND e.voided = 0 "
            + "                                            AND e.encounter_type =  ${52} "
            + "                                            AND e.location_id = :location "
            + "                                            AND o.concept_id =  ${23866} "
            + "                                            AND o.value_datetime > max_transferout.transferout_date AND max_transferout.patient_id = p.patient_id"
            + "                                            AND o.value_datetime <= :endDate); ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>All deaths registered in Patient Program State by reporting end date
   *
   * <ul>
   *   <li>Patient_program.program_id =2 = SERVICO TARV-TRATAMENTO and Patient_State.state = 10
   *       (Died) patient_State.start_date <= Report End Date Patient_state.end_date is null
   * </ul>
   *
   * </blockquote>
   *
   * @return {@link String}
   */
  public String getPatientsDeathDayRegisteredInProgramState() {

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("2", hivMetadata.getARTProgram().getProgramId());
    valuesMap.put("10", hivMetadata.getArtDeadWorkflowState().getProgramWorkflowStateId());

    String query =
        "   SELECT pg.patient_id, (ps.start_date) AS transferout_date FROM patient p "
            + "   INNER JOIN patient_program pg ON p.patient_id=pg.patient_id "
            + "   INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id "
            + "   WHERE pg.voided=0 AND ps.voided=0 AND p.voided=0 "
            + "   AND pg.program_id=${2} AND ps.state=${10} AND ps.end_date is null "
            + "   AND ps.start_date <= :endDate AND location_id=:location ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    return stringSubstitutor.replace(query);
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>All deaths registered in Patient Program State by reporting end date
   *
   * <ul>
   *   <li>Patient_program.program_id =2 = SERVICO TARV-TRATAMENTO and Patient_State.state = 10
   *       (Died) patient_State.start_date <= Report End Date Patient_state.end_date is null
   * </ul>
   *
   * </blockquote>
   *
   * @return {@link String}
   */
  public String getPatientsDeathsRegisteredInDemographics() {

    Map<String, Integer> valuesMap = new HashMap<>();

    String query =
        "        SELECT p.person_id, (ps.start_date) AS transferout_date   "
            + "     FROM person p  "
            + "     INNER JOIN patient_program pg ON p.person_id=pg.patient_id "
            + "     INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id "
            + "     WHERE p.dead= 1  "
            + "     AND p.death_date <= :endDate  "
            + "     AND p.voided=0 ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    return stringSubstitutor.replace(query);
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>All deaths registered in Ficha Resumo and Ficha Clinica of Master Card by reporting end date
   *
   * <ul>
   *   <li>OR Encounter Type ID= 53 Estado de Permanencia (Concept Id 6272) = Dead (Concept ID 1366)
   *       obs_datetime <= Report EndDate
   * </ul>
   *
   * </blockquote>
   *
   * @return {@link String}
   */
  public String getPatientsAndDeathDayRegisteredInFichaResumo() {

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("6272", hivMetadata.getStateOfStayPriorArtPatientConcept().getConceptId());
    valuesMap.put("1366", hivMetadata.getPatientHasDiedConcept().getConceptId());

    String query =
        "                SELECT  p.patient_id, (e.encounter_datetime) AS transferout_date "
            + "                FROM patient p "
            + "                         INNER JOIN encounter e "
            + "                                    ON e.patient_id=p.patient_id "
            + "                         INNER JOIN obs o "
            + "                                    ON o.encounter_id=e.encounter_id "
            + "                WHERE e.encounter_type =  ${53} "
            + "                  AND o.obs_datetime <= :endDate "
            + "                  AND o.concept_id =  ${6272} "
            + "                  AND o.value_coded= ${1366} "
            + "                  AND e.location_id = :location "
            + "                  AND p.voided=0 "
            + "                  AND e.voided=0 "
            + "                  AND o.voided=0 "
            + "                GROUP BY p.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    return stringSubstitutor.replace(query);
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>All deaths registered in Ficha Resumo and Ficha Clinica of Master Card by reporting end date
   *
   * <ul>
   *   <li>OR Encounter Type ID= 53 Estado de Permanencia (Concept Id 6272) = Dead (Concept ID 1366)
   *       obs_datetime <= Report EndDate
   * </ul>
   *
   * </blockquote>
   *
   * @return {@link String}
   */
  public String getPatientsAndDeathDayRegisteredInFichaClinica() {

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("6273", hivMetadata.getStateOfStayOfArtPatient().getConceptId());
    valuesMap.put("1366", hivMetadata.getPatientHasDiedConcept().getConceptId());

    String query =
        "                SELECT  p.patient_id , (e.encounter_datetime) AS transferout_date "
            + "                FROM patient p "
            + "                         INNER JOIN encounter e "
            + "                                    ON e.patient_id=p.patient_id "
            + "                         INNER JOIN obs o "
            + "                                    ON o.encounter_id=e.encounter_id "
            + "                WHERE e.encounter_type =  ${6} "
            + "                  AND e.encounter_datetime <= :endDate "
            + "                  AND o.concept_id =  ${6273} "
            + "                  AND o.value_coded= ${1366} "
            + "                  AND e.location_id = :location "
            + "                  AND p.voided= 0 "
            + "                  AND e.voided= 0 "
            + "                  AND o.voided= 0 "
            + "                GROUP BY p.patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    return stringSubstitutor.replace(query);
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>All deaths registered in Patient Program State by reporting end date
   *
   * <ul>
   *   <li>Patient_program.program_id =2 = SERVICO TARV-TRATAMENTO and Patient_State.state = 10
   *       (Died) patient_State.start_date <= Report End Date Patient_state.end_date is null
   * </ul>
   *
   * </blockquote>
   *
   * @return {@link String}
   */
  public String getPatientsAndDeathsDayRegisteredInLastHomeVisitCard() {

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("21", hivMetadata.getBuscaActivaEncounterType().getEncounterTypeId());
    valuesMap.put(
        "36", hivMetadata.getVisitaApoioReintegracaoParteAEncounterType().getEncounterTypeId());
    valuesMap.put(
        "37", hivMetadata.getVisitaApoioReintegracaoParteBEncounterType().getEncounterTypeId());
    valuesMap.put("2031", hivMetadata.getReasonPatientNotFound().getConceptId());
    valuesMap.put(
        "23944", hivMetadata.getReasonPatientNotFoundByActivist2ndVisitConcept().getConceptId());
    valuesMap.put(
        "23945", hivMetadata.getReasonPatientNotFoundByActivist3rdVisitConcept().getConceptId());
    valuesMap.put("1366", hivMetadata.getPatientHasDiedConcept().getConceptId());

    String query =
        " SELECT  max_date.patient_id, (max_date.last) AS transferout_date FROM "
            + "            (SELECT "
            + "                 p.patient_id, "
            + "                 MAX(e.encounter_datetime) last "
            + "             FROM patient p "
            + "                      INNER  JOIN encounter e ON e.patient_id=p.patient_id "
            + "             WHERE "
            + "                     e.encounter_datetime <= :endDate "
            + "               AND e.location_id = :location "
            + "               AND e.encounter_type  in(  ${21} , ${36} , ${37} ) "
            + "               AND e.voided=0 "
            + "               AND p.voided = 0 "
            + "             GROUP BY  p.patient_id  ) max_date "
            + "                INNER  JOIN encounter ee "
            + "                            ON ee.patient_id = max_date.patient_id "
            + "                INNER  JOIN obs o ON ee.encounter_id = o.encounter_id "
            + "                WHERE "
            + "                    ( (o.concept_id =  ${2031}  AND o.value_coded =  ${1366} ) OR "
            + "                            (o.concept_id =  ${23944}  AND o.value_coded =  ${1366} ) OR "
            + "                            (o.concept_id =  ${23945}  AND o.value_coded =  ${1366}  ) ) "
            + "                  AND o.voided=0 "
            + "                  AND ee.voided = 0 "
            + "                GROUP BY  max_date.patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    return stringSubstitutor.replace(query);
  }

  private String getPatientsWithMostRecentDataDeLevantamentoPlus30Days(boolean selectDatetime) {

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("52", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    valuesMap.put("23866", hivMetadata.getArtDatePickupMasterCard().getConceptId());
    valuesMap.put("23865", hivMetadata.getArtPickupConcept().getConceptId());
    valuesMap.put("1065", hivMetadata.getPatientFoundYesConcept().getConceptId());

    String fromSQL =
        " FROM "
            + "    patient p "
            + "INNER JOIN encounter e ON "
            + "    e.patient_id = p.patient_id "
            + "INNER JOIN obs oyes ON "
            + "    oyes.encounter_id = e.encounter_id "
            + "INNER JOIN obs ovalue ON "
            + "    ovalue.encounter_id = e.encounter_id "
            + "WHERE "
            + "    e.encounter_type = ${52} "
            + "    AND e.location_id = :location "
            + "    AND ovalue.concept_id = ${23866} "
            + "    AND ovalue.value_datetime <= :startDate "
            + "    AND oyes.concept_id = ${23865} "
            + "    AND oyes.value_coded = ${1065} "
            + "    AND p.voided = 0 "
            + "    AND e.voided = 0 "
            + "    AND oyes.voided = 0 "
            + "    AND ovalue.voided = 0 "
            + "GROUP BY "
            + "    patient_id ";
    String query =
        selectDatetime
            ? "SELECT p.patient_id, MAX(DATE_ADD(ovalue.value_datetime, INTERVAL 30 DAY)) recent_datetime "
                .concat(fromSQL)
            : "SELECT p.patient_id ".concat(fromSQL);

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);
    return stringSubstitutor.replace(query);
  }

  private String getPatientsAndLastPickupDateBetweenFilaAndMasterCard() {

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("52", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    valuesMap.put("18", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    valuesMap.put("23866", hivMetadata.getArtDatePickupMasterCard().getConceptId());

    String query =
        "SELECT last_pickup.patient_id, "
            + "       Max(recent_date) pickup_date "
            + "FROM  (SELECT p.patient_id, "
            + "              Max(o.value_datetime)recent_date "
            + "       FROM   patient p "
            + "              INNER JOIN encounter e "
            + "                      ON e.patient_id = p.patient_id "
            + "              INNER JOIN obs o "
            + "                      ON o.encounter_id = e.encounter_id "
            + "       WHERE  e.encounter_type = ${52} "
            + "              AND e.location_id = :location "
            + "              AND o.concept_id = ${23866} "
            + "              AND p.voided = 0 "
            + "              AND e.voided = 0 "
            + "              AND o.voided = 0 "
            + "       GROUP  BY p.patient_id "
            + "       HAVING recent_date <= :endDate "
            + "       UNION "
            + "       SELECT p.patient_id, "
            + "              Max(e.encounter_datetime)recent_date "
            + "       FROM   patient p "
            + "              INNER JOIN encounter e "
            + "                      ON e.patient_id = p.patient_id "
            + "       WHERE  e.encounter_type = ${18} "
            + "              AND e.location_id = :location "
            + "              AND p.voided = 0 "
            + "              AND e.voided = 0 "
            + "       GROUP  BY p.patient_id "
            + "       HAVING recent_date <= :endDate) last_pickup "
            + "GROUP  BY last_pickup.patient_id  ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);
    return stringSubstitutor.replace(query);
  }

  private void addParameters(CohortDefinition cd) {
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
  }

  private void addSqlCohortDefinitionParameters(SqlCohortDefinition sqlCohortDefinition) {
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));
  }
}
