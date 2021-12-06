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

  @Autowired
  public FaltososLevantamentoARVCohortQueries(HivMetadata hivMetadata) {
    this.hivMetadata = hivMetadata;
  }

  public CohortDefinition getDenominator() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("DENOMINATOR ");
    addParameters(cd);

    CohortDefinition chdScheduledPickup = getPatientsWithLastNextScheduledPickup();
    CohortDefinition chdB = getB();

    cd.addSearch(
        "scheduledPickup",
        EptsReportUtils.map(
            chdScheduledPickup, "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "B",
        EptsReportUtils.map(
            chdB, "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("scheduledPickup AND NOT B");

    return cd;
  }

  public CohortDefinition getNumerator() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Numerator - Select all patients from the A (Denominator) and filter");
    addParameters(cd);

    CohortDefinition chdDenominator = getDenominator();
    CohortDefinition chdMoreThan7Days =
        getPatientsWithMoreThan7DaysBetweenPickupDateAndLastNextScheduled();
    CohortDefinition chWithoutPickup = getPatientsWithoutAnyDrugPickupAfterLastPlus7Days();

    cd.addSearch(
        "denominator",
        EptsReportUtils.map(
            chdDenominator, "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "moreThan7Days",
        EptsReportUtils.map(
            chdMoreThan7Days, "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "withoutPickup",
        EptsReportUtils.map(
            chWithoutPickup, "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("denominator AND (moreThan7Days OR withoutPickup)");

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
    CohortDefinition chdWhoDied = getPatientsWhoDiedOrSuspendedTratmentE2AndE3();

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

  public CohortDefinition getPregnantsOnDenominator() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Pregant - All Female Patients Marked as Pregnant ");
    addParameters(cd);

    CohortDefinition chPregnant = getPatientsMarkedAsPregnant();
    CohortDefinition chAandNotB = getDenominator();

    cd.addSearch("D", EptsReportUtils.map(chPregnant, "location=${location}"));

    cd.addSearch(
        "AandNotB",
        EptsReportUtils.map(
            chAandNotB, "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("AandNotB AND D");
    return cd;
  }

  public CohortDefinition getBreatfeedingOnDenominator() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Breastfeeding - Mulheres Lactantes  ");
    addParameters(cd);

    CohortDefinition chBreastfeeding = getPatientsMarkedAsBreastfeeding();
    CohortDefinition chAandNotB = getDenominator();

    cd.addSearch("E", EptsReportUtils.map(chBreastfeeding, "location=${location}"));

    cd.addSearch(
        "AandNotB",
        EptsReportUtils.map(
            chAandNotB, "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("AandNotB AND E");
    return cd;
  }

  public CohortDefinition getViralLoadOnDenominator() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("APSS Consultation  ");
    addParameters(cd);

    CohortDefinition chVLResult = getPatientsWithMostRecentVLResult();
    CohortDefinition chAandNotB = getDenominator();

    cd.addSearch("F", EptsReportUtils.map(chVLResult, "location=${location}"));

    cd.addSearch(
        "AandNotB",
        EptsReportUtils.map(
            chAandNotB, "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("AandNotB AND F");
    return cd;
  }

  public CohortDefinition getAPSSConsultationOnDenominator() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("APSS Consultation  ");
    addParameters(cd);

    CohortDefinition chAPSS = getPatientsWithLeastOneAPSSConsultation();
    CohortDefinition chAandNotB = getDenominator();

    cd.addSearch("G", EptsReportUtils.map(chAPSS, "location=${location}"));

    cd.addSearch(
        "AandNotB",
        EptsReportUtils.map(
            chAandNotB, "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("AandNotB AND G");
    return cd;
  }

  public CohortDefinition getPregnantsOnNumerator() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Pregant - All Female Patients Marked as Pregnant on numerator ");
    addParameters(cd);

    CohortDefinition chPregnant = getPatientsMarkedAsPregnant();
    CohortDefinition chNumerator = getNumerator();

    cd.addSearch("D", EptsReportUtils.map(chPregnant, "location=${location}"));

    cd.addSearch(
        "C",
        EptsReportUtils.map(
            chNumerator, "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("C AND D");
    return cd;
  }

  public CohortDefinition getBreatfeedingOnNumerator() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Breastfeeding - Mulheres Lactantes on Numerator ");
    addParameters(cd);

    CohortDefinition chBreastfeeding = getPatientsMarkedAsBreastfeeding();
    CohortDefinition chNumetator = getNumerator();

    cd.addSearch("E", EptsReportUtils.map(chBreastfeeding, "location=${location}"));

    cd.addSearch(
        "C",
        EptsReportUtils.map(
            chNumetator, "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("C AND E");
    return cd;
  }

  public CohortDefinition getViralLoadOnNumerator() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Viral Load Result and Numerator ");
    addParameters(cd);

    CohortDefinition chVLResult = getPatientsWithMostRecentVLResult();
    CohortDefinition chNumerator = getNumerator();

    cd.addSearch("F", EptsReportUtils.map(chVLResult, "location=${location}"));

    cd.addSearch(
        "C",
        EptsReportUtils.map(
            chNumerator, "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("C AND F");
    return cd;
  }

  public CohortDefinition getAPSSConsultationOnNumerator() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("APSS Consultation  and Numerator");
    addParameters(cd);

    CohortDefinition chAPSS = getPatientsWithLeastOneAPSSConsultation();
    CohortDefinition chNumetator = getNumerator();

    cd.addSearch("G", EptsReportUtils.map(chAPSS, "location=${location}"));

    cd.addSearch(
        "C",
        EptsReportUtils.map(
            chNumetator, "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString("C AND G");
    return cd;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Select all patients with the most recent date from the following sources as: <br>
   *
   * <ul>
   *   <li>The “Data do próximo levantamento” (concept id 5096, value_datetime) from the most recent
   *       FILA (encounter type 18) by report start date(last encounter_datetime < startDate)
   *   <li>The most recent “Data de Levantamento” (concept_id 23866, value_datetime < startDate) +
   *       30 days, from “Recepcao Levantou ARV” (encounter type 52) with concept “Levantou ARV”
   *       (concept_id 23865) set to “SIM” (Concept id 1065) by report start date (value_datetime <
   *       startDate)
   *   <li>and this date “Last Next Scheduled Pick Up” is between startdate and endDate
   * </ul>
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWithLastNextScheduledPickup() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Demoninator - Last Next Scheduled Pick Up");
    addSqlCohortDefinitionParameters(sqlCohortDefinition);

    String query = getPatientsWithLastNextScheduledPickup(false);

    sqlCohortDefinition.setQuery(query);
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
    sqlCohortDefinition.setName("Exlusions  - E1.1 Trasfered out state in program");
    addSqlCohortDefinitionParameters(sqlCohortDefinition);

    String queryPatientsTransferedOut = getPatientsTransferredOutRegisteredInProgramState(false);

    sqlCohortDefinition.setQuery(queryPatientsTransferedOut);
    return sqlCohortDefinition;
  }

  private String getPatientsTransferredOutRegisteredInProgramState(boolean selectDatetime) {

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("2", hivMetadata.getARTProgram().getProgramId());
    valuesMap.put(
        "7",
        hivMetadata
            .getTransferredOutToAnotherHealthFacilityWorkflowState()
            .getProgramWorkflowStateId());

    String fromSql =
        "FROM   (SELECT p.patient_id, Max(ps.start_date) recent_date "
            + "        FROM   patient p "
            + "               INNER JOIN patient_program pg ON pg.patient_id = p.patient_id "
            + "               INNER JOIN patient_state ps ON ps.patient_program_id = pg.patient_program_id "
            + "        WHERE  pg.program_id = ${2} "
            + "               AND pg.location_id = :location "
            + "               AND ps.state = ${7} "
            + "               AND ps.end_date IS NULL "
            + "               AND p.voided = 0 "
            + "               AND pg.voided = 0 "
            + "               AND ps.voided = 0 "
            + "        GROUP  BY p.patient_id "
            + "        HAVING recent_date <= :endDate) transfered_out "
            + "GROUP  BY patient_id ";
    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    String query =
        selectDatetime
            ? "SELECT patient_id, recent_date ".concat(fromSql)
            : "SELECT patient_id ".concat(fromSql);

    return stringSubstitutor.replace(query);
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

    String transferedOut = getPatientsTransferredOutRegisteredInFichaResumoAndMasterCard(false);

    sqlCohortDefinition.setQuery(transferedOut);
    return sqlCohortDefinition;
  }

  public String getPatientsTransferredOutRegisteredInFichaResumoAndMasterCard(
      boolean selectDatetime) {

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("6273", hivMetadata.getStateOfStayOfArtPatient().getConceptId());
    valuesMap.put("1706", hivMetadata.getTransferredOutConcept().getConceptId());
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("6272", hivMetadata.getStateOfStayOfPreArtPatient().getConceptId());

    String fromSql =
        "FROM ("
            + " SELECT transferred_out.patient_id, MAX(transferred_out.recent_datetime) recent_date "
            + "FROM   (SELECT p.patient_id, MAX(e.encounter_datetime) recent_datetime "
            + "        FROM   patient p "
            + "               INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "               INNER JOIN obs o ON o.encounter_id = e.encounter_id "
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
            + "        SELECT p.patient_id, MAX(o.obs_datetime) recent_datetime "
            + "        FROM   patient p "
            + "               INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "               INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "        WHERE  e.encounter_type = ${53} "
            + "               AND o.concept_id = ${6272} "
            + "               AND o.value_coded = ${1706} "
            + "               AND e.location_id = :location "
            + "               AND o.obs_datetime <= :endDate "
            + "               AND p.voided = 0 "
            + "               AND e.voided = 0 "
            + "               AND o.voided = 0 "
            + "        GROUP  BY p.patient_id) transferred_out "
            + "GROUP  BY transferred_out.patient_id  ) transferred "
            + "GROUP BY transferred.patient_id ";
    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);
    String query =
        selectDatetime
            ? "SELECT patient_id, recent_date ".concat(fromSql)
            : "SELECT patient_id ".concat(fromSql);
    return stringSubstitutor.replace(query);
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

    String transferredOutInProgram = getPatientsTransferredOutRegisteredInProgramState(true);
    String transferredOutInResumoAndMasterCard =
        getPatientsTransferredOutRegisteredInFichaResumoAndMasterCard(true);
    String query =
        "SELECT patient_id "
            + "FROM   (SELECT transferout.patient_id,   Max(transferout.recent_date) transferred_date "
            + "        FROM   ( "
            + transferredOutInProgram
            + "                UNION "
            + transferredOutInResumoAndMasterCard
            + "                ) transferout "
            + "        GROUP  BY transferout.patient_id) max_transfer"
            + " WHERE  max_transfer.patient_id NOT IN (SELECT p.patient_id "
            + "                                          FROM   patient p "
            + "                                          JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                          WHERE  p.voided = 0 "
            + "                                            AND e.voided = 0 "
            + "                                            AND e.encounter_type IN (${6},${9},${18})  "
            + "                                            AND e.location_id = :location "
            + "                                            AND e.encounter_datetime > max_transfer.transferred_date "
            + "                                          AND max_transfer.patient_id = p.patient_id "
            + "                                            AND e.encounter_datetime <= :endDate "
            + "                                          UNION "
            + "                                          SELECT p.patient_id "
            + "                                          FROM   patient p "
            + "                                          JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                          JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                          WHERE  p.voided = 0"
            + "                                            AND e.voided = 0 "
            + "                                            AND e.encounter_type =  ${52} "
            + "                                            AND e.location_id = :location "
            + "                                            AND o.concept_id =  ${23866} "
            + "                                            AND o.value_datetime > max_transfer.transferred_date AND max_transfer.patient_id = p.patient_id"
            + "                                            AND o.value_datetime <= :endDate) GROUP BY patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));
    return sqlCohortDefinition;
  }

  /**
   * <b>2.5</b> - Exclude all patients who after the most recent date from 2.1 to 2.4, have a drugs
   * pick up or consultation: Encounter Type ID= 6, 9, 18 and encounter_datetime> the most recent
   * date OR Encounter Type ID = 52 and “Data de Levantamento” (Concept Id 23866 value_datetime) >
   * the most recent date. *
   *
   * <p><b>3.3</b> - 3.3 - Except all patients who after the most recent date from 3.1 to 3.2, have
   * a drugs pick up or consultation: Encounter Type ID= 6, 9, 18 and encounter_datetime> the most
   * recent date and <= Report EndDate OR Encounter Type ID = 52 and “Data de Levantamento” (Concept
   * Id 23866 value_datetime) > the most recent date and <= Report EndDate
   *
   * @return
   */
  public CohortDefinition getPatientsWhoDiedOrSuspendedTratmentE2AndE3() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("E2 and E3 - Patients who died or stopped/suspended treatment ");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "EndDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();

    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    map.put("18", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    map.put("52", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    map.put("23866", hivMetadata.getArtDatePickupMasterCard().getConceptId());

    String deathDayInProgramState =
        getPatientsDeathDayOrTreatmentSuspensionRegisteredInProgramState();
    String deathsRegisteredInDemographics = getPatientsDeathsRegisteredInDemographics();
    String deathRegisteredInFichaClinica =
        getPatientsAndDeathDayOrTreatmentSuspensionRegisteredInFichaClinica();
    String deathRegisteredInFichaResumo =
        getPatientsAndDeathDayOrTreatmentSuspensionRegisteredInFichaResumo();
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
            + "                                          JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                          WHERE  p.voided = 0 "
            + "                                            AND e.voided = 0 "
            + "                                            AND e.encounter_type IN (${6},${9},${18})  "
            + "                                            AND e.location_id = :location "
            + "                                            AND e.encounter_datetime > max_transferout.transferout_date "
            + "                                          AND max_transferout.patient_id = p.patient_id "
            + "                                            AND e.encounter_datetime <= :endDate "
            + "                                          UNION "
            + "                                          SELECT p.patient_id "
            + "                                          FROM   patient p "
            + "                                          JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                          JOIN obs o ON e.encounter_id = o.encounter_id "
            + "                                          WHERE  p.voided = 0"
            + "                                            AND e.voided = 0 "
            + "                                            AND e.encounter_type =  ${52} "
            + "                                            AND e.location_id = :location "
            + "                                            AND o.concept_id =  ${23866} "
            + "                                            AND o.value_datetime > max_transferout.transferout_date AND max_transferout.patient_id = p.patient_id"
            + "                                            AND o.value_datetime <= :endDate) GROUP BY patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

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

    String scheduledPickup = getPatientsWithLastNextScheduledPickup(true);

    String lastPickupBetweenFilaAndMasterCard =
        getPatientsAndLastPickupDateBetweenFilaAndMasterCard();
    String query =
        " SELECT more_days.patient_id FROM( "
            + " "
            + "                SELECT schedule.patient_id,    MAX(recent_datetime) scheduled_date "
            + "                FROM( "
            + scheduledPickup
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
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>All patients without any drugs pickup after the last pick up and reporting end date + 7 days
   * as follows
   *
   * <ul>
   *   <li>No drug pickup on FILA (encounter type 18, encounter datetime) and no drug pickup on
   *       Mastercard Levantou ARV (encounter type 52, concept_id 23866, value_datetime) between the
   *       “Last Pick Up Date” and endDate + 7 days (count of drugs pickup should be zero)
   *   <li>TThe “Last Pick Up Date” is the most recent date between the following:
   *   <li>The last pick up on FILA by reporting start date(encounter type 18, last encounter
   *       datetime < startDate) and
   *   <li>The last pick up on Mastercard Levantou ARV by reporting start date (encounter type 52,
   *       concept_id 23866, last value_datetime < startDate)
   * </ul>
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWithoutAnyDrugPickupAfterLastPlus7Days() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "Numerator - All patients without any drugs pickup after the last pick up ");
    addSqlCohortDefinitionParameters(sqlCohortDefinition);

    Map<String, Integer> map = new HashMap<>();
    map.put("18", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    map.put("52", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    map.put("23866", hivMetadata.getArtDatePickupMasterCard().getConceptId());

    String query =
        "SELECT pickup_after.patient_id "
            + "FROM   (SELECT last_pickup.patient_id, MAX(last_pickup.last_date) AS pickup_date "
            + "        FROM  (SELECT p.patient_id, Max(e.encounter_datetime) last_date "
            + "               FROM   patient p INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "               WHERE  e.encounter_type = ${18} "
            + "                      AND e.encounter_datetime < :startDate "
            + "                      AND e.location_id = :location "
            + "                      AND e.voided = 0 "
            + "                      AND p.voided = 0 "
            + "               GROUP  BY p.patient_id "
            + "               UNION "
            + "               SELECT p.patient_id, MAX(o.value_datetime) pickup_date "
            + "               FROM   patient p "
            + "                      INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                      INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "               WHERE  e.encounter_type = ${52} "
            + "                      AND e.location_id = :location "
            + "                      AND e.voided = 0 "
            + "                      AND o.concept_id = ${23866} "
            + "                      AND o.value_datetime < :startDate "
            + "                      AND o.voided = 0 "
            + "                      AND p.voided = 0 "
            + "               GROUP  BY p.patient_id) last_pickup "
            + "        GROUP  BY last_pickup.patient_id) pickup_after "
            + "WHERE  pickup_after.patient_id NOT IN(SELECT p.patient_id "
            + "                                      FROM   patient p "
            + "                                             INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                      WHERE  e.encounter_type = ${18} "
            + "                                             AND e.encounter_datetime BETWEEN pickup_after.pickup_date AND DATE_ADD(:endDate, INTERVAL 7 DAY ) "
            + "                                             AND e.location_id = :location "
            + "                                             AND e.voided = 0 "
            + "                                             AND pickup_after.patient_id = p.patient_id "
            + "                                             AND p.voided = 0 "
            + "                                      GROUP  BY p.patient_id "
            + "                                      UNION "
            + "                                      SELECT p.patient_id "
            + "                                      FROM   patient p "
            + "                                             INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                             INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                                      WHERE  e.encounter_type = ${52} "
            + "                                             AND e.location_id = :location "
            + "                                             AND e.voided = 0 "
            + "                                             AND o.concept_id = ${23866} "
            + "                                             AND o.value_datetime BETWEEN pickup_after.pickup_date AND DATE_ADD(:endDate, INTERVAL 7 DAY) "
            + "                                             AND o.voided = 0 "
            + "                                             AND pickup_after.patient_id = p.patient_id "
            + "                                             AND p.voided = 0 "
            + "                                      GROUP  BY p.patient_id)";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));
    return sqlCohortDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Mulheres Grávidas
   *
   * <ul>
   *   <li>Select all female patients (sex=female) and filter those with the most recent record of
   *       pregnant (concept id 1982) value coded Yes (concept id 1065) on Ficha Clinica (encounter
   *       type 6) and verify if it falls within the last 9 months (the max (encounter_datetime) is
   *       >= report generation date minus 9 months and <=report generation date).
   *   <li>Note: a) If the patient has both states (pregnant and breastfeeding) the most recent one
   *       should be considered.
   *   <li>b) For patients who have both state (pregnant and breastfeeding) marked on the same day,
   *       the system will consider the patient as pregnant.
   * </ul>
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsMarkedAsPregnant() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Pregnant - Female Patients Marked as Pregnant");
    addSqlCohortDefinitionParameters(sqlCohortDefinition);

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("6332", hivMetadata.getBreastfeeding().getConceptId());
    map.put("1982", hivMetadata.getPregnantConcept().getConceptId());
    map.put("1065", hivMetadata.getPatientFoundYesConcept().getConceptId());

    String query =
        "SELECT pregnant.patient_id "
            + "FROM   (SELECT p.patient_id, MAX(e.encounter_datetime) AS pregnancy_date "
            + "        FROM   patient p "
            + "               INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "               INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "               INNER JOIN person ps ON ps.person_id = p.patient_id "
            + "        WHERE  e.encounter_type = ${6} "
            + "               AND e.encounter_datetime BETWEEN DATE_ADD(CURDATE(), INTERVAL -9 month) AND CURDATE() "
            + "               AND e.location_id = :location "
            + "               AND e.voided = 0 "
            + "               AND o.concept_id = ${1982} "
            + "               AND o.value_coded = ${1065} "
            + "               AND o.voided = 0 "
            + "               AND ps.gender = 'F' "
            + "               AND ps.voided = 0 "
            + "        GROUP  BY p.patient_id) pregnant "
            + "       LEFT JOIN (SELECT p.patient_id, MAX(e.encounter_datetime) breastfeed_date "
            + "                  FROM   patient p "
            + "                         INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                         INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                         INNER JOIN person ps ON ps.person_id = p.patient_id "
            + "                  WHERE  e.encounter_type = ${6} "
            + "                         AND e.encounter_datetime BETWEEN DATE_ADD(CURDATE(), INTERVAL -18 MONTH) AND CURDATE() "
            + "                         AND e.location_id = :location "
            + "                         AND e.voided = 0 "
            + "                         AND o.concept_id = ${6332} "
            + "                         AND o.value_coded = ${1065} "
            + "                         AND o.voided = 0 "
            + "                         AND ps.gender = 'F' "
            + "                         AND ps.voided = 0 "
            + "                  GROUP  BY p.patient_id) AS breastfeeding "
            + "              ON breastfeeding.patient_id = pregnant.patient_id "
            + "WHERE  pregnant.pregnancy_date >= breastfeeding.breastfeed_date OR breastfeeding.breastfeed_date IS NULL "
            + "GROUP  BY pregnant.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));
    return sqlCohortDefinition;
  }
  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Mulheres Lactantes
   *
   * <ul>
   *   <li>Select all female patients (sex=female) and filter those with the most recent record of
   *       breastfeeding (concept id 6332) value coded Yes (concept id 1065) on Ficha Clinica
   *       (encounter type 6) and verify if it falls within the the last 18 months (the max
   *       (encounter_datetime) is >= report generation date minus 18 months and <=report generation
   *       date)
   *   <li>Note: a) If the patient has both states (pregnant and breastfeeding) the most recent one
   *       should be considered.
   *   <li>b) For patients who have both state (pregnant and breastfeeding) marked on the same day,
   *       the system will consider the patient as pregnant.
   * </ul>
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsMarkedAsBreastfeeding() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Pregnant - Female Patients Marked as Pregnant");
    addSqlCohortDefinitionParameters(sqlCohortDefinition);

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("6332", hivMetadata.getBreastfeeding().getConceptId());
    map.put("1982", hivMetadata.getPregnantConcept().getConceptId());
    map.put("1065", hivMetadata.getPatientFoundYesConcept().getConceptId());

    String query =
        "SELECT breastfeeding.patient_id "
            + "FROM  (SELECT p.patient_id, MAX(e.encounter_datetime) breastfeed_date "
            + "       FROM   patient p INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "              INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "              INNER JOIN person ps ON ps.person_id = p.patient_id "
            + "       WHERE  e.encounter_type = ${6} "
            + "              AND e.encounter_datetime BETWEEN DATE_ADD(CURDATE(), INTERVAL -18 MONTH) AND CURDATE() "
            + "              AND e.location_id = :location "
            + "              AND e.voided = 0 "
            + "              AND o.concept_id = ${6332} "
            + "              AND o.value_coded = ${1065} "
            + "              AND o.voided = 0 "
            + "              AND ps.gender = 'F' "
            + "              AND ps.voided = 0 "
            + "       GROUP  BY p.patient_id) breastfeeding "
            + "      LEFT JOIN (SELECT p.patient_id, MAX(e.encounter_datetime) pregnancy_date "
            + "                 FROM   patient p "
            + "                        INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                        INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                        INNER JOIN person ps ON ps.person_id = p.patient_id "
            + "                 WHERE  e.encounter_type = ${6} "
            + "                        AND e.encounter_datetime BETWEEN DATE_ADD(CURDATE(), INTERVAL -9 month) AND CURDATE() "
            + "                        AND e.location_id = :location "
            + "                        AND e.voided = 0 "
            + "                        AND o.concept_id = ${1982} "
            + "                        AND o.value_coded = ${1065} "
            + "                        AND o.voided = 0 "
            + "                        AND ps.gender = 'F' "
            + "                        AND ps.voided = 0 "
            + "                 GROUP  BY p.patient_id) pregnant "
            + "             ON pregnant.patient_id = breastfeeding.patient_id "
            + "WHERE  breastfeeding.breastfeed_date > pregnant.pregnancy_date "
            + "        OR pregnant.pregnancy_date IS NULL "
            + "GROUP  BY breastfeeding.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));
    return sqlCohortDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Consulta de APSS/PP nos últimos 3 Meses
   *
   * <ul>
   *   <li>Select all patients with at least one APSS/PP consultation (encounter type 35) between
   *       the report generation date minus 3 months and report generation date
   * </ul>
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWithLeastOneAPSSConsultation() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Pregnant - Female Patients Marked as Pregnant");
    addSqlCohortDefinitionParameters(sqlCohortDefinition);

    Map<String, Integer> map = new HashMap<>();
    map.put("35", hivMetadata.getPrevencaoPositivaSeguimentoEncounterType().getEncounterTypeId());

    String query =
        "SELECT p.patient_id "
            + "FROM   patient p "
            + "       INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "WHERE  e.encounter_type = 35 "
            + "       AND e.encounter_datetime BETWEEN Date_add(Curdate(), INTERVAL -3 month) AND  Curdate() "
            + "       AND e.location_id = :location "
            + "AND e.voided = 0  "
            + "       AND p.voided = 0 "
            + "GROUP  BY p.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));
    return sqlCohortDefinition;
  }
  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Viral Load
   *
   * <ul>
   *   <li>Select all patients with the most recent VL Result (concept Id 856 or concept id 1305)
   *       documented in the Laboratory Form (encounter type 13, encounter_datetime) or Ficha
   *       Clinica (encounter type 6, encounter_datetime) or Ficha Resumo (encounter type 53,
   *       obs_datetime ) or FSR form (encounter type 51, encounter_datetime) between the report
   *       generation date minus 12 months and report generation date and the Result is >= 1000
   *       copias/ml (concept 856 value_numeric >= 1000)
   * </ul>
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWithMostRecentVLResult() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("Viral Load - Patients with Most Recent VL Result");
    addSqlCohortDefinitionParameters(sqlCohortDefinition);

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("13", hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId());
    valuesMap.put("51", hivMetadata.getFsrEncounterType().getEncounterTypeId());
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    valuesMap.put("1305", hivMetadata.getHivViralLoadQualitative().getConceptId());

    String query =
        "SELECT viral_result.patient_id "
            + "FROM   (SELECT vl_registered.patient_id, vl_registered.concept_id, vl_registered.value_numeric "
            + "        FROM   (SELECT p.patient_id, e.encounter_id, e.encounter_type, encounter_datetime, o.concept_id, o.value_numeric, o.obs_datetime "
            + "                FROM   patient p INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                       INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                       INNER JOIN (SELECT most_recent.patient_id, MAX(most_recent.recent_datetime) vl_datetime "
            + "                                   FROM   (SELECT p.patient_id, MAX(e.encounter_datetime) recent_datetime "
            + "                                           FROM   patient p "
            + "                                                  INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                                  INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                                           WHERE  e.encounter_type IN ( ${6}, ${13}, ${51} ) "
            + "                                                  AND e.encounter_datetime BETWEEN DATE_ADD(CURDATE(),INTERVAL - 12 MONTH ) AND CURDATE() "
            + "                                                  AND e.location_id = :location "
            + "                                                  AND e.voided = 0 "
            + "                                                  AND o.concept_id IN ( ${856}, ${1305} ) "
            + "                                                  AND o.voided = 0 "
            + "                                                  AND p.voided = 0 "
            + "                                           GROUP  BY p.patient_id "
            + "                                           UNION "
            + "                                           SELECT p.patient_id, MAX(o.obs_datetime) recent_datetime "
            + "                                           FROM   patient p "
            + "                                                  INNER JOIN encounter e ON e.patient_id = "
            + "                                                             p.patient_id INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                                           WHERE  e.encounter_type = ${53} "
            + "                                                  AND e.location_id = :location "
            + "                                                  AND e.voided = 0 "
            + "                                                  AND o.concept_id IN ( ${856}, ${1305} ) "
            + "                                                  AND o.obs_datetime BETWEEN DATE_ADD(CURDATE(), INTERVAL - 12 MONTH) AND CURDATE() "
            + "                                                  AND o.voided = 0 "
            + "                                                  AND p.voided = 0 "
            + "                                           GROUP  BY p.patient_id) most_recent "
            + "                                   GROUP  BY most_recent.patient_id) recent_vl ON recent_vl.patient_id = p.patient_id "
            + "                WHERE  o.concept_id IN ( ${856}, ${1305} ) "
            + "                       AND ( ( e.encounter_type IN( ${6}, ${13}, ${51} ) AND e.encounter_datetime = recent_vl.vl_datetime ) "
            + "                              OR ( e.encounter_type = ${53} AND o.obs_datetime = recent_vl.vl_datetime ) ) "
            + "                GROUP  BY p.patient_id, e.encounter_id) vl_registered "
            + "        WHERE  EXISTS (SELECT encounter_53.encounter_id "
            + "                       FROM   encounter encounter_53 "
            + "                       WHERE  encounter_53.encounter_id = vl_registered.encounter_id "
            + "                              AND encounter_53.patient_id = vl_registered.patient_id "
            + "                              AND encounter_53 .encounter_type = ${53} "
            + "                              AND NOT EXISTS (SELECT encounter_6_51_13.encounter_id "
            + "                                              FROM   encounter encounter_6_51_13 "
            + "                                                     INNER JOIN obs o ON o.encounter_id = encounter_6_51_13.encounter_id "
            + "       WHERE  encounter_6_51_13.encounter_type IN ( ${6}, ${51}, ${13} ) "
            + "       AND o.concept_id IN ( ${856}, ${1305} ) "
            + "       AND encounter_6_51_13.encounter_datetime = vl_registered.obs_datetime "
            + "       AND encounter_6_51_13.patient_id = vl_registered.patient_id)) "
            + "       OR EXISTS (SELECT encounter_6.encounter_id "
            + "       FROM   encounter encounter_6 "
            + "       WHERE  encounter_6 .encounter_id = vl_registered.encounter_id "
            + "       AND encounter_6.patient_id = vl_registered.patient_id "
            + "       AND encounter_6.encounter_type = ${6} "
            + "       AND NOT EXISTS (SELECT encounter_51_13.encounter_id "
            + "       FROM   encounter encounter_51_13 "
            + "       INNER JOIN obs o ON o.encounter_id = encounter_51_13.encounter_id "
            + "       WHERE  encounter_51_13.encounter_type IN ( ${51}, ${13}) "
            + "       AND o.concept_id IN ( ${856}, ${1305} ) "
            + "       AND encounter_51_13.encounter_datetime = vl_registered.encounter_datetime "
            + "       AND encounter_51_13.patient_id = vl_registered.patient_id )) "
            + "       OR EXISTS (SELECT encounter_51.encounter_id "
            + "       FROM   encounter encounter_51 "
            + "       WHERE  encounter_51.encounter_id = vl_registered.encounter_id "
            + "       AND encounter_51.patient_id = vl_registered.patient_id "
            + "       AND encounter_51.encounter_type = ${51} "
            + "       AND NOT EXISTS (SELECT encounter_13.encounter_id "
            + "       FROM   encounter encounter_13 "
            + "       INNER JOIN obs o ON o.encounter_id = encounter_13.encounter_id "
            + "       WHERE  encounter_13.encounter_type = ${13} "
            + "       AND o.concept_id IN ( ${856}, ${1305} ) "
            + "       AND encounter_13.encounter_datetime = vl_registered.encounter_datetime "
            + "       AND encounter_13.patient_id = vl_registered.patient_id)) "
            + "       OR EXISTS (SELECT encounter_13.encounter_id "
            + "       FROM   encounter encounter_13 "
            + "       WHERE  encounter_13.encounter_id = vl_registered.encounter_id "
            + "       AND encounter_13.patient_id = vl_registered.patient_id "
            + "       AND encounter_13.encounter_type = ${13})) viral_result "
            + "WHERE  viral_result.concept_id = ${856} "
            + "       AND viral_result.value_numeric >= 1000 "
            + "GROUP  BY viral_result.patient_id";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);
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
   *   <li>2.1 Patient_program.program_id =2 = SERVICO TARV-TRATAMENTO and Patient_State.state = 10
   *       (Died) patient_State.start_date <= Report End Date Patient_state.end_date is null
   *   <li>3.1 - All suspended registered in Patient Program State by reporting end date
   * </ul>
   *
   * </blockquote>
   *
   * @return {@link String}
   */
  public String getPatientsDeathDayOrTreatmentSuspensionRegisteredInProgramState() {

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("2", hivMetadata.getARTProgram().getProgramId());
    valuesMap.put("10", hivMetadata.getArtDeadWorkflowState().getProgramWorkflowStateId());
    valuesMap.put(
        "8", hivMetadata.getSuspendedTreatmentWorkflowState().getProgramWorkflowStateId());

    String query =
        "   SELECT pg.patient_id, MAX(ps.start_date) AS transferout_date "
            + "FROM patient p "
            + "   INNER JOIN patient_program pg ON p.patient_id=pg.patient_id "
            + "   INNER JOIN patient_state ps ON pg.patient_program_id=ps.patient_program_id "
            + "   WHERE pg.voided=0 AND ps.voided=0 AND p.voided=0 "
            + "   AND pg.program_id=${2} AND ps.state IN (${10}, ${8})"
            + " AND ps.end_date is null "
            + "   AND ps.start_date <= :endDate "
            + "AND location_id=:location "
            + "GROUP BY pg.patient_id";

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
        "        SELECT p.person_id, ps.start_date AS transferout_date   "
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
   *   <li>2.4 OR Encounter Type ID= 53 Estado de Permanencia (Concept Id 6272) = Dead (Concept ID
   *       1366) obs_datetime <= Report EndDate
   *   <li>3.2 - All suspensions registered in Ficha Resumo and Ficha Clinica of Master Card by
   *       reporting end date
   * </ul>
   *
   * </blockquote>
   *
   * @return {@link String}
   */
  public String getPatientsAndDeathDayOrTreatmentSuspensionRegisteredInFichaResumo() {

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("6272", hivMetadata.getStateOfStayPriorArtPatientConcept().getConceptId());
    valuesMap.put("1366", hivMetadata.getPatientHasDiedConcept().getConceptId());
    valuesMap.put("1709", hivMetadata.getSuspendedTreatmentConcept().getConceptId());

    String query =
        "                SELECT  p.patient_id, MAX(e.encounter_datetime) AS transferout_date "
            + "                FROM patient p "
            + "                         INNER JOIN encounter e "
            + "                                    ON e.patient_id=p.patient_id "
            + "                         INNER JOIN obs o "
            + "                                    ON o.encounter_id=e.encounter_id "
            + "                WHERE e.encounter_type =  ${53} "
            + "                  AND o.obs_datetime <= :endDate "
            + "                  AND o.concept_id =  ${6272} "
            + "                  AND o.value_coded IN (${1366}, ${1709}) "
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
   *   <li>OR Encounter Type ID= 6 Estado de Permanencia (Concept Id 6273) = Dead (Concept ID 1366)
   *       encounter_datetime <= Report EndDate
   *   <li>3.2 Encounter Type ID= 6 Estado de Permanencia (Concept Id 6273) = Suspended (Concept ID
   *       1709) Encounter_datetime <= Report EndDate
   * </ul>
   *
   * </blockquote>
   *
   * @return {@link String}
   */
  public String getPatientsAndDeathDayOrTreatmentSuspensionRegisteredInFichaClinica() {

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("6273", hivMetadata.getStateOfStayOfArtPatient().getConceptId());
    valuesMap.put("1366", hivMetadata.getPatientHasDiedConcept().getConceptId());
    valuesMap.put("1709", hivMetadata.getSuspendedTreatmentConcept().getConceptId());

    String query =
        "                SELECT  p.patient_id , MAX(e.encounter_datetime) AS transferout_date "
            + "                FROM patient p "
            + "                         INNER JOIN encounter e "
            + "                                    ON e.patient_id=p.patient_id "
            + "                         INNER JOIN obs o "
            + "                                    ON o.encounter_id=e.encounter_id "
            + "                WHERE e.encounter_type =  ${6} "
            + "                  AND e.encounter_datetime <= :endDate "
            + "                  AND o.concept_id =  ${6273} "
            + "                  AND o.value_coded IN (${1366}, ${1709}) "
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

  private String getPatientsWithLastNextScheduledPickup(boolean selectDatetime) {

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("18", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    valuesMap.put("5096", hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId());
    valuesMap.put("52", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    valuesMap.put("23866", hivMetadata.getArtDatePickupMasterCard().getConceptId());
    valuesMap.put("23865", hivMetadata.getArtPickupConcept().getConceptId());
    valuesMap.put("1065", hivMetadata.getPatientFoundYesConcept().getConceptId());
    String fromSQL =
        "FROM   (SELECT p.patient_id, MAX(o.value_datetime) recent_datetime "
            + "        FROM   patient p "
            + "               INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "               INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "               INNER JOIN (SELECT p.patient_id, Max(e.encounter_datetime) encounter_date "
            + "                           FROM   patient p "
            + "                                  INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                           WHERE  e.encounter_type = ${18} AND e.encounter_datetime < :startDate "
            + "                                  AND e.location_id = :location "
            + "                                  AND e.voided = 0 "
            + "                                  AND p.voided = 0 "
            + "                           GROUP  BY p.patient_id) most_recent ON most_recent.patient_id = p.patient_id "
            + "        WHERE  most_recent.encounter_date = e.encounter_datetime "
            + "               AND e.encounter_type = ${18} "
            + "               AND e.encounter_datetime < :startDate "
            + "               AND e.location_id = :location "
            + "               AND e.voided = 0 "
            + "               AND p.voided = 0 "
            + "               AND o.voided = 0 "
            + "               AND o.concept_id = ${5096} "
            + "        GROUP  BY p.patient_id "
            + "        UNION "
            + "        SELECT p.patient_id, Max(Date_add(ovalue.value_datetime, INTERVAL 30 DAY)) recent_datetime "
            + "        FROM   patient p "
            + "               INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "               INNER JOIN obs oyes ON oyes.encounter_id = e.encounter_id "
            + "               INNER JOIN obs ovalue ON ovalue.encounter_id = e.encounter_id "
            + "        WHERE  e.encounter_type = ${52} "
            + "               AND e.location_id = :location "
            + "               AND ovalue.concept_id = ${23866} "
            + "               AND ovalue.value_datetime < :startDate "
            + "               AND oyes.concept_id = ${23865} "
            + "               AND oyes.value_coded = ${1065} "
            + "               AND p.voided = 0 "
            + "               AND e.voided = 0 "
            + "               AND oyes.voided = 0 "
            + "               AND ovalue.voided = 0 "
            + "        GROUP  BY patient_id) recent_pickup "
            + "WHERE  recent_pickup.recent_datetime BETWEEN :startDate AND :endDate "
            + "GROUP  BY patient_id";

    String query =
        selectDatetime
            ? "SELECT patient_id, recent_datetime ".concat(fromSQL)
            : "SELECT patient_id ".concat(fromSQL);
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
