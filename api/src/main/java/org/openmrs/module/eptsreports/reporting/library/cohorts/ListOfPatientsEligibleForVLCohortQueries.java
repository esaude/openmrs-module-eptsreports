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
public class ListOfPatientsEligibleForVLCohortQueries {

  private TxCurrCohortQueries txCurrCohortQueries;
  private HivMetadata hivMetadata;
  private QualityImprovement2020CohortQueries qualityImprovement2020CohortQueries;
  private TxNewCohortQueries txNewCohortQueries;

  @Autowired
  public ListOfPatientsEligibleForVLCohortQueries(
      TxCurrCohortQueries txCurrCohortQueries,
      HivMetadata hivMetadata,
      TxNewCohortQueries txNewCohortQueries) {

    this.txCurrCohortQueries = txCurrCohortQueries;
    this.hivMetadata = hivMetadata;
    this.txNewCohortQueries = txNewCohortQueries;
  }

  public CohortDefinition getBaseCohort() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("List of patients eligible for VL cohort");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    CohortDefinition txcurr = this.txCurrCohortQueries.getTxCurrCompositionCohort("txcurr", true);
    CohortDefinition chdX1 = getLastNextScheduledConsultationDate();
    CohortDefinition chdX2 = getLastNextScheduledPickUpDate();
    CohortDefinition chdX3 = getLastNextScheduledPickUpDateWithMostRecentDataLevantamento();
    CohortDefinition chdVL1 = getPatientsOnARTForMoreThan6Months();
    CohortDefinition chdVL2 = getPatientsWithVLLessThan1000();
    CohortDefinition chdVL3 = getPatientsWithMostRecentVLQuantitativeResult();
    CohortDefinition chdVL4 = getPatientsWhoHaveRegisteredVLLessThan1000ForMoreThan12Months();
    CohortDefinition chdVL5 = getPatientsWithVLIgualOrGreaterThan1000();
    CohortDefinition chdVL6 =
        getPatientsWhoHaveRegisteredVLIgualOrGreaterThan1000ForMoreThan3Months();
    CohortDefinition chdVL7 = getPatientsWhoDontHaveAnyViralLoad();
    CohortDefinition chdE1 = txNewCohortQueries.getTxNewBreastfeedingComposition(false);
    CohortDefinition chdE2 = txNewCohortQueries.getPatientsPregnantEnrolledOnART(false);

    cd.addSearch(
        "txcurr", EptsReportUtils.map(txcurr, "onOrBefore=${endDate},location=${location}"));

