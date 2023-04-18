package org.openmrs.module.eptsreports.reporting.library.queries;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Location;
import org.openmrs.cohort.CohortDefinition;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

public class DsdQueries {

  /**
   * Get All Patients On Sarcoma Karposi
   *
   * @param adultSeguimentoEncounter - {@link HivMetadata#getAdultoSeguimentoEncounterType()}
   * @param pediatriaSeguimentoEncounter - {@link {@link
   *     HivMetadata#getPediatriaSeguimentoEncounterType()}}
   * @param otherDiagnosisConceptId - other Diagnosis ConceptId
   * @param sarcomakarposiConceptId - sarcomakarposi ConceptId
   * @return String
   */
  public static String getPatientsOnSarcomaKarposi(
      int adultSeguimentoEncounter,
      int pediatriaSeguimentoEncounter,
      int otherDiagnosisConceptId,
      int sarcomakarposiConceptId) {
    String query =
        ""
            + "SELECT "
            + " p.patient_id "
            + "FROM patient p "
            + "INNER JOIN encounter e ON p.patient_id=e.patient_id "
            + "INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + "WHERE "
            + "e.encounter_type IN (%d,%d) AND o.concept_id=%d AND o.value_coded=%d AND e.location_id= :location AND e.encounter_datetime<= :endDate AND p.voided=0 AND e.voided=0 AND o.voided=0";

    return String.format(
        query,
        adultSeguimentoEncounter,
        pediatriaSeguimentoEncounter,
        otherDiagnosisConceptId,
        sarcomakarposiConceptId);
  }

  /**
   * <b>Patients With Type Of Dispensation On Last MDC Record</b>
   * <li>All active patients whose one of the MDCs is marked as “@param dispensationTypes” with
   *     Estado do MDC as Iniciar (I) or Continuar (C) in the last Ficha Clinica with MDCs
   *     registered
   *
   * @param dispensationTypes The List of dispensation types concepts
   * @param states The list of MDC states
   * @return {@link CohortDefinition}
   */
  public static SqlCohortDefinition getPatientsWithTypeOfDispensationOnLastMdcRecord(
      List<Integer> dispensationTypes, List<Integer> states) {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName(
        "All active patients whose one of the MDCs is marked as “DT OR DS OR DA” with Iniciar or Continuar in the last Ficha Clinica with MDC");
    cd.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, String> map = new HashMap<>();
    HivMetadata hivMetadata = new HivMetadata();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId().toString());
    map.put("165174", hivMetadata.getLastRecordOfDispensingModeConcept().getConceptId().toString());
    map.put("165322", hivMetadata.getMdcState().getConceptId().toString());
    map.put("dispensationTypes", StringUtils.join(dispensationTypes, ","));
    map.put("states", StringUtils.join(states, ","));

    String query =
        " SELECT p.patient_id   "
            + "FROM   patient p   "
            + "           INNER JOIN encounter e ON e.patient_id = p.patient_id   "
            + "           INNER JOIN obs otype ON otype.encounter_id = e.encounter_id   "
            + "           INNER JOIN obs ostate ON ostate.encounter_id = e.encounter_id   "
            + "           INNER JOIN (    "
            + "    SELECT p.patient_id, MAX(e.encounter_datetime) AS last_encounter   "
            + "    FROM   patient p   "
            + "               INNER JOIN encounter e ON p.patient_id = e.patient_id   "
            + "               INNER JOIN obs otype ON otype.encounter_id = e.encounter_id   "
            + "               INNER JOIN obs ostate ON ostate.encounter_id = e.encounter_id   "
            + "    WHERE  p.voided = 0    "
            + "      AND e.voided = 0   "
            + "      AND otype.voided = 0   "
            + "      AND ostate.voided = 0    "
            + "      AND e.encounter_type = ${6}   "
            + "      AND otype.concept_id = ${165174}    "
            + "      AND otype.value_coded IS NOT NULL "
            + "      AND ostate.concept_id = ${165322}   "
            + "      AND ostate.value_coded IS NOT NULL   "
            + "      AND otype.obs_group_id = ostate.obs_group_id   "
            + "      AND e.encounter_datetime <= :onOrBefore "
            + "      AND e.location_id = :location    "
            + "    group by p.patient_id    "
            + ") first_mdc ON first_mdc.patient_id = p.patient_id   "
            + "WHERE  e.encounter_type = ${6}   "
            + "  AND otype.concept_id = ${165174}   "
            + "  AND otype.value_coded IN (${dispensationTypes})   "
            + "  AND ostate.concept_id = ${165322}    "
            + "  AND ostate.value_coded IN (${states})   "
            + "  AND e.encounter_datetime = first_mdc.last_encounter    "
            + "  AND otype.obs_group_id = ostate.obs_group_id   "
            + "  AND e.location_id = :location    "
            + "  AND e.voided = 0   "
            + "  AND p.voided = 0   "
            + "  AND otype.voided = 0   "
            + "  AND ostate.voided = 0    ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    cd.setQuery(stringSubstitutor.replace(query));

