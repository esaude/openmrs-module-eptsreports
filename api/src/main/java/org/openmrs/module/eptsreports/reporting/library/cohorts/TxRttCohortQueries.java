/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.eptsreports.reporting.library.cohorts;

import static org.openmrs.module.reporting.evaluation.parameter.Mapped.mapStraightThrough;

import java.util.*;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.library.queries.TxRttQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TxRttCohortQueries {

  private HivMetadata hivMetadata;

  private GenericCohortQueries genericCohortQueries;

  private TxCurrCohortQueries txCurrCohortQueries;

  private CommonCohortQueries commonCohortQueries;

  private final String DEFAULT_MAPPING =
      "startDate=${startDate},endDate=${endDate},location=${location}";

  @Autowired
  public TxRttCohortQueries(
      HivMetadata hivMetadata,
      GenericCohortQueries genericCohortQueries,
      TxCurrCohortQueries txCurrCohortQueries,
      CommonCohortQueries commonCohortQueries) {
    this.hivMetadata = hivMetadata;
    this.genericCohortQueries = genericCohortQueries;
    this.txCurrCohortQueries = txCurrCohortQueries;
    this.commonCohortQueries = commonCohortQueries;
  }

  /**
   * <b>Technical Specs</b>
   *
   * <blockquote>
   *
   * All patients (adults and children) who at ANY clinical contact (clinical consultation or drugs
   * pick up <b>[Encounter Type Ids = 6,9,${}1}8,52]</b>) registered during the reporting period had
   * a delay greater than 28/30 days from the last scheduled/expected, which may have happened
   * during or prior to the reporting period period
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getAllPatientsWhoMissedAppointmentBy28Or30DaysButLaterHadVisit() {
    return genericCohortQueries.generalSql(
        "Having visit 30 days later",
        TxRttQueries.getAllPatientsWhoMissedPreviousAppointmentBy28Days(
            hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId(),
            hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId(),
            hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId(),
            hivMetadata.getReturnVisitDateConcept().getConceptId(),
            hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId(),
            hivMetadata.getArtDatePickupMasterCard().getConceptId()));
  }

  /**
   * The TX _RTT indicator reports the number of ART patients with no clinical contact (or ARV drug
   * pick-up) for greater than 28 days since their last expected contact (who experienced an
   * interruption in Treatment – IIT) during any previous reporting period, who successfully
   * restarted ARVs within the reporting period and remained on treatment until the end of reporting
   * period.
   *
   * <ul>
   *   <li>Select all patients patients who initiated ART by end of previous reporting period
   *       (startDate -1 day) following the criterias defined in the common queries:
   *   <li>Filter all patients who experienced IIT by end of previous reporting period (startDate -1
   *       day) following the criterias defined in the common queries:
   *       <ul>
   *         <li>And Exclude all IIT patients who are transferred out by previous reporting period,
   *             following the criterias defined in the common queries:
   *       </ul>
   *   <li>Filter all patients who returned to the treatment during the reporting period following
   *       the criterias below: {@link
   *       TxRttCohortQueries#getPatientsReturnedTreatmentDuringReportingPeriod() }
   *   <li>Filter all patients who remained on TX CURR by the end of the reporting period.
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getRTTComposition() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    cd.addSearch(
        "initiatedPreviousPeriod",
        EptsReportUtils.map(
            genericCohortQueries.getStartedArtBeforeDate(false),
            "onOrBefore=${startDate-1d},location=${location}"));

    cd.addSearch(
        "LTFU",
        EptsReportUtils.map(
            getITTOrLTFUPatients(28), "onOrBefore=${startDate-1d},location=${location}"));

    cd.addSearch(
        "returned",
        EptsReportUtils.map(getPatientsReturnedTreatmentDuringReportingPeriod(), DEFAULT_MAPPING));

    cd.addSearch(
        "txcurr",
        EptsReportUtils.map(
            txCurrCohortQueries.getTxCurrCompositionCohort("txcurr", true),
            "onOrBefore=${endDate},location=${location}"));

    cd.addSearch(
        "transferredout",
        EptsReportUtils.map(
            commonCohortQueries.getMohTransferredOutPatientsByEndOfPeriod(),
            "onOrBefore=${startDate-1d},location=${location}"));

    cd.setCompositionString(
        "initiatedPreviousPeriod AND returned AND txcurr AND (LTFU AND NOT transferredout)");

    return cd;
  }

  /**
   * Filter all patients who returned to the treatment during the reporting period following the
   * criterias below:
   *
   * <ul>
   *   <li>At least one Ficha Clinica registered during the reporting period (Encounter Type 6 or 9,
   *       and encounter_datetime>= startDate and <=endDate) OR
   *   <li>At least one Drugs Pick up registered in FILA during the reporting period (Encounter Type
   *       ${}1}8, and encounter_datetime>= startDate and <=endDate) OR
   *   <li>At least one Drugs Pick up registered in MasterCard-Recepção/Levantoy ARV, during the
   *       reporting period (Encounter Type 52, and “Levantou ARV”- concept ID 23865”= “Yes”
   *       (concept id 1065) and “Data de Levantamento” (concept Id 23866 value_datetime>= startDate
   *       and <=endDate)
   * </ul>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsReturnedTreatmentDuringReportingPeriod() {

    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "location", Location.class));

    CohortDefinition ficha =
        getPatientsWithFilaOrFichaOrMasterCardPickup(
            Arrays.asList(
                hivMetadata.getAdultoSeguimentoEncounterType(),
                hivMetadata.getPediatriaSeguimentoEncounterType()));

    CohortDefinition fila =
        getPatientsWithFilaOrFichaOrMasterCardPickup(
            Arrays.asList(hivMetadata.getARVPharmaciaEncounterType()));

    CohortDefinition drugPickUp =
        getPatientsWithFilaOrFichaOrMasterCardPickup(
            Arrays.asList(hivMetadata.getMasterCardDrugPickupEncounterType()),
            hivMetadata.getArtPickupConcept(),
            hivMetadata.getYesConcept(),
            hivMetadata.getArtDatePickupMasterCard());

    cd.addSearch("ficha", EptsReportUtils.map(ficha, DEFAULT_MAPPING));

    cd.addSearch("fila", EptsReportUtils.map(fila, DEFAULT_MAPPING));

    cd.addSearch("drugPickUp", EptsReportUtils.map(drugPickUp, DEFAULT_MAPPING));

    cd.setCompositionString("ficha OR fila OR drugPickUp");

    return cd;
  }

  private CohortDefinition getPatientsWithFilaOrFichaOrMasterCardPickup(
      List<EncounterType> encounterTypes, Concept... conceptIds) {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    StringBuilder builder = new StringBuilder();

    builder.append(" SELECT p.patient_id ");
    builder.append(" FROM patient p ");
    builder.append("    INNER JOIN encounter e  ");
    builder.append("        on e.patient_id = p.patient_id ");
    if (conceptIds.length == 3) {
      builder.append("  INNER JOIN obs o1");
      builder.append("      on e.encounter_id = o1.encounter_id ");
      builder.append("  INNER JOIN obs o2 ");
      builder.append("      on e.encounter_id = o2.encounter_id ");
    }
    builder.append(" WHERE  ");
    builder.append("    p.voided = 0  ");
    builder.append(" AND   e.voided = 0  ");
    if (encounterTypes.size() > 1) {
      builder.append("   AND e.encounter_type IN (%s,%s) ");
    } else {
      builder.append("   AND e.encounter_type = %s ");
    }
    if (conceptIds.length == 3) {
      builder.append(" AND o1.voided= 0 ");
      builder.append(" AND o2.voided= 0 ");
      builder.append(" AND (o1.concept_id = %s AND o1.value_coded = %s) ");
      builder.append(
          " AND (o2.concept_id = %s AND o2.value_datetime BETWEEN :startDate AND :endDate) ");
    } else {
      builder.append(" AND e.encounter_datetime  ");
      builder.append("        BETWEEN :startDate AND :endDate ");
    }
    builder.append("   AND e.location_id = :location ");
    String query = builder.toString();

    String formattedQuery = null;

    if (conceptIds.length == 3) {
      formattedQuery =
          String.format(
              query,
              encounterTypes.get(0).getEncounterTypeId(),
              conceptIds[0].getConceptId(),
              conceptIds[1].getConceptId(),
              conceptIds[2].getConceptId());
    } else {
      formattedQuery =
          encounterTypes.size() > 1
              ? String.format(
                  query,
                  encounterTypes.get(0).getEncounterTypeId(),
                  encounterTypes.get(1).getEncounterTypeId())
              : String.format(query, encounterTypes.get(0).getEncounterTypeId());
    }

    cd.setQuery(formattedQuery);

    return cd;
  }

  public CohortDefinition getITTOrLTFUPatients(int numDays) {
    CompositionCohortDefinition definition = new CompositionCohortDefinition();

    definition.addSearch(
        "31",
        mapStraightThrough(
            txCurrCohortQueries.getPatientHavingLastScheduledDrugPickupDateDaysBeforeEndDate(
                numDays)));

    definition.addSearch("32", mapStraightThrough(getSecondPartFromITT()));

    definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));
    definition.setCompositionString("31 OR  32");

    return definition;
  }

  public CohortDefinition getSecondPartFromITT() {

    SqlCohortDefinition definition = new SqlCohortDefinition();
    definition.setName("patientWithoutScheduledDrugPickupDateMasterCardAmdArtPickup");
    definition.addParameter(new Parameter("onOrBefore", "onOrBefore", Date.class));
    definition.addParameter(new Parameter("location", "location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    map.put("18", hivMetadata.getARVPharmaciaEncounterType().getEncounterTypeId());
    map.put("52", hivMetadata.getMasterCardDrugPickupEncounterType().getEncounterTypeId());

    map.put("1410", hivMetadata.getReturnVisitDateConcept().getConceptId());
    map.put("5096", hivMetadata.getReturnVisitDateForArvDrugConcept().getConceptId());
    map.put("23866", hivMetadata.getArtDatePickupMasterCard().getConceptId());

    String query =
        "SELECT final.patient_id "
            + " FROM( "
            + " SELECT total.patient_id FROM "
            + "    ( "
            + "        SELECT     pat1.patient_id, Max(enc1.encounter_datetime) AS encounter_datetime "
            + "        FROM  patient pat1  "
            + "            INNER JOIN encounter enc1  "
            + "                ON         pat1.patient_id=enc1.patient_id  "
            + "             "
            + "        WHERE enc1.encounter_datetime<= :onOrBefore "
            + "            AND pat1.voided=0  "
            + "            AND enc1.voided=0  "
            + "            AND enc1.location_id= :location  "
            + "            AND enc1.encounter_type IN (${6},${9}) "
            + "        GROUP BY   pat1.patient_id "
            + "    ) AS total "
            + "    LEFT JOIN "
            + "            ( "
            + "                SELECT p.patient_id  "
            + "                FROM patient p "
            + "                    INNER JOIN encounter e "
            + "                        ON e.patient_id =p.patient_id "
            + "                    INNER JOIN obs o "
            + "                        ON o.encounter_id =e.encounter_id "
            + "                WHERE "
            + "                    p.voided = 0 "
            + "                    AND e.voided = 0 "
            + "                    AND o.voided = 0 "
            + "                    AND e.encounter_type IN (${6},${9}) "
            + "                    AND encounter_datetime<= :onOrBefore "
            + "                    AND e.location_id= :location  "
            + "                    AND o.concept_id = ${1410} "
            + "                UNION "
            + "                SELECT p.patient_id  "
            + "                FROM patient p "
            + "                    INNER JOIN encounter e "
            + "                        ON e.patient_id =p.patient_id "
            + "                    INNER JOIN obs o "
            + "                        ON o.encounter_id =e.encounter_id "
            + "                WHERE "
            + "                    p.voided = 0 "
            + "                    AND e.voided = 0 "
            + "                    AND o.voided = 0 "
            + "                    AND e.encounter_type IN (${6},${9}) "
            + "                    AND encounter_datetime<= :onOrBefore "
            + "                    AND e.location_id= :location  "
            + "                    AND o.concept_id = ${1410} "
            + "                    AND o.value_datetime IS NOT NULL "
            + "            ) right1 "
            + "    ON total.patient_id = right1.patient_id   "
            + "WHERE  "
            + "    right1.patient_id IS NULL "
            + "UNION "
            + "SELECT total.patient_id FROM "
            + "    ( "
            + "        SELECT     pat1.patient_id, Max(enc1.encounter_datetime) AS encounter_datetime "
            + "        FROM  patient pat1  "
            + "            INNER JOIN encounter enc1  "
            + "                ON         pat1.patient_id=enc1.patient_id  "
            + "             "
            + "        WHERE enc1.encounter_datetime<= :onOrBefore "
            + "            AND pat1.voided=0  "
            + "            AND enc1.voided=0  "
            + "            AND enc1.location_id= :location  "
            + "            AND enc1.encounter_type IN (${18}) "
            + "        GROUP BY   pat1.patient_id "
            + "    ) AS total "
            + "    LEFT JOIN "
            + "            ( "
            + "                SELECT p.patient_id  "
            + "                FROM patient p "
            + "                    INNER JOIN encounter e "
            + "                        ON e.patient_id =p.patient_id "
            + "                    INNER JOIN obs o "
            + "                        ON o.encounter_id =e.encounter_id "
            + "                WHERE "
            + "                    p.voided = 0 "
            + "                    AND e.voided = 0 "
            + "                    AND o.voided = 0 "
            + "                    AND e.encounter_type IN (${18}) "
            + "                    AND encounter_datetime<= :onOrBefore "
            + "                    AND e.location_id= :location  "
            + "                    AND o.concept_id = ${5096} "
            + "                UNION "
            + "                SELECT p.patient_id  "
            + "                FROM patient p "
            + "                    INNER JOIN encounter e "
            + "                        ON e.patient_id =p.patient_id "
            + "                    INNER JOIN obs o "
            + "                        ON o.encounter_id =e.encounter_id "
            + "                WHERE "
            + "                    p.voided = 0 "
            + "                    AND e.voided = 0 "
            + "                    AND o.voided = 0 "
            + "                    AND e.encounter_type IN (${18}) "
            + "                    AND encounter_datetime<= :onOrBefore "
            + "                    AND e.location_id= :location  "
            + "                    AND o.concept_id = ${5096} "
            + "                    AND o.value_datetime IS NOT NULL "
            + "            ) right1 "
            + "    ON total.patient_id = right1.patient_id   "
            + " WHERE  "
            + "    right1.patient_id IS NULL "
            + " ) AS final  "
            + " WHERE final.patient_id NOT  IN( "
            + "    SELECT p.patient_id "
            + "    FROM  patient p "
            + "        INNER JOIN encounter e  "
            + "            ON e.patient_id = p.patient_id "
            + "        INNER JOIN obs o  "
            + "            ON e.encounter_id = o.encounter_id "
            + "     "
            + "    WHERE p.voided = 0 "
            + "        AND e.voided = 0 "
            + "        AND o.voided = 0 "
            + "        AND e.encounter_type = ${52} "
            + "        AND e.location_id = :location "
            + "        AND o.value_datetime <= :onOrBefore "
            + "        AND o.concept_id = ${23866} "
            + "        )";
    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);

    definition.setQuery(stringSubstitutor.replace(query));

    return definition;
  }
}