    cd.addSearch(
        "X1",
        EptsReportUtils.map(
            chdX1, "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "X2",
        EptsReportUtils.map(
            chdX2, "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "X3",
        EptsReportUtils.map(
            chdX3, "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "VL1",
        EptsReportUtils.map(
            chdVL1, "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch("VL2", EptsReportUtils.map(chdVL2, "startDate=${startDate},location=${location}"));
    cd.addSearch("VL3", EptsReportUtils.map(chdVL3, "startDate=${startDate},location=${location}"));
    cd.addSearch(
        "VL4",
        EptsReportUtils.map(
            chdVL4, "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch("VL5", EptsReportUtils.map(chdVL5, "startDate=${startDate},location=${location}"));
    cd.addSearch(
        "VL6",
        EptsReportUtils.map(
            chdVL6, "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch("VL7", EptsReportUtils.map(chdVL7, "startDate=${startDate},location=${location}"));
    cd.addSearch(
        "E1",
        EptsReportUtils.map(
            chdE1, "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
    cd.addSearch(
        "E2",
        EptsReportUtils.map(
            chdE2, "startDate=${startDate},endDate=${endDate},location=${location}"));

    cd.setCompositionString(
        "txcurr AND (X1 OR X2 OR X3) AND VL1 (( (VL2 OR VL3) AND VL4) OR (VL5 AND VL6) OR VL7) AND NOT (E1 OR E2)");

    return cd;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>X1- select all patients with “Data da próxima consulta” (concept id 1410, value_datetime) as
   * “Last Next scheduled Consultation Date” from the most recent FICHA CLÍNICA (encounter type 6,9)
   * by report end date (encounter_datetime <= endDate) and “Last Next scheduled Consultation Date”
   * >= startDate and <= endDate
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getLastNextScheduledConsultationDate() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("All patients with data da proxima consulta");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("1410", hivMetadata.getReturnVisitDateConcept().getConceptId());

    String query =
        "  SELECT p.patient_id "
            + "             FROM   patient p   "
            + "                     INNER JOIN encounter e   "
            + "                                     ON p.patient_id = e.patient_id   "
            + "                     INNER JOIN obs o   "
            + "                                     ON e.encounter_id = o.encounter_id   "
            + "     INNER JOIN  "
            + "       ( SELECT p.patient_id, MAX(e.encounter_datetime) as encounter_date  "
            + "      FROM  patient p   "
            + "       INNER JOIN encounter e  ON p.patient_id = e.patient_id  "
            + "      WHERE p.voided = 0  "
            + "       AND e.voided = 0   "
            + "       AND e.location_id = :location  "
            + "       AND e.encounter_datetime <= :endDate  "
            + "       AND e.encounter_type IN (${6}, ${9})  "
            + "      GROUP BY p.patient_id  "
            + "       )max_encounter ON p.patient_id=max_encounter.patient_id "
            + "             WHERE  p.voided = 0   "
            + "                     AND e.voided = 0   "
            + "                     AND o.voided = 0   "
            + "                     AND e.location_id = :location "
            + "                     AND e.encounter_type IN (${6}, ${9})  "
            + "                     AND o.concept_id = ${1410}   "
            + "                     AND e.encounter_datetime <= :endDate  "
            + "                     AND o.value_datetime BETWEEN :startDate AND :endDate "
            + "                     AND max_encounter.encounter_date = e.encounter_datetime  "
            + "             GROUP BY p.patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>X2- select all patients with “Data do próximo levantamento” (concept id 5096,
   * value_datetime) as “Last Next scheduled Pick-up Date” from the most recent FILA (encounter type
   * 18) by report end date (encounter_datetime <= endDate) and “Last Next scheduled Pick-up Date”
   * >= startDate and <= endDate
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getLastNextScheduledPickUpDate() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "All patients with Data do próximo levantamento from most recent FILA");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("18", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    valuesMap.put("5096", hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId());

    String query =
        " SELECT p.patient_id "
            + "FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON e.patient_id = p.patient_id "
            + "       INNER JOIN obs o"
            + "               ON o.encounter_id = e.encounter_id "
            + "       INNER JOIN (SELECT p.patient_id,"
            + "                          e.encounter_id,"
            + "                          Max(e.encounter_datetime) most_recent "
            + "                   FROM   patient p "
            + "                          INNER JOIN encounter e "
            + "                                  ON p.patient_id = e.patient_id "
            + "                   WHERE  e.encounter_type = 18 "
            + "                          AND e.encounter_datetime <= :endDate "
            + "                          AND e.location_id = :location "
            + "                          AND e.voided = 0 "
            + "                          AND p.voided = 0 "
            + "                   GROUP  BY p.patient_id) last_scheduled "
            + "               ON last_scheduled.patient_id = p.patient_id "
            + "WHERE  last_scheduled.most_recent = e.encounter_datetime "
            + "       AND e.voided = 0 "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type = ${18} "
            + "       AND p.voided = 0 "
            + "       AND o.voided = 0 "
            + "       AND o.concept_id = ${5096} "
            + "       AND o.value_datetime BETWEEN :startDate AND :endDate "
            + "GROUP  BY p.patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>X3- select all patients with most recent “Data de Levantamento” (concept_id 23866,
   * value_datetime + 30 days) “Last Next scheduled Pick up Date” from “Recepcao Levantou ARV”
   * (encounter type 52) with concept “Levantou ARV” (concept_id 23865) set to “SIM” (Concept id
   * 1065) by report end date (value_datetime <= endDate) and “Last Next scheduled Pick-up Date” >=
   * startDate and <= endDate
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getLastNextScheduledPickUpDateWithMostRecentDataLevantamento() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("All patients with most recent Data de Levantamento");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("52", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    valuesMap.put("23866", hivMetadata.getArtDatePickupMasterCard().getConceptId());
    valuesMap.put("23865", hivMetadata.getArtPickupConcept().getConceptId());
    valuesMap.put("1065", hivMetadata.getPatientFoundYesConcept().getConceptId());

    String query =
        " SELECT p.patient_id FROM patient p "
            + "         INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "         INNER JOIN obs oyes ON oyes.encounter_id = e.encounter_id "
            + "         INNER JOIN obs ovalue ON ovalue.encounter_id = e.encounter_id "
            + "         INNER JOIN "
            + "     (SELECT  p.patient_id, MAX(DATE_ADD(o.value_datetime, INTERVAL 30 DAY)) AS last_obs "
            + "     FROM patient p "
            + "     INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "     INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "     WHERE   e.encounter_type = ${52} "
            + "             AND o.concept_id = ${23866} "
            + "             AND o.value_datetime <= :endDate          "
            + "             AND e.voided = 0 "
            + "             AND e.location_id = :location "
            + "             AND e.voided = 0 "
            + "             AND p.voided = 0 "
            + "             AND o.voided = 0 "
            + "     GROUP BY p.patient_id) most_recent ON most_recent.patient_id = e.patient_id "
            + " WHERE "
            + "     e.encounter_type = ${52} "
            + "         AND e.location_id = :location "
            + "         AND oyes.concept_id = ${23865} "
            + "         AND oyes.value_coded = ${1065} "
            + "         AND DATE_ADD(ovalue.value_datetime, INTERVAL 30 DAY) = most_recent.last_obs "
            + "         AND most_recent.last_obs BETWEEN :startDate  AND :endDate  "
            + "         AND p.voided = 0 "
            + "         AND e.voided = 0 "
            + "         AND oyes.voided = 0 "
            + "         AND ovalue.voided = 0 "
            + "          "
            + " GROUP BY p.patient_id  ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>VL1- select all patients on ART for more than 6 months on their Last Scheduled Consultation
   * or Drug Pick-up that occurred during the reporting period ( ScheduledDate minus ArtStartDate >=
   * 6 months)
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsOnARTForMoreThan6Months() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "All patients on ART for more than 6 months on their Last Scheduled Consultation");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("13", hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId());
    valuesMap.put("51", hivMetadata.getFsrEncounterType().getEncounterTypeId());
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    valuesMap.put("1305", hivMetadata.getHivViralLoadQualitative().getConceptId());
    valuesMap.put("52", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    valuesMap.put("23866", hivMetadata.getArtDatePickupMasterCard().getConceptId());
    valuesMap.put("23865", hivMetadata.getArtPickupConcept().getConceptId());
    valuesMap.put("1065", hivMetadata.getPatientFoundYesConcept().getConceptId());
    valuesMap.put("1190", hivMetadata.getARVStartDateConcept().getConceptId());
    valuesMap.put("1255", hivMetadata.getARVPlanConcept().getConceptId());
    valuesMap.put("1410", hivMetadata.getReturnVisitDateConcept().getConceptId());
    valuesMap.put("18", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    valuesMap.put("5096", hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId());
    valuesMap.put("1256", hivMetadata.getStartDrugs().getConceptId());

    String query =
        " SELECT recentx.patient_id FROM ( "
            + " SELECT most_recentx.patient_id, MAX(most_recentx.recent_date) xdate FROM  "
            + " (SELECT p.patient_id, o.value_datetime AS recent_date  "
            + " FROM   patient p    "
            + " INNER JOIN encounter e  ON p.patient_id = e.patient_id    "
            + " INNER JOIN obs o ON e.encounter_id = o.encounter_id    "
            + " INNER JOIN   "
            + " ( SELECT p.patient_id, MAX(e.encounter_datetime) as encounter_datetime   "
            + " FROM  patient p    "
            + " INNER JOIN encounter e  ON p.patient_id = e.patient_id   "
            + " WHERE p.voided = 0   "
            + " AND e.voided = 0    "
            + " AND e.location_id = :location   "
            + " AND e.encounter_datetime <= :endDate  "
            + " AND e.encounter_type IN (${6}, ${9})   "
            + " GROUP BY p.patient_id "
            + " ) max_encounter ON p.patient_id = max_encounter.patient_id "
            + " WHERE  p.voided = 0    "
            + " AND e.voided = 0    "
            + " AND o.voided = 0    "
            + " AND e.location_id = :location  "
            + " AND e.encounter_type IN (${6}, ${9})   "
            + " AND o.concept_id = ${1410}   "
            + " AND o.value_datetime BETWEEN :startDate AND :endDate  "
            + " AND max_encounter.encounter_datetime = e.encounter_datetime   "
            + " GROUP BY p.patient_id  "
            + " UNION  "
            + " SELECT p.patient_id, most_recent.last_obs AS recent_date  "
            + " FROM patient p"
            + "        INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "        INNER JOIN obs oyes ON oyes.encounter_id = e.encounter_id "
            + "        INNER JOIN obs ovalue ON ovalue.encounter_id = e.encounter_id "
            + "        INNER JOIN"
            + "    (SELECT  p.patient_id, MAX(DATE_ADD(o.value_datetime, INTERVAL 30 DAY)) AS last_obs "
            + "    FROM patient p "
            + "    INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "    INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "    WHERE   e.encounter_type = ${52} "
            + "            AND o.concept_id = ${23866} "
            + "            AND o.value_datetime <= :endDate         "
            + "            AND e.voided = 0 "
            + "            AND e.location_id = :location"
            + "            AND e.voided = 0 "
            + "            AND p.voided = 0 "
            + "            AND o.voided = 0 "
            + "    GROUP BY p.patient_id) most_recent ON most_recent.patient_id = e.patient_id "
            + " WHERE "
            + "    e.encounter_type = ${52} "
            + "        AND e.location_id = :location "
            + "        AND oyes.concept_id = ${23865} "
            + "        AND oyes.value_coded = ${1065} "
            + "        AND DATE_ADD(ovalue.value_datetime, INTERVAL 30 DAY) = most_recent.last_obs"
            + "        AND most_recent.last_obs BETWEEN :startDate AND :endDate "
            + "        AND p.voided = 0 "
            + "        AND e.voided = 0 "
            + "        AND oyes.voided = 0 "
            + "        AND ovalue.voided = 0 "
            + "        "
            + " GROUP BY p.patient_id "
            + " UNION  "
            + " SELECT p.patient_id, most_recent.last_obs AS recent_date FROM patient p  "
            + " INNER JOIN encounter e ON e.patient_id = p.patient_id  "
            + " INNER JOIN obs o ON o.encounter_id = e.encounter_id  "
            + " INNER JOIN  "
            + " (SELECT p.patient_id, MAX(o.value_datetime) AS last_obs, o.obs_datetime  "
            + " FROM encounter e INNER JOIN obs o ON o.encounter_id = e.encounter_id  "
            + " INNER JOIN patient p ON p.patient_id = e.patient_id  "
            + " WHERE e.encounter_type = ${52}  "
            + " AND o.concept_id = ${23866}  "
            + "          "
            + " AND e.voided = 0  "
            + " AND e.location_id = :location  "
            + "         AND e.voided = 0  "
            + "         AND p.voided = 0  "
            + "         AND o.voided = 0  "
            + " GROUP BY p.patient_id) most_recent  "
            + " ON most_recent.patient_id = e.patient_id  "
            + " WHERE p.voided = 0  "
            + "   AND e.encounter_type = ${52}  "
            + "   AND e.location_id = :location  "
            + "   AND o.concept_id = ${23865}  "
            + "   AND o.value_coded = ${1065}  "
            + "   AND o.obs_datetime = most_recent.obs_datetime  "
            + "   AND o.voided = 0  "
            + "   AND e.voided = 0  "
            + "   AND p.voided = 0  "
            + "   AND DATE_ADD(most_recent.last_obs, INTERVAL 30 DAY) BETWEEN :startDate AND :endDate "
            + "   AND most_recent.last_obs <= :endDate "
            + " GROUP BY p.patient_id) AS most_recentx "
            + " GROUP BY most_recentx.patient_id "
            + " ) recentx "
            + " INNER JOIN "
            + " (  "
            + " SELECT art.patient_id, MIN(art.art_date) min_art_date FROM ( "
            + " SELECT p.patient_id, MIN(e.encounter_datetime) art_date FROM patient p "
            + " INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + " WHERE e.encounter_type = ${18} "
            + " AND e.encounter_datetime <= :endDate "
            + " AND e.voided = 0 "
            + " AND p.voided = 0 "
            + " AND e.location_id = :location "
            + " GROUP BY p.patient_id "
            + "  "
            + " UNION "
            + "  "
            + " SELECT p.patient_id, Min(e.encounter_datetime) art_date  "
            + "                                 FROM patient p  "
            + "                           INNER JOIN encounter e  "
            + "                               ON p.patient_id = e.patient_id  "
            + "                           INNER JOIN obs o  "
            + "                               ON e.encounter_id = o.encounter_id  "
            + "                       WHERE  p.voided = 0  "
            + "                           AND e.voided = 0  "
            + "                           AND o.voided = 0  "
            + "                           AND e.encounter_type IN (${6}, ${9}, ${18})  "
            + "                           AND o.concept_id = ${1255}  "
            + "                           AND o.value_coded= ${1256}  "
            + "                           AND e.encounter_datetime <= :endDate  "
            + "                           AND e.location_id = :location  "
            + "                       GROUP  BY p.patient_id  "
            + " UNION "
            + " SELECT p.patient_id, historical.min_date AS art_date FROM patient p  "
            + " INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "     INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "     INNER JOIN( "
            + " SELECT p.patient_id,e.encounter_id,  MIN(o.value_datetime) min_date FROM patient p "
            + " INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + " INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + " WHERE e.encounter_type IN(${6},${9},${18},${53}) "
            + " AND o.concept_id = ${1190} "
            + " AND e.location_id = :location "
            + "                 AND o.value_datetime <= :endDate "
            + " AND e.voided = 0 "
            + " AND p.voided = 0 "
            + " GROUP BY p.patient_id "
            + "                 ) historical "
            + " ON historical.patient_id = p.patient_id "
            + " WHERE e.encounter_type IN(${6},${9},${18},${53}) "
            + " AND o.concept_id = ${1190} "
            + " AND e.location_id = :location "
            + "                 AND o.value_datetime <= :endDate "
            + " AND e.voided = 0 "
            + " AND p.voided = 0 "
            + "                 AND historical.encounter_id = e.encounter_id "
            + "                 AND o.value_datetime = historical.min_date "
            + " GROUP BY p.patient_id "
            + "                  "
            + " UNION "
            + "  "
            + " SELECT p.patient_id, ps.start_date AS art_date "
            + "     FROM   patient p   "
            + "           INNER JOIN patient_program pg  "
            + "                ON p.patient_id = pg.patient_id  "
            + "        INNER JOIN patient_state ps  "
            + "                   ON pg.patient_program_id = ps.patient_program_id  "
            + "     WHERE  pg.location_id = :location "
            + "    AND pg.program_id = 2 and ps.start_date <= :endDate "
            + "     "
            + "    UNION "
            + "     "
            + "    SELECT p.patient_id,  MIN(o.value_datetime) AS art_date FROM patient p "
            + " INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + " INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                         INNER JOIN obs oyes ON oyes.encounter_id = e.encounter_id  "
            + "                         AND o.person_id = oyes.person_id "
            + " WHERE e.encounter_type = ${52} "
            + " AND o.concept_id = ${23866} "
            + "                 AND o.value_datetime <= :endDate "
            + "                 AND o.voided = 0 "
            + "                 AND oyes.concept_id = ${23865} "
            + "                 AND oyes.value_coded = ${1065} "
            + "                 AND oyes.voided = 0 "
            + " AND e.location_id = :location                 "
            + " AND e.voided = 0 "
            + " AND p.voided = 0 "
            + "  "
            + " GROUP BY p.patient_id "
            + " ) art  "
            + " GROUP BY art.patient_id "
            + " ) min_art "
            + " ON recentx.patient_id = min_art.patient_id "
            + " AND TIMESTAMPDIFF(MONTH, min_art.min_art_date, recentx.xdate) >= 6 "
            + " GROUP BY recentx.patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>VL2 - select all patients with the most recent VL Numeric Result (concept Id 856) documented
   * in the Laboratory Form (encounter type 13, encounter_datetime) or Ficha de Seguimento Adulto or
   * Pediatria (encounter type 6,9, encounter_datetime) or Ficha Clinica (encounter type 6,
   * encounter_datetime) or Ficha Resumo (encounter type 53, obs_datetime ) or FSR form (encounter
   * type 51, encounter_datetime) by start of reporting period (<=startDate) and the Result is <
   * 1000 copias/ml (concept 856 value_numeric < 1000)
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWithVLLessThan1000() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("All patients with the most recent VL Numeric Result");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    String query = mostRecentVLNumericResultQuery(Sentence.LessThan, 1000);

    sqlCohortDefinition.setQuery(query);

    return sqlCohortDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>VL3- select all patients with the most recent VL Qualitative Result (concept Id 1305)
   * documented in the Laboratory Form (encounter type 13, encounter_datetime) or Ficha de
   * Seguimento Adulto or Pediatria (encounter type 6,9, encounter_datetime) or Ficha Clinica
   * (encounter type 6, encounter_datetime) or Ficha Resumo (encounter type 53, obs_datetime ) or
   * FSR form (encounter type 51, encounter_datetime) by start of reporting period (<=startDate) and
   * the Result is not Null (concept 1305 value_coded NOT Null)
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWithMostRecentVLQuantitativeResult() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("All patients with the most recent VL Qualitative Result ");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("13", hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId());
    valuesMap.put("51", hivMetadata.getFsrEncounterType().getEncounterTypeId());
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("1305", hivMetadata.getHivViralLoadQualitative().getConceptId());

    String query =
        "  SELECT max.patient_id FROM ( "
            + "     SELECT max_val_result.patient_id, MAX(max_val_result.max_vl) FROM ( "
            + "         SELECT  p.patient_id, MAX(e.encounter_datetime) AS max_vl FROM patient p "
            + "             INNER JOIN encounter e on p.patient_id = e.patient_id "
            + "             INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "         WHERE p.voided =0 "
            + "             AND e.voided =0 "
            + "             AND o.voided = 0 "
            + "             AND e.location_id = :location "
            + "             AND o.concept_id = ${1305} "
            + "             AND e.encounter_type IN (${13},${6},${9},${51}) "
            + "             AND e.encounter_datetime <= :startDate "
            + "             AND o.value_coded IS NOT NULL "
            + "         GROUP BY p.patient_id "
            + "         UNION "
            + "         SELECT  p.patient_id, MAX(o.obs_datetime) AS max_vl FROM patient p "
            + "             INNER JOIN encounter e on p.patient_id = e.patient_id "
            + "             INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "         WHERE p.voided =0 "
            + "             AND e.voided =0 "
            + "             AND e.location_id = :location "
            + "             AND o.voided = 0 "
            + "             AND o.concept_id = ${1305} "
            + "             AND e.encounter_type = ${53} "
            + "             AND o.obs_datetime <=  :startDate "
            + "             AND o.value_coded IS NOT NULL "
            + "         GROUP BY p.patient_id) AS max_val_result GROUP BY max_val_result.patient_id) AS max ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>Select all patients who have registered the most recent VL <1000 copies/ml for more than 12
   * months before “ScheduledDate”, i.e. “ScheduledDate” minus “Most Recent VL Date” >= 12months
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWhoHaveRegisteredVLLessThan1000ForMoreThan12Months() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "Select all patients who have registered the most recent VL <1000 copies/ml for more than 12 months");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();

    valuesMap.put("1410", hivMetadata.getReturnVisitDateConcept().getConceptId());
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("13", hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId());
    valuesMap.put("51", hivMetadata.getFsrEncounterType().getEncounterTypeId());
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    valuesMap.put("1305", hivMetadata.getHivViralLoadQualitative().getConceptId());
    valuesMap.put("18", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    valuesMap.put("5096", hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId());
    valuesMap.put("52", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    valuesMap.put("23866", hivMetadata.getArtDatePickupMasterCard().getConceptId());
    valuesMap.put("23865", hivMetadata.getArtPickupConcept().getConceptId());
    valuesMap.put("1065", hivMetadata.getPatientFoundYesConcept().getConceptId());

    String query =
        " SELECT most_recentvl.patient_id "
            + " FROM( "
            + " SELECT most_recent.patient_id AS patient_id, MAX(recent_datetime) AS recent_date   FROM( "
            + " SELECT p.patient_id, MAX(e.encounter_datetime) recent_datetime "
            + " FROM patient p "
            + "                 INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                 INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                 WHERE e.encounter_type IN (${13},${6},${9},${51}) "
            + "                 AND e.encounter_datetime <= :startDate "
            + "                 AND o.concept_id = ${856} "
            + "                 AND o.value_numeric < 1000 "
            + "                 AND e.location_id = :location "
            + "                 AND e.voided = 0 "
            + "                 AND o.voided = 0 "
            + "                 AND p.voided = 0 "
            + "                 GROUP BY p.patient_id "
            + "                  "
            + "                 UNION "
            + "                  "
            + "                SELECT p.patient_id, MAX(o.obs_datetime) recent_datetime "
            + "                 FROM patient p  "
            + "                 INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                 INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                 WHERE e.encounter_type = ${53} "
            + "                 AND o.concept_id = ${856} "
            + "                 AND o.obs_datetime <= :startDate "
            + "                 AND e.location_id = :location "
            + "                 AND e.voided = 0 "
            + "                 AND p.voided = 0 "
            + "                 AND o.voided = 0    "
            + "                 GROUP BY p.patient_id "
            + "                  "
            + " ) AS most_recent GROUP BY most_recent.patient_id "
            + "              "
            + "             UNION "
            + "              "
            + "              SELECT max_val_result.patient_id AS patient_id, MAX(max_val_result.max_vl) AS recent_date FROM ( "
            + "         SELECT  p.patient_id, MAX(e.encounter_datetime) AS max_vl FROM patient p "
            + "             INNER JOIN encounter e on p.patient_id = e.patient_id "
            + "             INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "         WHERE p.voided =0 "
            + "             AND e.voided =0 "
            + "             AND e.location_id = :location "
            + "             AND o.voided = 0 "
            + "             AND o.concept_id = ${1305} "
            + "             AND e.encounter_type IN (${13},${6},${9},${51}) "
            + "             AND e.encounter_datetime <= :endDate "
            + "             AND o.value_coded IS NOT NULL "
            + "         GROUP BY p.patient_id "
            + "         UNION "
            + "         SELECT  p.patient_id, MAX(o.obs_datetime) AS max_vl FROM patient p "
            + "             INNER JOIN encounter e on p.patient_id = e.patient_id "
            + "             INNER JOIN obs o ON e.encounter_id = o.encounter_id "
            + "         WHERE p.voided =0 "
            + "             AND e.voided =0 "
            + "             AND e.location_id = :location "
            + "             AND o.voided = 0 "
            + "             AND o.concept_id = ${1305} "
            + "             AND e.encounter_type = ${53} "
            + "             AND o.obs_datetime <= :endDate "
            + "             AND o.value_coded IS NOT NULL "
            + "         GROUP BY p.patient_id) AS max_val_result GROUP BY max_val_result.patient_id "
            + "         ) AS most_recentvl        "
            + " INNER JOIN  "
            + "  (        "
            + " SELECT xpatient.patient_id, xpatient.xdate recentx_date FROM ( "
            + " SELECT most_recentx.patient_id, MAX(most_recentx.recent_date) xdate FROM "
            + " (SELECT p.patient_id, o.value_datetime AS recent_date "
            + " FROM   patient p    "
            + " INNER JOIN encounter e  ON p.patient_id = e.patient_id    "
            + " INNER JOIN obs o ON e.encounter_id = o.encounter_id    "
            + " INNER JOIN   "
            + " ( SELECT p.patient_id, MAX(e.encounter_datetime) as encounter_datetime   "
            + " FROM  patient p    "
            + " INNER JOIN encounter e  ON p.patient_id = e.patient_id   "
            + " WHERE p.voided = 0   "
            + " AND e.voided = 0    "
            + " AND e.location_id = :location   "
            + " AND e.encounter_datetime <= :endDate  "
            + " AND e.encounter_type IN (${6}, ${9})   "
            + " GROUP BY p.patient_id "
            + " ) max_encounter ON p.patient_id = max_encounter.patient_id "
            + " WHERE  p.voided = 0    "
            + " AND e.voided = 0    "
            + " AND o.voided = 0    "
            + " AND e.location_id = :location  "
            + " AND e.encounter_type IN (${6}, ${9})   "
            + " AND o.concept_id = ${1410}   "
            + " AND o.value_datetime BETWEEN :startDate AND :endDate  "
            + " AND max_encounter.encounter_datetime = e.encounter_datetime   "
            + " GROUP BY p.patient_id  "
            + "  "
            + " UNION "
            + "  "
            + " SELECT p.patient_id, o.value_datetime AS recent_date "
            + " FROM patient p "
            + " INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "         INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "         INNER JOIN ( "
            + " SELECT p.patient_id,e.encounter_id, MAX(e.encounter_datetime) most_recent "
            + " FROM patient p INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + " WHERE e.encounter_type = ${18} "
            + " AND e.encounter_datetime <= :endDate "
            + "                 AND e.location_id = :location "
            + " AND e.voided = 0 "
            + " AND p.voided = 0     "
            + " GROUP BY p.patient_id) last_scheduled  "
            + " ON last_scheduled.patient_id = p.patient_id  "
            + " WHERE last_scheduled.most_recent = e.encounter_datetime                     "
            + "                     AND e.voided = 0 "
            + "                     AND e.location_id = :location "
            + "                     AND e.encounter_type = ${18} "
            + "                     AND p.voided = 0 "
            + "                     AND o.voided = 0 "
            + "                     AND o.concept_id = ${5096} "
            + " AND o.value_datetime BETWEEN :startDate AND :endDate "
            + " GROUP BY p.patient_id "
            + "                      "
            + " UNION "
            + "  "
            + " SELECT p.patient_id, most_recent.last_obs AS recent_date FROM patient p "
            + " INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + " INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + " INNER JOIN "
            + " (SELECT p.patient_id, MAX(o.value_datetime) AS last_obs, o.obs_datetime "
            + " FROM encounter e INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + " INNER JOIN patient p ON p.patient_id = e.patient_id "
            + " WHERE e.encounter_type = ${52} "
            + " AND o.concept_id = ${23866} "
            + "          "
            + " AND e.voided = 0 "
            + " AND e.location_id = :location "
            + "         AND e.voided = 0  "
            + "         AND p.voided = 0 "
            + "         AND o.voided = 0 "
            + " GROUP BY p.patient_id) most_recent "
            + " ON most_recent.patient_id = e.patient_id "
            + " WHERE p.voided = 0 "
            + "   AND e.encounter_type = ${52} "
            + "   AND e.location_id = :location "
            + "   AND o.concept_id = ${23865} "
            + "   AND o.value_coded = ${1065} "
            + "   AND o.obs_datetime = most_recent.obs_datetime "
            + "   AND o.voided = 0 "
            + "   AND e.voided = 0 "
            + "   AND p.voided = 0 "
            + "   AND DATE_ADD(most_recent.last_obs, INTERVAL 30 DAY) BETWEEN :startDate AND :endDate "
            + "   AND most_recent.last_obs <= :endDate "
            + " GROUP BY p.patient_id) AS most_recentx "
            + " GROUP BY most_recentx.patient_id "
            + " ) AS xpatient "
            + " ) x ON x.patient_id = most_recentvl.patient_id "
            + " AND TIMESTAMPDIFF(MONTH,  most_recentvl.recent_date,x.recentx_date) >= 12 "
            + " GROUP BY x.patient_id ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>VL5- select all patients with the most recent VL Numeric Result (concept Id 856) documented
   * in the Laboratory Form (encounter type 13, encounter_datetime) or Ficha de Seguimento Adulto or
   * Pediatria (encounter type 6,9, encounter_datetime) or Ficha Clinica (encounter type 6,
   * encounter_datetime) or Ficha Resumo (encounter type 53, obs_datetime ) or FSR form (encounter
   * type 51, encounter_datetime) by start of reporting period (<=startDate) and the Result is >=
   * 1000 copias/ml (concept 856 value_numeric >= 1000)
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWithVLIgualOrGreaterThan1000() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("All patients with the most recent VL Numeric Result");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    String query = mostRecentVLNumericResultQuery(Sentence.EqualOrGreaterThan, 1000);

    sqlCohortDefinition.setQuery(query);

    return sqlCohortDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>VL6- Select all patients who have registered the most recent VL >=1000 copies/ml for more
   * than 3 months before “ScheduledDate”, i.e. “ScheduledDate” minus “Most Recent VL Date 3” >=
   * 3months
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWhoHaveRegisteredVLIgualOrGreaterThan1000ForMoreThan3Months() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(
        "All all patients who have registered the most recent VL >=1000 copies/ml for more than 3 months");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("endDate", "endDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();

    valuesMap.put("1410", hivMetadata.getReturnVisitDateConcept().getConceptId());
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("13", hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId());
    valuesMap.put("51", hivMetadata.getFsrEncounterType().getEncounterTypeId());
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    valuesMap.put("1305", hivMetadata.getHivViralLoadQualitative().getConceptId());
    valuesMap.put("18", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    valuesMap.put("5096", hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId());
    valuesMap.put("52", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());
    valuesMap.put("23866", hivMetadata.getArtDatePickupMasterCard().getConceptId());
    valuesMap.put("23865", hivMetadata.getArtPickupConcept().getConceptId());
    valuesMap.put("1065", hivMetadata.getPatientFoundYesConcept().getConceptId());

    String query =
        " SELECT vl5.patient_id FROM( "
            + " SELECT patient_id, MAX(recent_date) vl5_date FROM( "
            + " SELECT p.patient_id, MAX(o.obs_datetime) recent_date FROM patient p "
            + " INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "         INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + " WHERE e.encounter_type = ${53} "
            + " AND o.concept_id = ${856} "
            + " AND o.value_numeric >= 1000 "
            + " AND o.obs_datetime <= :startDate "
            + " AND e.location_id = :location "
            + " AND e.voided = 0 "
            + " AND p.voided = 0 "
            + " AND o.voided = 0 "
            + " GROUP BY patient_id "
            + " UNION "
            + " SELECT p.patient_id, MAX(e.encounter_datetime) recent_date FROM patient p "
            + " INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "     INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + " WHERE e.encounter_type IN(${13}, ${6},${9}, ${51}) "
            + " AND o.concept_id = ${856} "
            + " AND o.value_numeric >= 1000 "
            + " AND e.encounter_datetime <= :startDate "
            + " AND e.location_id = :location "
            + " AND e.voided = 0 "
            + " AND p.voided = 0 "
            + " AND o.voided = 0 "
            + " GROUP BY p.patient_id "
            + " ) AS most_recent_vl  "
            + " GROUP BY patient_id "
            + " )vl5 "
            + " INNER JOIN "
            + " ( "
            + " SELECT most_recentx.patient_id, MAX(most_recentx.recent_date) AS recentx_date FROM "
            + " (SELECT p.patient_id, o.value_datetime AS recent_date "
            + " FROM   patient p    "
            + " INNER JOIN encounter e  ON p.patient_id = e.patient_id    "
            + " INNER JOIN obs o ON e.encounter_id = o.encounter_id    "
            + " INNER JOIN   "
            + " ( SELECT p.patient_id, MAX(e.encounter_datetime) as encounter_datetime   "
            + " FROM  patient p    "
            + " INNER JOIN encounter e  ON p.patient_id = e.patient_id   "
            + " WHERE p.voided = 0   "
            + " AND e.voided = 0    "
            + " AND e.location_id = :location   "
            + " AND e.encounter_datetime <= :endDate  "
            + " AND e.encounter_type IN (${6}, ${9})   "
            + " GROUP BY p.patient_id "
            + " ) max_encounter ON p.patient_id = max_encounter.patient_id "
            + " WHERE  p.voided = 0    "
            + " AND e.voided = 0    "
            + " AND o.voided = 0    "
            + " AND e.location_id = :location "
            + " AND e.encounter_type IN (${6}, ${9})   "
            + " AND o.concept_id = ${1410}   "
            + " AND o.value_datetime BETWEEN :startDate AND :endDate  "
            + " AND max_encounter.encounter_datetime = e.encounter_datetime   "
            + " GROUP BY p.patient_id  "
            + " UNION "
            + " SELECT p.patient_id, o.value_datetime AS recent_date "
            + " FROM patient p "
            + " INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "         INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "         INNER JOIN ( "
            + " SELECT p.patient_id,e.encounter_id, MAX(e.encounter_datetime) most_recent "
            + " FROM patient p INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + " WHERE e.encounter_type = ${18} "
            + " AND e.encounter_datetime <= :endDate "
            + "                 AND e.location_id = :location "
            + " AND e.voided = 0 "
            + " AND p.voided = 0     "
            + " GROUP BY p.patient_id) last_scheduled  "
            + " ON last_scheduled.patient_id = p.patient_id  "
            + " WHERE last_scheduled.most_recent = e.encounter_datetime                     "
            + "                     AND e.voided = 0 "
            + "                     AND e.location_id = :location "
            + "                     AND e.encounter_type = ${18} "
            + "                     AND p.voided = 0 "
            + "                     AND o.voided = 0 "
            + "                     AND o.concept_id = ${5096} "
            + " AND o.value_datetime BETWEEN :startDate AND :endDate "
            + " GROUP BY p.patient_id "
            + " UNION "
            + " SELECT p.patient_id, most_recent.last_obs AS recent_date FROM patient p "
            + " INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + " INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + " INNER JOIN "
            + " (SELECT p.patient_id, MAX(o.value_datetime) AS last_obs, o.obs_datetime "
            + " FROM encounter e INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + " INNER JOIN patient p ON p.patient_id = e.patient_id "
            + " WHERE e.encounter_type = ${52} "
            + " AND o.concept_id = ${23866} "
            + "          "
            + " AND e.voided = 0 "
            + " AND e.location_id = :location "
            + "         AND e.voided = 0  "
            + "         AND p.voided = 0 "
            + "         AND o.voided = 0 "
            + " GROUP BY p.patient_id) most_recent "
            + " ON most_recent.patient_id = e.patient_id "
            + " WHERE p.voided = 0 "
            + "   AND e.encounter_type = ${52} "
            + "   AND e.location_id = :location "
            + "   AND o.concept_id = ${23865} "
            + "   AND o.value_coded = ${1065} "
            + "   AND o.obs_datetime = most_recent.obs_datetime "
            + "   AND o.voided = 0 "
            + "   AND e.voided = 0 "
            + "   AND p.voided = 0 "
            + "   AND DATE_ADD(most_recent.last_obs, INTERVAL 30 DAY) BETWEEN :startDate AND :endDate "
            + "   AND most_recent.last_obs <= :endDate "
            + " GROUP BY p.patient_id) AS most_recentx "
            + " GROUP BY most_recentx.patient_id "
            + " ) recentx "
            + " ON vl5.patient_id = recentx.patient_id "
            + " AND TIMESTAMPDIFF(MONTH,  vl5.vl5_date,recentx.recentx_date) >= 3 ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * <p>VL7- select all patients who DO NOT have any Viral Load Result (concept Id 856 -
   * value_numeric, or concept id 1305 value_coded not null ) documented in the Laboratory Form
   * (encounter type 13, encounter_datetime) or Ficha de Seguimento Adulto or Pediatria (encounter
   * type 6,9, encounter_datetime) or Ficha Clinica (encounter type 6, encounter_datetime) or Ficha
   * Resumo (encounter type 53, obs_datetime ) or FSR form (encounter type 51, encounter_datetime)
   * by start of reporting period (<=startDate)
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPatientsWhoDontHaveAnyViralLoad() {

    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName("All patients who DO NOT have any Viral Load Result");
    sqlCohortDefinition.addParameter(new Parameter("startDate", "startDate", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("13", hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId());
    valuesMap.put("51", hivMetadata.getFsrEncounterType().getEncounterTypeId());
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    valuesMap.put("1305", hivMetadata.getHivViralLoadQualitative().getConceptId());

    String query =
        "  SELECT match_patient.patient_id FROM( "
            + "              SELECT most_recent.patient_id, MAX(most_recent.max_date) FROM( "
            + "              SELECT p.patient_id, MAX(o.obs_datetime) max_date FROM patient p "
            + "              INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                  INNER JOIN obs o ON o.encounter_id = e.encounter_id     "
            + "              WHERE e.encounter_type = ${53} "
            + "              AND ((o.concept_id = ${856} AND o.value_numeric IS NOT NULL) OR (o.concept_id = ${1305} AND o.value_coded IS NOT NULL))  "
            + "              AND o.obs_datetime <= :startDate "
            + "              AND e.location_id = :location "
            + "              AND e.voided = 0 "
            + "              AND p.voided = 0 "
            + "              AND o.voided = 0 "
            + "              GROUP BY p.patient_id "
            + "               "
            + "              UNION "
            + "               "
            + "              SELECT p.patient_id, MAX(e.encounter_datetime) max_date FROM patient p "
            + "              INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                  INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                  WHERE e.encounter_type IN(${13},${6},${9},${51}) "
            + "                  AND e.encounter_datetime <= :startDate "
            + "                  AND ((o.concept_id = ${856} AND o.value_numeric IS NOT NULL ) OR (o.concept_id = ${1305} AND o.value_coded IS NOT NULL) ) "
            + "                  AND e.location_id = :location "
            + "                  AND e.voided = 0 "
            + "                  AND p.voided = 0 "
            + "                  AND o.voided = 0 "
            + "              GROUP BY patient_id) AS most_recent GROUP BY most_recent.patient_id "
            + "               "
            + "              ) AS match_patient GROUP BY match_patient.patient_id "
            + "          ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    sqlCohortDefinition.setQuery(stringSubstitutor.replace(query));

    return sqlCohortDefinition;
  }

  private String mostRecentVLNumericResultQuery(Sentence sentence, Integer value) {

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    valuesMap.put("13", hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId());
    valuesMap.put("51", hivMetadata.getFsrEncounterType().getEncounterTypeId());
    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());

    String query =
        "      SELECT patient_id FROM( "
            + " SELECT most_recent.patient_id, MAX(recent_datetime) FROM( "
            + " SELECT p.patient_id, MAX(e.encounter_datetime) recent_datetime "
            + " FROM patient p "
            + "                 INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                 INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                 WHERE e.encounter_type IN (${13},${6},${9},${51}) "
            + "                 AND e.encounter_datetime <= :startDate "
            + "                 AND o.concept_id = ${856} "
            + "                 AND o.value_numeric %s %d "
            + "                 AND e.location_id = :location "
            + "                 AND e.voided = 0 "
            + "                 AND o.voided = 0 "
            + "                 AND p.voided = 0 "
            + "                 GROUP BY p.patient_id "
            + "                  "
            + "                 UNION "
            + "                  "
            + "                SELECT p.patient_id, MAX(o.obs_datetime) recent_datetime "
            + "                 FROM patient p  "
            + "                 INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                 INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                 WHERE e.encounter_type = ${53} "
            + "                 AND o.concept_id = ${856} "
            + "                 AND o.value_numeric %s %d "
            + "                 AND o.obs_datetime <= :startDate "
            + "                 AND e.location_id = :location "
            + "                 AND e.voided = 0 "
            + "                 AND p.voided = 0 "
            + "                 AND o.voided = 0    "
            + "                 GROUP BY p.patient_id "
            + "                  "
            + " ) AS most_recent GROUP BY most_recent.patient_id "
            + "              "
            + "              "
            + " ) AS recent_vl "
            + "     GROUP BY recent_vl.patient_id ";

    String sql = String.format(query, sentence.getSentence(), value, sentence.getSentence(), value);

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    return stringSubstitutor.replace(sql);
  }

  enum Sentence {
    GreaterThan(">"),
    EqualOrGreaterThan(">="),
    LessThan("<"),
    EqualOrLessThan("<="),
    Different("<>");

    private final String sentence;

    Sentence(String sentence) {
      this.sentence = sentence;
    }

    public String getSentence() {
      return this.sentence;
    }
  }
}