    return cd;
  }

  /**
   * DSD_FR11 bullet 1
   *
   * <p><b>Description:</b> All patients who missed the last scheduled drugs pick up in FILA and 30
   * days after the last ART pickup date registered in Ficha Recepção - Levantou ARV and adding 59
   * days and this date being less than reporting period endDate - 3 months
   *
   * @return {@link String}
   */
  public static SqlCohortDefinition
      getPatientsWhoExperiencedInterruptionIn3MonthsBeforeReportingEndDate(
          int returnVisitDateForDrugsConcept,
          int pharmacyEncounterType,
          int masterCardDrugPickupEncounterType,
          int artPickupDateMasterCardConcept) {
    SqlCohortDefinition cd = new SqlCohortDefinition();

    cd.setName("DSD_FR11 bullet 1");
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    valuesMap.put("returnVisitDateForArvDrug", returnVisitDateForDrugsConcept);
    valuesMap.put("aRVPharmaciaEncounterType", pharmacyEncounterType);
    valuesMap.put("artDatePickupMasterCard", artPickupDateMasterCardConcept);
    valuesMap.put("masterCardDrugPickupEncounterType", masterCardDrugPickupEncounterType);
    String query =
        " SELECT final.patient_id    "
            + "             from(    "
            + "                SELECT   patient_id,"
            + "                     Greatest(COALESCE(return_date_fila,0),COALESCE(return_date_master,0)) AS return_date"
            + "                FROM     ("
            + "                         SELECT p.patient_id,"
            + "                         ("
            + "                                SELECT   o.value_datetime"
            + "                                FROM     encounter e"
            + "                                JOIN     obs o"
            + "                                ON       e.encounter_id = o.encounter_id"
            + "                                WHERE    p.patient_id = e.patient_id"
            + "                                AND      e.location_id = :location"
            + "                                AND      e.encounter_type = ${aRVPharmaciaEncounterType}"
            + "                                AND      o.concept_id = ${returnVisitDateForArvDrug}"
            + "                                AND      e.voided = 0"
            + "                                AND      o.voided = 0"
            + "                                AND      e.encounter_datetime ="
            + "                                         ("
            + "                                                    SELECT     e.encounter_datetime AS return_date"
            + "                                                    FROM       encounter e"
            + "                                                    INNER JOIN obs o"
            + "                                                    ON         e.encounter_id = o.encounter_id"
            + "                                                    WHERE      p.patient_id = e.patient_id"
            + "                                                    AND        e.voided = 0"
            + "                                                    AND        o.voided = 0"
            + "                                                    AND        e.encounter_type = ${aRVPharmaciaEncounterType} "
            + "                                                    AND        e.location_id = :location"
            + "                                                    AND        e.encounter_datetime <= :endDate "
            + "                                                    ORDER BY   e.encounter_datetime DESC LIMIT 1) "
            + "                                ORDER BY o.value_datetime DESC LIMIT 1) AS return_date_fila, "
            + "                                 ("
            + "                                  SELECT     date_add(o.value_datetime, interval 30 day) AS return_date"
            + "                                  FROM       encounter e"
            + "                                  INNER JOIN obs o"
            + "                                  ON         e.encounter_id = o.encounter_id"
            + "                                  WHERE      p.patient_id = e.patient_id"
            + "                                  AND        e.voided = 0"
            + "                                  AND        o.voided = 0"
            + "                                  AND        e.encounter_type = ${masterCardDrugPickupEncounterType} "
            + "                                  AND        e.location_id = :location"
            + "                                  AND        o.concept_id = ${artDatePickupMasterCard}"
            + "                                  AND        o.value_datetime <= :endDate"
            + "                                  ORDER BY   o.value_datetime DESC LIMIT 1) AS return_date_master"
            + "                FROM   patient p"
            + "                WHERE  p.voided=0) e"
            + " GROUP BY e.patient_id "
            + "                HAVING  DATE_ADD(return_date, INTERVAL 59 DAY) < :endDate "
            + "             ) final "
            + "             GROUP BY final.patient_id;";

    StringSubstitutor sub = new StringSubstitutor(valuesMap);

    cd.setQuery(sub.replace(query));

    return cd;
  }

  public static SqlCohortDefinition getTranferredInPatients() {
    SqlCohortDefinition cd = new SqlCohortDefinition();

    cd.setName("Transferred in patients");
    cd.addParameter(new Parameter("onOrAfter", "Start Date", Date.class));
    cd.addParameter(new Parameter("onOrBefore", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    HivMetadata hivMetadata = new HivMetadata();

    valuesMap.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    valuesMap.put("1369", hivMetadata.getTransferredFromOtherFacilityConcept().getConceptId());
    valuesMap.put("1065", hivMetadata.getYesConcept().getConceptId());
    valuesMap.put("6276", hivMetadata.getArtStatus().getConceptId());
    valuesMap.put("6300", hivMetadata.getTypeOfPatientTransferredFrom().getConceptId());
    valuesMap.put("23891", hivMetadata.getDateOfMasterCardFileOpeningConcept().getConceptId());
    valuesMap.put("2", hivMetadata.getARTProgram().getProgramId());
    valuesMap.put(
        "29",
        hivMetadata
            .getArtTransferredFromOtherHealthFacilityWorkflowState()
            .getProgramWorkflowStateId());

    String query =
        "SELECT p.patient_id "
            + "    FROM   patient p "
            + "           JOIN encounter e ON p.patient_id = e.patient_id "
            + "           JOIN obs transf ON transf.encounter_id = e.encounter_id "
            + "           JOIN obs type  ON type.encounter_id = e.encounter_id "
            + "           JOIN obs opening ON opening.encounter_id = e.encounter_id "
            + "    WHERE  p.voided = 0 "
            + "           AND e.voided = 0 "
            + "           AND e.encounter_type = ${53} "
            + "           AND e.location_id = :location "
            + "           AND transf.voided = 0 "
            + "           AND transf.concept_id = ${1369} "
            + "           AND transf.value_coded = ${1065} "
            + "           AND type.voided = 0 "
            + "           AND type.concept_id = ${6300} "
            + "		   AND type.value_coded = ${6276} "
            + "           AND opening.voided = 0 "
            + "           AND opening.concept_id = ${23891} "
            + "           AND opening.value_datetime BETWEEN :onOrAfter AND :onOrBefore "
            + "	UNION "
            + "     "
            + "    SELECT pgEnrollment.patient_id "
            + "          FROM( "
            + "          SELECT p.patient_id, ps.start_date as pgEnrollmentDate "
            + "			FROM patient p "
            + "          JOIN patient_program pp on p.patient_id=pp.patient_id "
            + "          JOIN patient_state ps on pp.patient_program_id=ps.patient_program_id "
            + "          WHERE  pp.voided=0 "
            + "			  AND ps.voided=0 "
            + "			  AND p.voided=0 "
            + "			  AND pp.program_id=${2} "
            + "	          AND ps.state = ${29} "
            + "			  AND location_id= :location "
            + "			  AND ps.start_date BETWEEN :onOrAfter AND :onOrBefore "
            + "      ) pgEnrollment ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    cd.setQuery(stringSubstitutor.replace(query));

    return cd;
  }

  public static SqlCohortDefinition getNextScheduledPickUpDate() {
    SqlCohortDefinition cd = new SqlCohortDefinition();

    cd.setName("DSD_FR11 bullet 2 part 1");
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    HivMetadata hivMetadata = new HivMetadata();

    valuesMap.put("18", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    valuesMap.put("5096", hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId());
    String query =
        "SELECT p.patient_id "
            + "FROM patient p "
            + " WHERE p.voided = 0 "
            + " AND p.patient_id NOT IN ("
            + " SELECT p.patient_id   "
            + " FROM patient p "
            + "       INNER JOIN encounter e "
            + "               ON e.patient_id = p.patient_id "
            + "       INNER JOIN obs o "
            + "               ON o.encounter_id = e.encounter_id "
            + "WHERE  e.voided = 0 "
            + "       AND e.location_id = :location "
            + "       AND e.encounter_type = ${18} "
            + "       AND p.voided = 0 "
            + "       AND o.voided = 0 "
            + "       AND o.concept_id = ${5096} "
            + "       AND e.encounter_datetime <= :endDate "
            + "       AND o.value_datetime IS NOT NULL "
            + "GROUP  BY p.patient_id )";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    cd.setQuery(stringSubstitutor.replace(query));

    return cd;
  }

  /**
   * Patients who have any ART Pickup date registered in Ficha Recepção - Levantou ARV by reporting
   * period end date-3 months
   *
   * @return {@link DataDefinition}
   */
  public static SqlCohortDefinition getAnyArtPickup() {
    SqlCohortDefinition cd = new SqlCohortDefinition();

    cd.setName("DSD_FR11 bullet 2 part 2");
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> valuesMap = new HashMap<>();
    HivMetadata hivMetadata = new HivMetadata();
    valuesMap.put("23866", hivMetadata.getArtDatePickupMasterCard().getConceptId());
    valuesMap.put("52", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());

    String query =
        "SELECT p.patient_id "
            + "FROM patient p "
            + " WHERE p.voided = 0 "
            + " AND p.patient_id NOT IN ("
            + "     SELECT p.patient_id "
            + "FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON e.patient_id = p.patient_id "
            + "       INNER JOIN obs o "
            + "               ON o.encounter_id = e.encounter_id "
            + "WHERE  e.encounter_type = ${52} "
            + "       AND e.location_id = :location "
            + "       AND o.value_datetime <= :endDate "
            + "       AND o.concept_id = ${23866} "
            + "       AND p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND o.voided = 0 "
            + "GROUP  BY p.patient_id )";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(valuesMap);

    cd.setQuery(stringSubstitutor.replace(query));

    return cd;
  }

  // ! TO DO organize javadoc
  /** @return {@link CohortDefinition} */
  public static SqlCohortDefinition
      getPatientsWithTypeOfDispensationOnMdcInTheMostRecentFichaClinicaAndFila(
          List<Integer> dispensationTypesFila, List<Integer> dispensationTypesFichaClinica) {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName(
        "Patients With Type Of Dispensation On Mdc In The Most Recent Ficha Clinica And Fila");
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, String> map = new HashMap<>();
    HivMetadata hivMetadata = new HivMetadata();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId().toString());
    map.put("18", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId().toString());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId().toString());
    map.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId().toString());
    map.put("5096", hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId().toString());
    map.put("165174", hivMetadata.getLastRecordOfDispensingModeConcept().getConceptId().toString());
    map.put("165322", hivMetadata.getMdcState().getConceptId().toString());
    map.put("dispensationTypesFila", StringUtils.join(dispensationTypesFila, ","));
    map.put("dispensationTypesFichaClinica", StringUtils.join(dispensationTypesFichaClinica, ","));

    String query =
        "SELECT p.patient_id"
            + "      FROM   patient p "
            + "                 INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                 INNER JOIN obs otype ON otype.encounter_id = e.encounter_id "
            + "                 INNER JOIN obs ostate ON ostate.encounter_id = e.encounter_id "
            + "                 INNER JOIN ( "
            + "          SELECT last_encounter.patient_id, MAX(last_encounter.last_encounter_datetime) as last_encounter "
            + "          FROM ( "
            + "                   SELECT p.patient_id, e.encounter_datetime as last_encounter_datetime "
            + "                   FROM   patient p "
            + "                              INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                              INNER JOIN obs otype ON otype.encounter_id = e.encounter_id "
            + "                              INNER JOIN obs ostate ON ostate.encounter_id = e.encounter_id "
            + "                              INNER JOIN ( "
            + "                       SELECT p.patient_id, MAX(e.encounter_datetime) AS last_encounter "
            + "                       FROM patient p "
            + "                                INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                       WHERE p.voided = 0 "
            + "                         AND e.voided = 0 "
            + "                         AND e.location_id = :location "
            + "                         AND e.encounter_type = ${6} "
            + "                         AND e.encounter_datetime <= :endDate "
            + "                       GROUP BY p.patient_id "
            + "                   ) last_ficha ON p.patient_id = last_ficha.patient_id "
            + "                   WHERE p.voided = 0 "
            + "                     AND e.voided = 0 "
            + "                     AND otype.voided = 0 "
            + "                     AND ostate.voided = 0 "
            + "                     AND e.encounter_type = ${6} "
            + "                     AND otype.concept_id = ${165174} "
            + "                     AND otype.value_coded IS NOT NULL "
            + "                     AND ostate.concept_id = ${165322} "
            + "                     AND ostate.value_coded IS NOT NULL "
            + "                     AND otype.obs_group_id = ostate.obs_group_id "
            + "                     AND e.encounter_datetime = last_ficha.last_encounter "
            + "                     AND e.location_id = :location "
            + "       "
            + "                   UNION "
            + "       "
            + "                   SELECT p.patient_id, MAX(oo.value_datetime) as last_encounter_datetime "
            + "                   FROM   patient p "
            + "                              INNER JOIN encounter e "
            + "                                         ON e.patient_id = p.patient_id "
            + "                              INNER JOIN obs oo "
            + "                                         ON e.encounter_id = oo.encounter_id "
            + "                              INNER JOIN (SELECT p.patient_id, MAX(e.encounter_datetime) AS last_encounter "
            + "                                          FROM patient p "
            + "                                                   INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                          WHERE p.voided = 0 "
            + "                                            AND e.voided = 0 "
            + "                                            AND e.location_id = :location "
            + "                                            AND e.encounter_type = ${18} "
            + "                                            AND e.encounter_datetime <= :endDate "
            + "                                          GROUP BY p.patient_id "
            + "                   ) last_fila ON p.patient_id = last_fila.patient_id "
            + "                   WHERE "
            + "                           e.location_id = :location "
            + "                     AND e.encounter_type = ${18} "
            + "                     AND e.voided = 0 "
            + "                     AND oo.voided = 0 "
            + "                     AND oo.concept_id = ${5096} "
            + "                     AND oo.value_datetime IS NOT NULL "
            + "                     AND e.encounter_datetime = last_fila.last_encounter GROUP BY p.patient_id"
            + "               ) last_encounter "
            + "                     GROUP BY last_encounter.patient_id "
            + "      ) last_mdc ON last_mdc.patient_id = p.patient_id "
            + "      WHERE e.voided = 0 "
            + "        AND p.voided = 0 "
            + "        AND otype.voided = 0 "
            + "        AND ostate.voided = 0 "
            + "        AND e.location_id = :location"
            + "        AND ((e.encounter_type = ${18} "
            + "          AND e.encounter_dateTime <= :endDate "
            + "          AND ostate.value_datetime = last_mdc.last_encounter "
            + "          AND ostate.concept_id = ${5096}"
            + "          AND otype.concept_id = ${165174} "
            + "          AND otype.value_coded IN (${dispensationTypesFila}))"
            + "          "
            + "          OR "
            + "             (e.encounter_type = ${6} "
            + "                 AND e.encounter_datetime = last_mdc.last_encounter "
            + "                 AND e.encounter_datetime <= :endDate "
            + "                 AND otype.concept_id = ${165174} "
            + "                 AND otype.value_coded IN (${dispensationTypesFichaClinica}) "
            + "                 AND ostate.concept_id = ${165322} "
            + "                 AND otype.obs_group_id = ostate.obs_group_id "
            + "                 AND ostate.value_coded IN (${1256}, ${1257}) "
            + "                 AND NOT EXISTS (SELECT e.patient_id "
            + "                                          FROM encounter e "
            + "                                                   INNER JOIN obs o ON e.encounter_id=o.encounter_id"
            + "                                          WHERE e.voided = 0 "
            + "                                            AND o.voided = 0 "
            + "                                            AND e.encounter_type = ${18} "
            + "                                            AND o.value_datetime = last_mdc.last_encounter "
            + "                                            AND e.encounter_dateTime <= :endDate "
            + "                                            AND e.patient_id = p.patient_id "
            + "                                            AND e.location_id = :location "
            + "                                            AND o.concept_id = ${5096}) ))";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    cd.setQuery(stringSubstitutor.replace(query));

    return cd;
  }

  public static SqlCohortDefinition
      getPatientsWithTypeOfDispensationOnMdcInTheMostRecentFichaClinicaOrWithPickupOnFilaBetween(
          int lowerBounded, int upperBounded, List<Integer> dispensationTypesFichaClinica) {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName(
        "Patients With Type Of Dispensation On Mdc In The Most Recent Ficha Clinica Or With Pickup On Fila Between dates");
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, String> map = new HashMap<>();
    HivMetadata hivMetadata = new HivMetadata();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId().toString());
    map.put("18", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId().toString());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId().toString());
    map.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId().toString());
    map.put("5096", hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId().toString());
    map.put("165174", hivMetadata.getLastRecordOfDispensingModeConcept().getConceptId().toString());
    map.put("165322", hivMetadata.getMdcState().getConceptId().toString());
    map.put("23739", hivMetadata.getTypeOfDispensationConcept().getConceptId().toString());
    map.put("23720", hivMetadata.getQuarterlyConcept().getConceptId().toString());
    map.put("dispensationTypesFichaClinica", StringUtils.join(dispensationTypesFichaClinica, ","));
    map.put("lower", String.valueOf(lowerBounded));
    map.put("upper", String.valueOf(upperBounded));
    map.put("23888", hivMetadata.getSemiannualDispensation().getConceptId().toString());

    String query =
        ""
            + "SELECT p.patient_id "
            + "FROM patient p "
            + "         INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "         INNER JOIN obs otype ON otype.encounter_id = e.encounter_id "
            + "         INNER JOIN obs ostate ON ostate.encounter_id = e.encounter_id "
            + "         INNER JOIN ( SELECT last_encounter.patient_id, "
            + "                             Max(last_encounter.last_encounter_datetime) AS last_encounter "
            + "                      FROM ( SELECT p.patient_id, "
            + "                                    e.encounter_datetime AS last_encounter_datetime "
            + "                             FROM patient p "
            + "                                      INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                      INNER JOIN obs otype ON otype.encounter_id = e.encounter_id "
            + "                                      INNER JOIN obs ostate ON ostate.encounter_id = e.encounter_id "
            + "                                      INNER JOIN ( SELECT p.patient_id, "
            + "                                                          Max(e.encounter_datetime) AS last_encounter "
            + "                                                   FROM patient p "
            + "                                                            INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                                   WHERE p.voided = 0 "
            + "                                                     AND e.voided = 0 "
            + "                                                     AND e.location_id = :location "
            + "                                                     AND e.encounter_type = ${6} "
            + "                                                     AND e.encounter_datetime <= :endDate "
            + "                                                   GROUP BY p.patient_id ) last_ficha ON p.patient_id = last_ficha.patient_id "
            + "                             WHERE p.voided = 0 "
            + "                               AND e.voided = 0 "
            + "                               AND otype.voided = 0 "
            + "                               AND ostate.voided = 0 "
            + "                               AND e.encounter_type = ${6} "
            + "                               AND otype.concept_id = ${165174} "
            + "                               AND otype.value_coded IS NOT NULL "
            + "                               AND ostate.concept_id = ${165322} "
            + "                               AND ostate.value_coded IS NOT NULL "
            + "                               AND otype.obs_group_id = ostate.obs_group_id "
            + "                               AND e.encounter_datetime = last_ficha.last_encounter "
            + "                               AND e.location_id = :location "
            + "                             UNION "
            + "                             SELECT p.patient_id, "
            + "                                    MAX(o.value_datetime) AS last_encounter_datetime "
            + "                             FROM patient p "
            + "                                   INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                   INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "                                   INNER JOIN ( SELECT p.patient_id, "
            + "                                                       Max(e.encounter_datetime) consultation_date "
            + "                                                FROM patient p "
            + "                                                       INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                                       WHERE e.encounter_type = ${18} "
            + "                                                       AND e.location_id = :location "
            + "                                                       AND e.encounter_datetime <= :endDate "
            + "                                                       AND e.voided = 0 "
            + "                                                       AND p.voided = 0 "
            + "                                                       GROUP BY p.patient_id ) recent_clinical ON recent_clinical.patient_id = p.patient_id "
            + "                             WHERE e.encounter_datetime = recent_clinical.consultation_date "
            + "                               AND e.encounter_type = ${18} "
            + "                               AND e.location_id = :location "
            + "                               AND o.concept_id = ${5096} "
            + "                               AND o.value_datetime IS NOT NULL "
            + "                               AND p.voided = 0 "
            + "                               AND e.voided = 0 "
            + "                               AND o.voided = 0 "
            + "                             GROUP BY p.patient_id "
            + "                             UNION "
            + "                             SELECT p.patient_id, "
            + "                                    e.encounter_datetime AS last_encounter_datetime "
            + "                             FROM patient p "
            + "                                      INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                                      INNER JOIN obs o1 ON o1.encounter_id = e.encounter_id "
            + "                                      INNER JOIN ( SELECT p.patient_id, "
            + "                                                          Max(e.encounter_datetime) AS last_encounter "
            + "                                                   FROM patient p "
            + "                                                            INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                                                   WHERE p.voided = 0 "
            + "                                                     AND e.voided = 0 "
            + "                                                     AND e.location_id = :location "
            + "                                                     AND e.encounter_type = ${6} "
            + "                                                     AND e.encounter_datetime <= :endDate "
            + "                                                   GROUP BY p.patient_id ) last_ficha ON p.patient_id = last_ficha.patient_id "
            + "                             WHERE p.voided = 0 "
            + "                               AND e.voided = 0 "
            + "                               AND o1.voided = 0 "
            + "                               AND e.encounter_type = ${6} "
            + "                               AND o1.concept_id = ${23739} "
            + "                               AND o1.value_coded IS NOT NULL "
            + "                               AND e.encounter_datetime = last_ficha.last_encounter "
            + "                               AND e.location_id = :location ) last_encounter "
            + "                      GROUP BY last_encounter.patient_id ) last_mdc ON last_mdc.patient_id = p.patient_id "
            + "WHERE e.voided = 0 "
            + "  AND p.voided = 0 "
            + "  AND otype.voided = 0 "
            + "  AND ostate.voided = 0 "
            + "  AND e.location_id = :location "
            + "  AND (( e.encounter_type = ${18} "
            + "    AND otype.concept_id = ${5096} "
            + "    AND otype.value_datetime = last_mdc.last_encounter "
            + "    AND Datediff(otype.value_datetime, (SELECT max(e.encounter_datetime)"
            + "                                        FROM  encounter e"
            + "                                        INNER JOIN obs o ON e.encounter_id=o.encounter_id"
            + "                                        WHERE e.voided = 0 "
            + "                                        AND e.encounter_type = 18 "
            + "                                        AND o.value_datetime =last_mdc.last_encounter"
            + "                                        AND o.concept_id = 5096"
            + "                                        AND e.encounter_datetime <= '2022-09-20' "
            + "                                        AND e.patient_id = p.patient_id "
            + "                                        AND e.location_id = 399"
            + "                                        GROUP BY e.patient_id limit 1)) >= ${lower} "
            + "    AND Datediff(otype.value_datetime, (SELECT max(e.encounter_datetime)"
            + "                                        FROM  encounter e"
            + "                                        INNER JOIN obs o ON e.encounter_id=o.encounter_id"
            + "                                        WHERE e.voided = 0 "
            + "                                        AND e.encounter_type = 18 "
            + "                                        AND o.value_datetime =last_mdc.last_encounter"
            + "                                        AND o.concept_id = 5096"
            + "                                        AND e.encounter_datetime <= '2022-09-20' "
            + "                                        AND e.patient_id = p.patient_id "
            + "                                        AND e.location_id = 399"
            + "                                        GROUP BY e.patient_id limit 1)) <= ${upper} ) "
            + "    OR ( e.encounter_type = ${6} "
            + "        AND e.encounter_datetime = last_mdc.last_encounter "
            + "        AND otype.concept_id = ${165174} "
            + "        AND e.encounter_datetime <= :endDate"
            + "        AND otype.value_coded IN (${dispensationTypesFichaClinica}) "
            + "        AND ostate.concept_id = ${165322} "
            + "        AND otype.obs_group_id = ostate.obs_group_id "
            + "        AND ostate.value_coded IN (${1256}, ${1257}) "
            + "        AND NOT EXISTS ( SELECT e.patient_id "
            + "                                  FROM encounter e "
            + "                                           INNER JOIN obs o ON e.encounter_id=o.encounter_id "
            + "                                  where e.voided = 0 "
            + "                                    AND o.voided = 0 "
            + "                                    AND e.encounter_type = ${18} "
            + "                                    AND o.value_datetime =last_mdc.last_encounter "
            + "                                    AND e.patient_id = p.patient_id "
            + "                                    AND e.location_id = :location "
            + "                                    AND o.concept_id = ${5096}) ) ";
    String sql = ");";
    // In case Dispensa Trimestral
    if (lowerBounded == 83 && upperBounded == 97)
      sql =
          "    OR ( e.encounter_type = 6 "
              + "        AND e.encounter_datetime = last_mdc.last_encounter "
              + "        AND otype.concept_id = ${23739} "
              + "        AND e.encounter_datetime <= :endDate"
              + "        AND otype.value_coded   IN (${23720}) "
              + "        AND NOT EXISTS ( SELECT e.patient_id "
              + "                                  FROM encounter e "
              + "                                           INNER JOIN obs o ON e.encounter_id=o.encounter_id"
              + "                                  WHERE e.voided = 0 "
              + "                                    AND o.voided = 0 "
              + "                                    AND e.encounter_type = ${18} "
              + "                                    AND o.value_datetime =last_mdc.last_encounter "
              + "                                    AND e.patient_id = p.patient_id "
              + "                                    AND e.location_id = :location "
              + "                                    AND o.concept_id = ${5096} ) ) );";
    else if (lowerBounded == 173 && upperBounded == 187) // Incase dispensa semestral
    sql =
          "    OR ( e.encounter_type = 6 "
              + "        AND e.encounter_datetime = last_mdc.last_encounter "
              + "        AND otype.concept_id = ${23739} "
              + "        AND e.encounter_datetime <= :endDate"
              + "        AND otype.value_coded   IN (${23888}) "
              + "        AND NOT EXISTS ( SELECT e.patient_id "
              + "                                  FROM encounter e "
              + "                                           INNER JOIN obs o ON e.encounter_id=o.encounter_id"
              + "                                  WHERE e.voided = 0 "
              + "                                    AND o.voided = 0 "
              + "                                    AND e.encounter_type = ${18} "
              + "                                    AND o.value_datetime =last_mdc.last_encounter "
              + "                                    AND e.patient_id = p.patient_id "
              + "                                    AND e.location_id = :location "
              + "                                    AND o.concept_id = ${5096} ) ) );";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    query = query + sql;
    cd.setQuery(stringSubstitutor.replace(query));

    return cd;
  }

  public static SqlCohortDefinition
      getPatientsWithTypeOfDispensationOnMdcInTheMostRecentFichaClinica(
          List<Integer> dispensationTypesFichaClinica) {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("Patients With Type Of Dispensation On Mdc In The Most Recent Ficha Clinica");
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, String> map = new HashMap<>();
    HivMetadata hivMetadata = new HivMetadata();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId().toString());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId().toString());
    map.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId().toString());
    map.put("165174", hivMetadata.getLastRecordOfDispensingModeConcept().getConceptId().toString());
    map.put("165322", hivMetadata.getMdcState().getConceptId().toString());
    map.put("dispensationTypesFichaClinica", StringUtils.join(dispensationTypesFichaClinica, ","));

    String query =
        ""
            + "SELECT p.patient_id "
            + "FROM   patient p "
            + "           INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "           INNER JOIN obs otype ON otype.encounter_id = e.encounter_id "
            + "           INNER JOIN obs ostate ON ostate.encounter_id = e.encounter_id "
            + "           INNER JOIN ( "
            + "    SELECT last_encounter.patient_id, MAX(last_encounter.last_encounter_datetime) as last_encounter "
            + "    FROM ( "
            + "             SELECT p.patient_id, e.encounter_datetime as last_encounter_datetime "
            + "             FROM   patient p "
            + "                        INNER JOIN encounter e ON p.patient_id = e.patient_id "
            + "                        INNER JOIN obs otype ON otype.encounter_id = e.encounter_id "
            + "                        INNER JOIN obs ostate ON ostate.encounter_id = e.encounter_id "
            + "                        INNER JOIN ( "
            + "                 SELECT p.patient_id, MAX(e.encounter_datetime) AS last_encounter "
            + "                 FROM patient p "
            + "                          INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                 WHERE p.voided = 0 "
            + "                   AND e.voided = 0 "
            + "                   AND e.location_id = :location "
            + "                   AND e.encounter_type = ${6} "
            + "                   AND e.encounter_datetime <= :endDate "
            + "                 GROUP BY p.patient_id "
            + "             ) last_ficha ON p.patient_id = last_ficha.patient_id "
            + "             WHERE p.voided = 0 "
            + "               AND e.voided = 0 "
            + "               AND otype.voided = 0 "
            + "               AND ostate.voided = 0 "
            + "               AND e.encounter_type = ${6} "
            + "               AND otype.concept_id = ${165174} "
            + "               AND otype.value_coded IS NOT NULL "
            + "               AND ostate.concept_id = ${165322} "
            + "               AND ostate.value_coded IS NOT NULL "
            + "               AND otype.obs_group_id = ostate.obs_group_id "
            + "               AND e.encounter_datetime = last_ficha.last_encounter "
            + "               AND e.location_id = :location "
            + "         ) last_encounter "
            + "               GROUP BY last_encounter.patient_id "
            + ") last_mdc ON last_mdc.patient_id = p.patient_id "
            + "WHERE e.voided = 0 "
            + "  AND p.voided = 0 "
            + "  AND otype.voided = 0 "
            + "  AND ostate.voided = 0 "
            + "  AND e.location_id = :location "
            + "  AND ((e.encounter_type = ${6} "
            + "           AND e.encounter_datetime = last_mdc.last_encounter "
            + "           AND otype.concept_id = ${165174} "
            + "           AND otype.value_coded IN (${dispensationTypesFichaClinica}) "
            + "           AND ostate.concept_id = ${165322} "
            + "           AND otype.obs_group_id = ostate.obs_group_id "
            + "           AND ostate.value_coded IN (${1256}, ${1257}) "
            + "           ) "
            + "    )";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    cd.setQuery(stringSubstitutor.replace(query));

    return cd;
  }

  public static SqlCohortDefinition
      getPatientsWithTypeOfDispensationOnMdcInTheMostRecentFichaClinica(
          List<Integer> dispensationTypesFichaClinica, int lowerBounded, int upperBounded) {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName(
        "Patients With Type Of Dispensation On Mdc In The Most Recent Ficha Clinica lowerBound and UpperBound");
    cd.addParameter(new Parameter("endDate", "endDate", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    Map<String, String> map = new HashMap<>();
    HivMetadata hivMetadata = new HivMetadata();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId().toString());
    map.put("1256", hivMetadata.getStartDrugs().getConceptId().toString());
    map.put("1257", hivMetadata.getContinueRegimenConcept().getConceptId().toString());
    map.put("165174", hivMetadata.getLastRecordOfDispensingModeConcept().getConceptId().toString());
    map.put("165322", hivMetadata.getMdcState().getConceptId().toString());
    map.put("1410", hivMetadata.getReturnVisitDateConcept().getConceptId().toString());
    map.put("dispensationTypesFichaClinica", StringUtils.join(dispensationTypesFichaClinica, ","));
    map.put("lowerBounded", String.valueOf(lowerBounded));
    map.put("upperBounded", String.valueOf(upperBounded));

    String query =
        " SELECT p.patient_id "
            + " FROM   patient p "
            + " INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + " INNER JOIN obs otype ON otype.encounter_id = e.encounter_id "
            + " INNER JOIN obs ostate ON ostate.encounter_id = e.encounter_id "
            + " INNER JOIN ( "
            + " SELECT p.patient_id, MAX(e.encounter_datetime) AS last_encounter "
            + " FROM patient p "
            + " INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + " WHERE p.voided = 0 "
            + " AND e.voided = 0 "
            + " AND e.location_id = :location "
            + " AND e.encounter_type = ${6} "
            + " AND e.encounter_datetime <= :endDate "
            + " GROUP BY p.patient_id "
            + " ) last_mdc ON last_mdc.patient_id = p.patient_id "
            + " WHERE e.voided = 0 "
            + " AND p.voided = 0 "
            + " AND otype.voided = 0 "
            + " AND ostate.voided = 0 "
            + " AND e.location_id = :location "
            + " AND e.encounter_type = ${6} "
            + " AND e.encounter_datetime = last_mdc.last_encounter "
            + " AND (( otype.concept_id = ${165174} "
            + " AND otype.value_coded IN (${dispensationTypesFichaClinica}) "
            + " AND ostate.concept_id = ${165322} "
            + " AND otype.obs_group_id = ostate.obs_group_id "
            + " AND ostate.value_coded IN (${1256}, ${1257}) ) OR (  "
            + " otype.concept_id = ${1410}"
            + " AND Datediff(otype.value_datetime, last_mdc.last_encounter) >= ${lowerBounded}"
            + " AND Datediff(otype.value_datetime, last_mdc.last_encounter) <= ${upperBounded}))"
            + " GROUP BY p.patient_id;";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    cd.setQuery(stringSubstitutor.replace(query));

    return cd;
  }
}
