/*
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.eptsreports.reporting.library.cohorts;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.eptsreports.metadata.CommonMetadata;
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.mq.BreastfeedingPregnantCalculation4MQ;
import org.openmrs.module.eptsreports.reporting.calculation.pvls.BreastfeedingPregnantCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationCohortDefinition;
import org.openmrs.module.eptsreports.reporting.library.queries.CommonQueries;
import org.openmrs.module.eptsreports.reporting.library.queries.ViralLoadQueries;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportConstants.PregnantOrBreastfeedingWomen;
import org.openmrs.module.eptsreports.reporting.utils.EptsReportUtils;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.CompositionCohortDefinition;
import org.openmrs.module.reporting.cohort.definition.SqlCohortDefinition;
import org.openmrs.module.reporting.definition.library.DocumentedDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Defines all of the TxPvls Cohort Definition instances we want to expose for EPTS */
@Component
public class TxPvlsCohortQueries {

  private HivCohortQueries hivCohortQueries;

  private HivMetadata hivMetadata;

  private CommonMetadata commonMetadata;
  private CommonQueries commonQueries;

  @Autowired
  public TxPvlsCohortQueries(HivMetadata hivMetadata, CommonMetadata commonMetadata) {
    this.hivCohortQueries = hivCohortQueries;
    this.hivMetadata = hivMetadata;
    this.commonQueries = commonQueries;
    this.commonMetadata = commonMetadata;
  }

  /**
   * <b>Description</b> On ART more than 3 months <blockqoute> Patients who have NOT been on ART for
   * 3 months based on the ART initiation date and date of last viral load registered </blockqoute>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoAreMoreThan3MonthsOnArt(
      List<EncounterType> encounterTypeList) {

    SqlCohortDefinition cd = new SqlCohortDefinition();
    cd.setName("ART for less than 3 months");
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    String artStart = commonQueries.getARTStartDate(true);
    String viralLoad = ViralLoadQueries.getPatientsHavingViralLoadInLast12Months(encounterTypeList);

    String query =
        "SELECT vl.patient_id FROM ( "
            + "                                   SELECT patient_id, MAX(vl_date) vl_date"
            + "                                   FROM ( "
            + viralLoad
            + "                                        ) viral GROUP BY viral.patient_id"
            + "                                  ) vl "
            + "     INNER JOIN ("
            + artStart
            + ") art ON art.patient_id = vl.patient_id "
            + "WHERE TIMESTAMPDIFF(DAY, art.first_pickup, vl.vl_date) >= 90";

    cd.setQuery(query);
    return cd;
  }

  /**
   * <b>Description</b>Breast feeding women with viral load suppression and on ART for more than 3
   * months
   *
   * @return CohortDefinition
   */
  @DocumentedDefinition(value = "breastfeedingWomenWithViralSuppression")
  public CohortDefinition getBreastfeedingWomenWhoHaveViralSuppression() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Breastfeeding with viral suppression");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "breastfeeding",
        EptsReportUtils.map(getBreastfeedingPatients(), "endDate=${endDate},location=${location}"));

    cd.addSearch(
        "suppression",
        EptsReportUtils.map(
            getPatientsWithViralLoadSuppressionWhoAreOnArtMoreThan3Months(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.setCompositionString("breastfeeding AND suppression");

    return cd;
  }

  /**
   * <b>Description</b> Breast feeding women with viral load suppression
   *
   * @return CohortDefinition
   */
  @DocumentedDefinition(value = "breastfeedingWomenWithViralLoadResults")
  public CohortDefinition getBreastfeedingWomenWhoHaveViralLoadResults() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Breastfeeding with viral results");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));

    cd.addSearch(
        "breastfeeding",
        EptsReportUtils.map(getBreastfeedingPatients(), "endDate=${endDate},location=${location}"));

    cd.addSearch(
        "results",
        EptsReportUtils.map(
            getPatientsWithViralLoadResultsAndOnArtForMoreThan3Months(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.setCompositionString("breastfeeding AND results");

    return cd;
  }

  /**
   * <b>Description</b> Viral load suppression
   *
   * <blockquote>
   *
   * Patients with viral suppression of <1000 in the last 12 months excluding dead, LTFU,
   * transferred out, stopped ART
   *
   * </blockquote>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWithViralLoadSuppressionWhoAreOnArtMoreThan3Months() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    cd.addSearch(
        "supp",
        EptsReportUtils.map(
            hivCohortQueries.getPatientsWithSuppressedViralLoadWithin12Months(), mappings));
    cd.addSearch(
        "onArtLongEnough",
        EptsReportUtils.map(
            getPatientsWhoAreMoreThan3MonthsOnArt(
                Arrays.asList(
                    hivMetadata.getMisauLaboratorioEncounterType(),
                    hivMetadata.getAdultoSeguimentoEncounterType(),
                    hivMetadata.getPediatriaSeguimentoEncounterType(),
                    hivMetadata.getMasterCardEncounterType(),
                    hivMetadata.getFsrEncounterType())),
            "endDate=${endDate},location=${location}"));
    cd.setCompositionString("supp AND onArtLongEnough");
    return cd;
  }

  /**
   * <b>Description</b> Viral load results composition
   *
   * <blockquote>
   *
   * Patients with viral results recorded in the last 12 months excluding dead, LTFU, transferred
   * out, stopped ARTtxNewCohortQueries Only filter out patients who are on routine
   *
   * </blockquote>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWithViralLoadResultsAndOnArtForMoreThan3Months() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    cd.addSearch(
        "results",
        EptsReportUtils.map(hivCohortQueries.getPatientsViralLoadWithin12Months(), mappings));
    cd.addSearch(
        "onArtLongEnough",
        EptsReportUtils.map(
            getPatientsWhoAreMoreThan3MonthsOnArt(
                Arrays.asList(
                    hivMetadata.getMisauLaboratorioEncounterType(),
                    hivMetadata.getAdultoSeguimentoEncounterType(),
                    hivMetadata.getPediatriaSeguimentoEncounterType(),
                    hivMetadata.getMasterCardEncounterType(),
                    hivMetadata.getFsrEncounterType())),
            "endDate=${endDate},location=${location}"));
    cd.setCompositionString("results AND onArtLongEnough");
    return cd;
  }
  /**
   * <b>Description</b> Viral load results and on routine composition
   *
   * <blockquote>
   *
   * Patients with viral results recorded in the last 12 months excluding dead, LTFU, transferred
   * out, stopped ARTtxNewCohortQueries Only filter out patients who are on routine
   *
   * </blockquote>
   *
   * @return Cohort
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWithViralLoadResultsAndOnRoutine() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    cd.addSearch(
        "results",
        EptsReportUtils.map(hivCohortQueries.getPatientsViralLoadWithin12Months(), mappings));
    cd.addSearch(
        "onArtLongEnough",
        EptsReportUtils.map(
            getPatientsWhoAreMoreThan3MonthsOnArt(
                Arrays.asList(
                    hivMetadata.getMisauLaboratorioEncounterType(),
                    hivMetadata.getAdultoSeguimentoEncounterType(),
                    hivMetadata.getPediatriaSeguimentoEncounterType(),
                    hivMetadata.getMasterCardEncounterType(),
                    hivMetadata.getFsrEncounterType())),
            "endDate=${endDate},location=${location}"));
    cd.addSearch("Routine", EptsReportUtils.map(getPatientsWhoAreOnRoutine(), mappings));
    cd.setCompositionString("(results AND onArtLongEnough) AND Routine");
    return cd;
  }

  /**
   * <b>Description</b Viral load results and on target
   *
   * <blockquote>
   *
   * Patients with viral results recorded in the last 12 months excluding dead, LTFU, transferred
   * out, stopped ARTtxNewCohortQueries Only filter out patients who are on target
   *
   * </blockquote>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWithViralLoadResultsAndOnTarget() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    cd.addSearch(
        "results",
        EptsReportUtils.map(hivCohortQueries.getPatientsViralLoadWithin12Months(), mappings));
    cd.addSearch(
        "onArtLongEnough",
        EptsReportUtils.map(
            getPatientsWhoAreMoreThan3MonthsOnArt(
                Arrays.asList(
                    hivMetadata.getMisauLaboratorioEncounterType(),
                    hivMetadata.getAdultoSeguimentoEncounterType(),
                    hivMetadata.getPediatriaSeguimentoEncounterType(),
                    hivMetadata.getMasterCardEncounterType(),
                    hivMetadata.getFsrEncounterType())),
            "endDate=${endDate},location=${location}"));
    cd.addSearch("Target", EptsReportUtils.map(getPatientsWhoAreOnTarget(), mappings));
    cd.setCompositionString("(results AND onArtLongEnough) AND Target");
    return cd;
  }

  /**
   * <b>Description</b>Get patients who are on routine Composition
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoAreOnRoutine() {
    SqlCohortDefinition sql = new SqlCohortDefinition();
    sql.setName("Patients who have viral load results OR FSR coded values");
    sql.setName("Routine for all patients using FSR form");
    sql.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sql.addParameter(new Parameter("endDate", "End Date", Date.class));
    sql.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("51", hivMetadata.getFsrEncounterType().getEncounterTypeId());
    map.put("13", hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("1305", hivMetadata.getHivViralLoadQualitative().getConceptId());
    map.put("23818", hivMetadata.getReasonForRequestingViralLoadConcept().getConceptId());
    map.put("23817", hivMetadata.getRoutineForRequestingViralLoadConcept().getConceptId());
    map.put("1067", hivMetadata.getUnkownConcept().getConceptId());

    StringSubstitutor sb = new StringSubstitutor(map);

    sql.setQuery(sb.replace(ViralLoadQueries.getPatientsHavingRoutineViralLoadTestsUsingFsr()));

    return sql;
  }

  public CohortDefinition getPatientsWhoAreOnRoutineOnMasterCardAndClinicalEncounter() {
    SqlCohortDefinition sqlc = new SqlCohortDefinition();
    sqlc.setName("Patients who have viral load results OR FSR coded values");
    sqlc.setName("Routine for all patients using FSR form");
    sqlc.addParameter(new Parameter("startDate", "Start Date", Date.class));
    sqlc.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlc.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();

    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("1305", hivMetadata.getHivViralLoadQualitative().getConceptId());

    StringSubstitutor sb = new StringSubstitutor(map);
    String sql =
        "SELECT p.patient_id "
            + "FROM   patient p "
            + "       INNER JOIN encounter e "
            + "               ON p.patient_id = e.patient_id "
            + "       INNER JOIN obs o "
            + "               ON e.encounter_id = o.encounter_id "
            + "       INNER JOIN obs o2 "
            + "               ON e.encounter_id = o2.encounter_id "
            + "       INNER JOIN (SELECT max_vl_result.patient_id, "
            + "                          Max(max_vl_result.max_vl) last_vl "
            + "                   FROM   (SELECT p.patient_id, "
            + "                                  Date(e.encounter_datetime) AS max_vl "
            + "                           FROM   patient p "
            + "                                  INNER JOIN encounter e "
            + "                                          ON p.patient_id = e.patient_id "
            + "                                  INNER JOIN obs o "
            + "                                          ON e.encounter_id = o.encounter_id "
            + "                           WHERE  p.voided = 0 "
            + "                                  AND e.voided = 0 "
            + "                                  AND o.voided = 0 "
            + "                                  AND e.encounter_type IN ( ${6}, ${9} ) "
            + "                                  AND ( ( o.concept_id = ${856} "
            + "                                          AND o.value_numeric IS NOT NULL ) "
            + "                                         OR ( o.concept_id = ${1305} "
            + "                                              AND o.value_coded IS NOT NULL ) ) "
            + "                                  AND Date(e.encounter_datetime) <= :endDate "
            + "                                  AND e.location_id = :location "
            + "                           UNION "
            + "                           SELECT p.patient_id, "
            + "                                  Date(o.obs_datetime) AS max_vl "
            + "                           FROM   patient p "
            + "                                  INNER JOIN encounter e "
            + "                                          ON p.patient_id = e.patient_id "
            + "                                  INNER JOIN obs o "
            + "                                          ON e.encounter_id = o.encounter_id "
            + "                           WHERE  p.voided = 0 "
            + "                                  AND e.voided = 0 "
            + "                                  AND o.voided = 0 "
            + "                                  AND e.encounter_type IN ( ${53} ) "
            + "                                  AND ( ( o.concept_id = ${856} "
            + "                                          AND o.value_numeric IS NOT NULL ) "
            + "                                         OR ( o.concept_id = ${1305} "
            + "                                              AND o.value_coded IS NOT NULL ) ) "
            + "                                  AND Date(o.obs_datetime) <= :endDate "
            + "                                  AND e.location_id = :location) max_vl_result "
            + "                   GROUP  BY max_vl_result.patient_id) last_date "
            + "               ON p.patient_id = last_date.patient_id "
            + "WHERE  p.voided = 0 "
            + "       AND e.voided = 0 "
            + "       AND o.voided = 0 "
            + "       AND    ( e.encounter_type IN ( ${6}, ${9} "
            + "               AND ( ( o.concept_id = ${856} "
            + "                       AND o.value_numeric IS NOT NULL ) "
            + "                      OR ( o.concept_id = ${1305} "
            + "                           AND o.value_coded IS NOT NULL ) ) "
            + "               AND Date(e.encounter_datetime) = last_date.last_vl ) "
            + "              OR ( e.encounter_type = ${53} "
            + "                   AND ( ( o.concept_id = ${856} "
            + "                           AND o.value_numeric IS NOT NULL ) "
            + "                          OR ( o.concept_id = ${1305} "
            + "                               AND o.value_coded IS NOT NULL ) ) "
            + "                   AND Date(o.obs_datetime) = last_date.last_vl )) "
            + "GROUP  BY p.patient_id";

    sqlc.setQuery(sb.replace(sql));

    return sqlc;
  }

  /**
   * <b>Description</b> Get patients who are on target Composition
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoAreOnTarget() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("All patients on Target");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "results",
        EptsReportUtils.map(
            hivCohortQueries.getPatientsViralLoadWithin12Months(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "routine",
        EptsReportUtils.map(
            getPatientsWhoAreOnRoutine(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.setCompositionString("results AND NOT routine");
    return cd;
  }

  /**
   * <b>Description</b> Get patients who are on target Composition
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoAreOnTargetBySource() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("All patients on Target");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addSearch(
        "results",
        EptsReportUtils.map(
            hivCohortQueries.getPatientsViralLoadWithin12MonthsBySource(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.addSearch(
        "routine",
        EptsReportUtils.map(
            getPatientsWhoAreOnRoutineOnMasterCardAndClinicalEncounter(),
            "startDate=${startDate},endDate=${endDate},location=${location}"));
    cd.setCompositionString("results AND NOT routine");
    return cd;
  }

  /**
   * <b>Description</b>Get patients having viral load suppression and routine for adults and
   * children - Numerator
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientWithViralSuppressionAndOnRoutine() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Suppression and on routine adult and children");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    cd.addSearch(
        "supp",
        EptsReportUtils.map(
            getPatientsWithViralLoadSuppressionWhoAreOnArtMoreThan3Months(), mappings));
    cd.addSearch("routine", EptsReportUtils.map(getPatientsWhoAreOnRoutine(), mappings));
    cd.setCompositionString("supp AND routine");
    return cd;
  }

  /**
   * <b>Description</b>Get patients having viral load suppression and target for adults and children
   * - Numerator
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientWithViralSuppressionAndOnTarget() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Suppression and on target adult and children");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    cd.addSearch(
        "supp",
        EptsReportUtils.map(
            getPatientsWithViralLoadSuppressionWhoAreOnArtMoreThan3Months(), mappings));
    cd.addSearch("routine", EptsReportUtils.map(getPatientsWhoAreOnRoutine(), mappings));
    cd.setCompositionString("supp AND NOT routine");
    return cd;
  }

  /**
   * <b>Description</b> Get pregnant women Numerator
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPregnantWomenWithViralLoadSuppression() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Get pregnant women with viral load suppression");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    cd.addSearch(
        "suppression",
        EptsReportUtils.map(
            getPatientsWithViralLoadSuppressionWhoAreOnArtMoreThan3Months(), mappings));
    cd.addSearch(
        "pregnant",
        EptsReportUtils.map(this.getPregnantWoman(), "endDate=${endDate},location=${location}"));
    cd.setCompositionString("suppression AND pregnant");
    return cd;
  }

  /**
   * <b>Description</b>Get pregnant women Denominator
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPregnantWomenWithViralLoadResults() {
    CompositionCohortDefinition cd = new CompositionCohortDefinition();
    cd.setName("Get pregnant women with viral load results denominator");
    cd.addParameter(new Parameter("startDate", "Start Date", Date.class));
    cd.addParameter(new Parameter("endDate", "End Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    String mappings = "startDate=${startDate},endDate=${endDate},location=${location}";
    cd.addSearch(
        "results",
        EptsReportUtils.map(getPatientsWithViralLoadResultsAndOnArtForMoreThan3Months(), mappings));
    cd.addSearch(
        "pregnant",
        EptsReportUtils.map(this.getPregnantWoman(), "endDate=${endDate},location=${location}"));
    cd.setCompositionString("results AND pregnant");
    return cd;
  }

  /**
   * <b>Description</b>Get patients who are breastfeeding or pregnant controlled by parameter
   *
   * @param state state
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoArePregnantOrBreastfeedingBasedOnParameter(
      PregnantOrBreastfeedingWomen state, List<EncounterType> encounterTypeList) {
    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            "pregnantBreastfeeding",
            Context.getRegisteredComponents(BreastfeedingPregnantCalculation.class).get(0));
    cd.addParameter(new Parameter("onOrBefore", "On or before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "On or before Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addCalculationParameter("state", state);
    cd.addCalculationParameter("encounterTypeList", encounterTypeList);
    return cd;
  }
  /**
   * <b>Description</b>Get patients who are breastfeeding or pregnant controlled by parameter This
   * method implements MQ Cat 14 Criteria
   *
   * @param state state
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoArePregnantOrBreastfeedingBasedOnParameter4MQ(
      PregnantOrBreastfeedingWomen state, List<EncounterType> encounterTypeList) {
    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            "pregnantBreastfeeding",
            Context.getRegisteredComponents(BreastfeedingPregnantCalculation4MQ.class).get(0));
    cd.addParameter(new Parameter("onOrBefore", "On or before Date", Date.class));
    cd.addParameter(new Parameter("onOrAfter", "On or before Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addCalculationParameter("state", state);
    cd.addCalculationParameter("encounterTypeList", encounterTypeList);
    return cd;
  }

  /**
   * <b>PVLS_FR9</b>
   *
   * <blockquote>
   *
   * <p>The system will identify female patients who are breastfeeding as following:
   *
   * <ul>
   *   <li>Patients who have the “Delivery date” registered in the initial or follow-up
   *       consultations (Processo Clinico Parte A or Ficha de Seguimento Adulto) and where the
   *       delivery date is within the period range or
   *   <li>Patients who started ART for being breastfeeding as specified in “CRITÉRIO PARA INÍCIO DE
   *       TRATAMENTO ARV” in the initial or follow-up consultations (Processo Clinico Parte A or
   *       Ficha de Seguimento Adulto) that occurred within period range or chart: patient
   *       Transferred Out or
   *   <li>Patients who have been registered as breastfeeding in follow up consultation (Ficha de
   *       Seguimento Adulto) within the period range.
   *   <li>Have registered as breastfeeding in Ficha Resumo or Ficha Clinica within the period range
   *       OR
   *   <li>Patients enrolled on Prevention of the Vertical Transmission/Elimination of the Vertical
   *       Transmission (PTV/ETV) program with state 27 (gave birth) within the period range.
   *   <li>Patient who have “Actualmente está a amamentar” marked as “Sim” on FSR Form and Data de
   *       Colheita is during the period range.
   * </ul>
   *
   * <br>
   *
   * <p>If the patient has both states (pregnant and breastfeeding) the most recent one should be
   * considered. For patients who have both state (pregnant and breastfeeding) marked on the same
   * day, the system will consider the patient as pregnant.<br>
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getBreastfeedingPatients() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" Patients disaggregation - breastfeeding");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));

    Map<String, Integer> map = new HashMap<>();
    map.put("1600", hivMetadata.getPregnancyDueDate().getConceptId());
    map.put("1279", hivMetadata.getNumberOfWeeksPregnant().getConceptId());
    map.put("1982", hivMetadata.getPregnantConcept().getConceptId());
    map.put("6331", hivMetadata.getBpostiveConcept().getConceptId());
    map.put("1190", hivMetadata.getARVStartDateConcept().getConceptId());
    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("5", hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("51", hivMetadata.getFsrEncounterType().getEncounterTypeId());
    map.put("5599", hivMetadata.getPriorDeliveryDateConcept().getConceptId());
    map.put("23821", hivMetadata.getSampleCollectionDateAndTime().getConceptId());
    map.put("6332", hivMetadata.getBreastfeeding().getConceptId());
    map.put("1065", hivMetadata.getYesConcept().getConceptId());
    map.put("6334", hivMetadata.getCriteriaForArtStart().getConceptId());
    map.put("8", hivMetadata.getPtvEtvProgram().getProgramId());
    map.put("27", hivMetadata.getPatientGaveBirthWorkflowState().getProgramWorkflowStateId());
    map.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    map.put("13", hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("1305", hivMetadata.getHivViralLoadQualitative().getConceptId());

    String query =
        " SELECT breastfeeding.patient_id FROM( "
            + "                   SELECT vl.patient_id, "
            + "                            MAX(vl.last_date) AS last_vl, MAX(bf.breastfeeding_date) as bf_date "
            + "                     FROM "
            + "                      (SELECT vl.patient_id, MAX(vl.last_date) AS last_date "
            + "             FROM ( "
            + "               SELECT "
            + "                 p.patient_id, "
            + "                 CASE "
            + "                   WHEN e.encounter_type = ${53} THEN o.obs_datetime "
            + "                   ELSE e.encounter_datetime "
            + "                 END AS last_date "
            + "               FROM "
            + "                 patient p "
            + "                 INNER JOIN encounter e ON e.patient_id = p.patient_id "
            + "                 INNER JOIN obs o ON o.encounter_id = e.encounter_id "
            + "               WHERE "
            + "                 ( "
            + "                   (e.encounter_type IN (${6},${9}, ${13}, ${51}) AND e.encounter_datetime "
            + "                     BETWEEN DATE_SUB(:endDate, INTERVAL 12 MONTH) AND :endDate) "
            + "                   OR (e.encounter_type = ${53} AND o.obs_datetime "
            + "                     BETWEEN DATE_SUB(:endDate, INTERVAL 12 MONTH) AND :endDate) "
            + "                 ) "
            + "                 AND ( "
            + "                   (o.concept_id = ${856} AND o.value_numeric IS NOT NULL) "
            + "                   OR (o.concept_id = ${1305} AND o.value_coded IS NOT NULL) "
            + "                 ) "
            + "                 AND e.voided = 0 "
            + "                 AND p.voided = 0 "
            + "                 AND o.voided = 0 "
            + "             ) vl "
            + "             GROUP BY vl.patient_id) vl "
            + "                             INNER JOIN( "
            + "                               SELECT lactantes.patient_id, "
            + "                                       lactantes.last_date AS breastfeeding_date "
            + "                                FROM   (SELECT p.patient_id, "
            + "                                               o.value_datetime AS last_date "
            + "                                        FROM   patient p "
            + "                                               inner join person p2 "
            + "                                                       ON p2.person_id = p.patient_id "
            + "                                               inner join encounter e "
            + "                                                       ON e.patient_id = p.patient_id "
            + "                                               inner join obs o "
            + "                                                       ON o.encounter_id = e.encounter_id "
            + "                                        WHERE  p2.gender = 'F' "
            + "                                               AND e.encounter_type IN ( ${5}, ${6} ) "
            + "                                               AND e.location_id = :location "
            + "                                               AND o.concept_id = 5599 "
            + "                                               AND o.value_datetime <= :endDate "
            + "                                               AND o.voided = 0 "
            + "                                               AND p.voided = 0 "
            + "                                               AND e.voided = 0 "
            + "                                               AND p2.voided = 0 "
            + "                                        UNION "
            + "                                        SELECT p.patient_id, "
            + "                                               e.encounter_datetime AS last_date "
            + "                                        FROM   patient p "
            + "                                               inner join person p2 "
            + "                                                       ON p2.person_id = p.patient_id "
            + "                                               inner join encounter e "
            + "                                                       ON e.patient_id = p.patient_id "
            + "                                               inner join obs o "
            + "                                                       ON o.encounter_id = e.encounter_id "
            + "                                        WHERE  p2.gender = 'F' "
            + "                                               AND e.encounter_type = ${6} "
            + "                                               AND e.location_id = :location "
            + "                                               AND o.concept_id = ${6332} "
            + "                                               AND o.value_coded = ${1065} "
            + "                                               AND e.encounter_datetime <= :endDate "
            + "                                               AND o.voided = 0 "
            + "                                               AND e.voided = 0 "
            + "                                               AND p.voided = 0 "
            + "                                               AND p2.voided = 0 "
            + "                                        UNION "
            + "                                        SELECT p.patient_id, "
            + "                                               e.encounter_datetime AS last_date "
            + "                                        FROM   patient p "
            + "                                               inner join person p2 "
            + "                                                       ON p2.person_id = p.patient_id "
            + "                                               inner join encounter e "
            + "                                                       ON e.patient_id = p.patient_id "
            + "                                               inner join obs o "
            + "                                                       ON o.encounter_id = e.encounter_id "
            + "                                        WHERE  p2.gender = 'F' "
            + "                                               AND e.encounter_type IN ( ${5}, ${6} ) "
            + "                                               AND o.concept_id = ${6334} "
            + "                                               AND o.value_coded = ${6332} "
            + "                                               AND p.voided = 0 "
            + "                                               AND e.voided = 0 "
            + "                                               AND o.voided = 0 "
            + "                                               AND p2.voided = 0 "
            + "                                               AND e.location_id = :location "
            + "                                               AND e.encounter_datetime <= :endDate "
            + "                                        UNION "
            + "                                        SELECT pp.patient_id, "
            + "                                               ps.start_date AS last_date "
            + "                                        FROM   patient_program pp "
            + "                                               inner join person p "
            + "                                                       ON p.person_id = pp.patient_id "
            + "                                               inner join patient_state ps "
            + "                                                       ON ps.patient_program_id = pp.patient_program_id "
            + "                                        WHERE  p.gender = 'F' "
            + "                                               AND pp.program_id = ${8} "
            + "                                               AND ps.state = ${27} "
            + "                                               AND pp.location_id = :location "
            + "                                               AND pp.voided = 0 "
            + "                                               AND ps.voided = 0 "
            + "                                               AND p.voided = 0 "
            + "                                               AND ps.start_date <= :endDate "
            + "                                        UNION "
            + "                                      SELECT p.patient_id, hist.value_datetime AS last_date "
            + "                                    FROM patient p "
            + "                                    INNER JOIN person pe "
            + "                                        ON p.patient_id=pe.person_id "
            + "                                    INNER JOIN encounter e "
            + "                                        ON p.patient_id=e.patient_id "
            + "                                    INNER JOIN obs o "
            + "                                        ON e.encounter_id=o.encounter_id "
            + "                                    INNER JOIN obs hist "
            + "                                        ON e.encounter_id=hist.encounter_id "
            + "                                    WHERE p.voided = 0 "
            + "                                    AND e.voided = 0 "
            + "                                    AND o.voided = 0 "
            + "                                    AND pe.voided = 0 "
            + "                                    AND hist.voided=0 "
            + "                                    AND o.concept_id = ${6332} "
            + "                                    AND o.value_coded = ${1065} "
            + "                                    AND e.encounter_type = ${53} "
            + "                                    AND hist.concept_id = ${1190} "
            + "                                    AND hist.value_datetime <= :endDate "
            + "                                        UNION "
            + "                                        SELECT p.patient_id, "
            + "                                               DATE(o2.value_datetime) AS last_date "
            + "                                        FROM   patient p "
            + "                                               inner join encounter e "
            + "                                                       ON e.patient_id = p.patient_id "
            + "                                               inner join person p2 ON p2.person_id=p.patient_id "
            + "                                               inner join obs o "
            + "                                                       ON o.encounter_id = e.encounter_id "
            + "                                               inner join obs o2 "
            + "                                                       ON o2.encounter_id = e.encounter_id "
            + "                                        WHERE  e.encounter_type = ${51} "
            + "                                               AND  o.concept_id = ${6332} "
            + "                                                     AND o.value_coded = ${1065} "
            + "                                               AND  o2.concept_id = ${23821} "
            + "                                                     AND DATE(o2.value_datetime) <= :endDate "
            + "                                               AND p2.gender = 'F' "
            + "                                               AND p.voided = 0 "
            + "                                               AND e.voided = 0 "
            + "                                               AND o.voided = 0 "
            + "                                               AND o2.voided = 0 "
            + "                                               AND e.location_id = :location "
            + "                                               )lactantes "
            + "                             ) bf ON "
            + "                             vl.patient_id=bf.patient_id "
            + "                             WHERE  bf.breastfeeding_date BETWEEN DATE_SUB(vl.last_date, INTERVAL 18 MONTH) AND vl.last_date "
            + "                     GROUP  BY vl.patient_id ) breastfeeding "
            + "                     WHERE "
            + "                      NOT EXISTS  (SELECT e.patient_id "
            + "                                                          FROM   encounter e "
            + "                                                                 inner join obs o "
            + "                                                                         ON o.encounter_id = e.encounter_id "
            + "                                                          WHERE  e.encounter_type IN ( ${5}, ${6} ) "
            + "                                                                 AND ( o.concept_id = ${1982} "
            + "                                                                       AND o.value_coded = ${1065} ) "
            + "                                                                 AND e.location_id = :location "
            + "                                                                 AND e.voided = 0 "
            + "                                                                 AND o.voided = 0 "
            + "                                                                 AND breastfeeding.patient_id = e.patient_id "
            + "                                                                 AND e.encounter_datetime >= breastfeeding.bf_date "
            + "                                                                 AND e.encounter_datetime BETWEEN DATE_SUB(breastfeeding.last_vl, INTERVAL 9 MONTH) AND breastfeeding.last_vl "
            + " "
            + "                                                         UNION "
            + "                                                           SELECT e.patient_id "
            + "                                                          FROM   encounter e "
            + "                                                                 inner join obs o "
            + "                                                                         ON o.encounter_id = e.encounter_id "
            + "                                                                 WHERE e.encounter_type IN ( ${5}, ${6} ) "
            + "                                                                 AND  o.concept_id = ${1279} "
            + "                                                                 AND e.location_id = :location "
            + "                                                                 AND e.voided = 0 "
            + "                                                                 AND o.voided = 0 "
            + "                                                                 AND breastfeeding.patient_id = e.patient_id "
            + "                                                                 AND e.encounter_datetime >= breastfeeding.bf_date "
            + "                                                                 AND e.encounter_datetime BETWEEN DATE_SUB(breastfeeding.last_vl, INTERVAL 9 MONTH) AND breastfeeding.last_vl "
            + "                                                          UNION "
            + "                                                          SELECT e.patient_id "
            + "                                                          FROM   encounter e "
            + "                                                                 inner join obs o "
            + "                                                                         ON o.encounter_id = e.encounter_id "
            + "                                                                 WHERE e.encounter_type IN ( ${5}, ${6} ) "
            + "                                                                 AND o.concept_id = ${1600} "
            + "                                                                 AND e.location_id = :location "
            + "                                                                 AND e.voided = 0 "
            + "                                                                 AND o.voided = 0 "
            + "                                                                 AND breastfeeding.patient_id = e.patient_id "
            + "                                                                 AND e.encounter_datetime >= breastfeeding.bf_date "
            + "                                                                 AND e.encounter_datetime BETWEEN DATE_SUB(breastfeeding.last_vl, INTERVAL 9 MONTH) AND breastfeeding.last_vl "
            + "                                                          UNION "
            + "                                                          SELECT e.patient_id "
            + "                                                          FROM   encounter e "
            + "                                                                 inner join obs o "
            + "                                                                         ON o.encounter_id = e.encounter_id "
            + "                                                                 WHERE e.encounter_type = ${6} "
            + "                                                                 AND o.concept_id = ${6334} "
            + "																AND o.value_coded = ${6331} "
            + "                                                                 AND e.location_id = :location "
            + "                                                                 AND e.voided = 0 "
            + "                                                                 AND o.voided = 0 "
            + "                                                                 AND breastfeeding.patient_id = e.patient_id "
            + "                                                                 AND e.encounter_datetime >= breastfeeding.bf_date "
            + "                                                                 AND e.encounter_datetime BETWEEN DATE_SUB(breastfeeding.last_vl, INTERVAL 9 MONTH) AND breastfeeding.last_vl "
            + "                                                          UNION "
            + "                                                          SELECT pp.patient_id "
            + "                                                          FROM   patient_program pp "
            + "                                                                 WHERE pp.program_id = ${8} "
            + "                                                                 AND pp.voided = 0 "
            + "                                                                 AND breastfeeding.patient_id = pp.patient_id "
            + "                                                                 AND pp.date_enrolled >= breastfeeding.bf_date "
            + "                                                                 AND pp.date_enrolled BETWEEN DATE_SUB(breastfeeding.last_vl, INTERVAL 9 MONTH) AND breastfeeding.last_vl "
            + "                                                          UNION "
            + "                                                          SELECT e.patient_id "
            + "                                                          FROM   encounter e "
            + "                                                                 inner join obs o "
            + "                                                                         ON o.encounter_id = e.encounter_id "
            + "                                                                 inner join obs o2 "
            + "                                                                         ON o2.encounter_id = e.encounter_id "
            + "                                                                 WHERE e.encounter_type = ${53} "
            + "                                                                 AND  o.concept_id = ${1982} "
            + "                                                                 AND o.value_coded = ${1065} "
            + "                                                                 AND  o2.concept_id = ${1190} "
            + "                                                                 AND o2.value_datetime BETWEEN DATE_SUB(breastfeeding.last_vl, INTERVAL 9 MONTH) AND breastfeeding.last_vl "
            + "                                                                 AND e.location_id = :location "
            + "                                                                 AND e.voided = 0 "
            + "                                                                 AND o.voided = 0 "
            + "                                                                 AND o2.voided = 0 "
            + "                                                                 AND breastfeeding.patient_id = e.patient_id "
            + "                                                                 AND o2.value_datetime >= breastfeeding.bf_date "
            + "                                                          UNION "
            + "                                                          SELECT e.patient_id "
            + "                                                          FROM   encounter e "
            + "                                                                 inner join obs o "
            + "                                                                         ON o.encounter_id = e.encounter_id "
            + "                                                                 WHERE e.encounter_type = ${6} "
            + "                                                                 AND  o.concept_id = ${1982} "
            + "																 AND o.value_coded = ${1065} "
            + "                                                                 AND e.location_id = :location "
            + "                                                                 AND e.voided = 0 "
            + "                                                                 AND o.voided = 0 "
            + "                                                                 AND breastfeeding.patient_id = e.patient_id "
            + "                                                                 AND e.encounter_datetime >= breastfeeding.bf_date "
            + "                                                                 AND e.encounter_datetime BETWEEN DATE_SUB(breastfeeding.last_vl, INTERVAL 9 MONTH) AND breastfeeding.last_vl "
            + "                                                          UNION "
            + "                                                          SELECT e.patient_id "
            + "                                                          FROM   encounter e "
            + "                                                                 inner join obs o "
            + "                                                                         ON o.encounter_id = e.encounter_id "
            + "                                                                 inner join obs o2 "
            + "                                                                         ON o2.encounter_id = e.encounter_id "
            + "                                                                 WHERE e.encounter_type = ${51} "
            + "                                                                 AND  o.concept_id = ${1982} "
            + "                                                                 AND o.value_coded = ${1065} "
            + "                                                                 AND  o2.concept_id = ${23821} "
            + "                                                                 AND DATE(o2.value_datetime) BETWEEN DATE_SUB(breastfeeding.last_vl, INTERVAL 9 MONTH) AND breastfeeding.last_vl "
            + "                                                                 AND e.location_id = :location "
            + "                                                                 AND e.voided = 0 "
            + "                                                                 AND o.voided = 0 "
            + "                                                                 AND o2.voided = 0 "
            + "                                                                 AND breastfeeding.patient_id = e.patient_id "
            + "                                                                 AND DATE(o2.value_datetime) >= breastfeeding.bf_date "
            + "                                                        ) ";

    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    String mappedQuery = stringSubstitutor.replace(query);

    sqlCohortDefinition.setQuery(mappedQuery);

    return sqlCohortDefinition;
  }

  /**
   * <b>PVLS_FR9</b>
   *
   * <blockquote>
   *
   * <p>The system will identify female patients who are breastfeeding as following:
   *
   * <ul>
   *   <li>Patients who have the “Delivery date” registered in the initial or follow-up
   *       consultations (Processo Clinico Parte A or Ficha de Seguimento Adulto) and where the
   *       delivery date is within the period range or
   *   <li>Patients who started ART for being breastfeeding as specified in “CRITÉRIO PARA INÍCIO DE
   *       TRATAMENTO ARV” in the initial or follow-up consultations (Processo Clinico Parte A or
   *       Ficha de Seguimento Adulto) that occurred within period range or chart: patient
   *       Transferred Out or
   *   <li>Patients who have been registered as breastfeeding in follow up consultation (Ficha de
   *       Seguimento Adulto) within the period range.
   *   <li>Have registered as breastfeeding in Ficha Resumo or Ficha Clinica within the period range
   *       OR
   *   <li>Patients enrolled on Prevention of the Vertical Transmission/Elimination of the Vertical
   *       Transmission (PTV/ETV) program with state 27 (gave birth) within the period range.
   *   <li>Patient who have “Actualmente está a amamentar” marked as “Sim” on FSR Form and Data de
   *       Colheita is during the period range.
   * </ul>
   *
   * <br>
   *
   * <p>If the patient has both states (pregnant and breastfeeding) the most recent one should be
   * considered. For patients who have both state (pregnant and breastfeeding) marked on the same
   * day, the system will consider the patient as pregnant.<br>
   *
   * </blockquote>
   *
   * @return {@link CohortDefinition}
   */
  public CohortDefinition getPregnantWoman() {
    SqlCohortDefinition sqlCohortDefinition = new SqlCohortDefinition();
    sqlCohortDefinition.setName(" Patients disaggregation - Pregnant");
    sqlCohortDefinition.addParameter(new Parameter("endDate", "End Date", Date.class));
    sqlCohortDefinition.addParameter(new Parameter("location", "Location", Location.class));
    Map<String, Integer> map = new HashMap<>();

    map.put("6", hivMetadata.getAdultoSeguimentoEncounterType().getEncounterTypeId());
    map.put("5", hivMetadata.getARVAdultInitialEncounterType().getEncounterTypeId());
    map.put("53", hivMetadata.getMasterCardEncounterType().getEncounterTypeId());
    map.put("51", hivMetadata.getFsrEncounterType().getEncounterTypeId());
    map.put("1600", hivMetadata.getPregnancyDueDate().getConceptId());
    map.put("23821", hivMetadata.getSampleCollectionDateAndTime().getConceptId());
    map.put("1065", hivMetadata.getYesConcept().getConceptId());
    map.put("6334", hivMetadata.getCriteriaForArtStart().getConceptId());
    map.put("8", hivMetadata.getPtvEtvProgram().getProgramId());
    map.put("1279", hivMetadata.getNumberOfWeeksPregnant().getConceptId());
    map.put("9", hivMetadata.getPediatriaSeguimentoEncounterType().getEncounterTypeId());
    map.put("1982", hivMetadata.getPregnantConcept().getConceptId());
    map.put("856", hivMetadata.getHivViralLoadConcept().getConceptId());
    map.put("1305", hivMetadata.getHivViralLoadQualitative().getConceptId());
    map.put("13", hivMetadata.getMisauLaboratorioEncounterType().getEncounterTypeId());
    map.put("6331", hivMetadata.getBPlusConcept().getConceptId());
    map.put("1190", hivMetadata.getARVStartDateConcept().getConceptId());
    map.put("5599", hivMetadata.getPriorDeliveryDateConcept().getConceptId());
    map.put("6332", hivMetadata.getBreastfeeding().getConceptId());
    map.put("27", hivMetadata.getPatientGaveBirthWorkflowState().getProgramWorkflowStateId());

    String query =
        "SELECT pregnant.patient_id "
            + "FROM  ( "
            + "                  SELECT     vl.patient_id, "
            + "                             vl.last_date           AS last_vl, "
            + "                             Max(pg.pregnancy_date) AS pg_date "
            + "                  FROM       ( "
            + "                                      SELECT   vl.patient_id, "
            + "                                               Max(vl.last_date) AS last_date "
            + "                                      FROM     ( "
            + "                                                          SELECT     p.patient_id, "
            + "                                                                     CASE "
            + "                                                                                WHEN e.encounter_type = ${53} THEN o.obs_datetime "
            + "                                                                                ELSE e.encounter_datetime "
            + "                                                                     END AS last_date "
            + "                                                          FROM       patient p "
            + "                                                          INNER JOIN encounter e "
            + "                                                          ON         e.patient_id = p.patient_id "
            + "                                                          INNER JOIN obs o "
            + "                                                          ON         o.encounter_id = e.encounter_id "
            + "                                                          WHERE      ( ( "
            + "                                                                                           e.encounter_type IN (${6}, "
            + "                                                                                                                ${9}, "
            + "                                                                                                                ${13}, "
            + "                                                                                                                ${51}) "
            + "                                                                                AND        e.encounter_datetime BETWEEN date_sub(:endDate, interval 12 month) AND        :endDate) "
            + "                                                                     OR         ( "
            + "                                                                                           e.encounter_type = ${53} "
            + "                                                                                AND        o.obs_datetime BETWEEN date_sub(:endDate, interval 12 month) AND        :endDate) ) "
            + "                                                          AND        ( ( "
            + "                                                                                           o.concept_id = ${856} "
            + "                                                                                AND        o.value_numeric IS NOT NULL) "
            + "                                                                     OR         ( "
            + "                                                                                           o.concept_id = ${1305} "
            + "                                                                                AND        o.value_coded IS NOT NULL) ) "
            + "                                                          AND        e.voided = 0 "
            + "                                                          AND        p.voided = 0 "
            + "                                                          AND        o.voided = 0 ) vl "
            + "                                      GROUP BY vl.patient_id) vl "
            + "                  INNER JOIN "
            + "                             ( "
            + "                                        SELECT     p.patient_id, "
            + "                                                   e.encounter_datetime AS pregnancy_date "
            + "                                        FROM       patient p "
            + "                                        INNER JOIN person p2 "
            + "                                        ON         p2.person_id = p.patient_id "
            + "                                        INNER JOIN encounter e "
            + "                                        ON         e.patient_id = p.patient_id "
            + "                                        INNER JOIN obs o "
            + "                                        ON         o.encounter_id = e.encounter_id "
            + "                                        WHERE      e.encounter_type IN ( ${5}, "
            + "                                                                        ${6} ) "
            + "                                        AND        p2.gender = 'F' "
            + "                                        AND        ( "
            + "                                                              o.concept_id = ${1982} "
            + "                                                   AND        o.value_coded = ${1065} ) "
            + "                                        AND        e.location_id = :location "
            + "                                        AND        p.voided = 0 "
            + "                                        AND        e.encounter_datetime <= :endDate "
            + "                                        AND        p2.voided = 0 "
            + "                                        AND        e.voided = 0 "
            + "                                        AND        o.voided = 0 "
            + "                                        UNION "
            + "                                        SELECT     p.patient_id, "
            + "                                                   e.encounter_datetime AS pregnancy_date "
            + "                                        FROM       patient p "
            + "                                        INNER JOIN person p2 "
            + "                                        ON         p2.person_id = p.patient_id "
            + "                                        INNER JOIN encounter e "
            + "                                        ON         e.patient_id = p.patient_id "
            + "                                        INNER JOIN obs o "
            + "                                        ON         o.encounter_id = e.encounter_id "
            + "                                        WHERE      p2.gender = 'F' "
            + "                                        AND        e.encounter_type IN ( ${5}, "
            + "                                                                        ${6} ) "
            + "                                        AND        o.concept_id = ${1279} "
            + "                                        AND        e.location_id = :location "
            + "                                        AND        p.voided = 0 "
            + "                                        AND        p2.voided = 0 "
            + "                                        AND        e.voided = 0 "
            + "                                        AND        o.voided = 0 "
            + "                                        AND        e.encounter_datetime <= :endDate "
            + "                                        UNION "
            + "                                        SELECT     p.patient_id, "
            + "                                                   e.encounter_datetime AS pregnancy_date "
            + "                                        FROM       patient p "
            + "                                        INNER JOIN person p2 "
            + "                                        ON         p2.person_id = p.patient_id "
            + "                                        INNER JOIN encounter e "
            + "                                        ON         e.patient_id = p.patient_id "
            + "                                        INNER JOIN obs o "
            + "                                        ON         o.encounter_id = e.encounter_id "
            + "                                        WHERE      p2.gender = 'F' "
            + "                                        AND        e.encounter_type IN ( ${5}, "
            + "                                                                        ${6} ) "
            + "                                        AND        o.concept_id = ${1600} "
            + "                                        AND        e.location_id = :location "
            + "                                        AND        p.voided = 0 "
            + "                                        AND        p2.voided = 0 "
            + "                                        AND        e.voided = 0 "
            + "                                        AND        o.voided = 0 "
            + "                                        AND        e.encounter_datetime <= :endDate "
            + "                                        UNION "
            + "                                        SELECT     p.patient_id, "
            + "                                                   e.encounter_datetime AS pregnancy_date "
            + "                                        FROM       patient p "
            + "                                        INNER JOIN person p2 "
            + "                                        ON         p2.person_id = p.patient_id "
            + "                                        INNER JOIN encounter e "
            + "                                        ON         e.patient_id = p.patient_id "
            + "                                        INNER JOIN obs o "
            + "                                        ON         o.encounter_id = e.encounter_id "
            + "                                        WHERE      p2.gender = 'F' "
            + "                                        AND        e.encounter_type = ${6} "
            + "                                        AND        ( "
            + "                                                              o.concept_id = ${6334} "
            + "                                                   AND        o.value_coded = ${6331} ) "
            + "                                        AND        e.location_id = :location "
            + "                                        AND        p.voided = 0 "
            + "                                        AND        p2.voided = 0 "
            + "                                        AND        e.voided = 0 "
            + "                                        AND        o.voided = 0 "
            + "                                        AND        e.encounter_datetime <= :endDate "
            + "                                        UNION "
            + "                                        SELECT     pp.patient_id, "
            + "                                                   pp.date_enrolled AS pregnancy_date "
            + "                                        FROM       patient_program pp "
            + "                                        INNER JOIN person p "
            + "                                        ON         p.person_id = pp.patient_id "
            + "                                        INNER JOIN encounter e "
            + "                                        ON         e.patient_id = pp.patient_id "
            + "                                        WHERE      p.gender = 'F' "
            + "                                        AND        pp.program_id = ${8} "
            + "                                        AND        e.location_id = :location "
            + "                                        AND        p.voided = 0 "
            + "                                        AND        pp.voided = 0 "
            + "                                        AND        pp.date_enrolled <= :endDate "
            + "                                        UNION "
            + "                                        SELECT     p.patient_id, "
            + "                                                   o2.value_datetime AS pregnancy_date "
            + "                                        FROM       patient p "
            + "                                        INNER JOIN person p2 "
            + "                                        ON         p2.person_id = p.patient_id "
            + "                                        INNER JOIN encounter e "
            + "                                        ON         e.patient_id = p.patient_id "
            + "                                        INNER JOIN obs o "
            + "                                        ON         o.encounter_id = e.encounter_id "
            + "                                        INNER JOIN obs o2 "
            + "                                        ON         o2.encounter_id = e.encounter_id "
            + "                                        WHERE      p2.gender = 'F' "
            + "                                        AND        e.encounter_type = ${53} "
            + "                                        AND        ( "
            + "                                                              o.concept_id = ${1982} "
            + "                                                   AND        o.value_coded = ${1065} ) "
            + "                                        AND        ( "
            + "                                                              o2.concept_id = ${1190} "
            + "                                                   AND        o2.value_datetime <= :endDate ) "
            + "                                        AND        e.location_id = :location "
            + "                                        AND        p.voided = 0 "
            + "                                        AND        p2.voided = 0 "
            + "                                        AND        e.voided = 0 "
            + "                                        AND        o.voided = 0 "
            + "                                        AND        o2.voided = 0 "
            + "                                        UNION "
            + "                                        SELECT     p.patient_id, "
            + "                                                   e.encounter_datetime AS pregnancy_date "
            + "                                        FROM       patient p "
            + "                                        INNER JOIN person p2 "
            + "                                        ON         p2.person_id = p.patient_id "
            + "                                        INNER JOIN encounter e "
            + "                                        ON         e.patient_id = p.patient_id "
            + "                                        INNER JOIN obs o "
            + "                                        ON         o.encounter_id = e.encounter_id "
            + "                                        WHERE      p2.gender = 'F' "
            + "                                        AND        e.encounter_type = ${6} "
            + "                                        AND        ( "
            + "                                                              o.concept_id = ${1982} "
            + "                                                   AND        o.value_coded = ${1065} ) "
            + "                                        AND        e.location_id = :location "
            + "                                        AND        p.voided = 0 "
            + "                                        AND        p2.voided = 0 "
            + "                                        AND        e.voided = 0 "
            + "                                        AND        o.voided = 0 "
            + "                                        AND        e.encounter_datetime <= :endDate "
            + "                                        UNION "
            + "                                        SELECT     p.patient_id, "
            + "                                                   date(o2.value_datetime) AS pregnancy_date "
            + "                                        FROM       patient p "
            + "                                        INNER JOIN person p2 "
            + "                                        ON         p2.person_id = p.patient_id "
            + "                                        INNER JOIN encounter e "
            + "                                        ON         e.patient_id = p.patient_id "
            + "                                        INNER JOIN obs o "
            + "                                        ON         o.encounter_id = e.encounter_id "
            + "                                        INNER JOIN obs o2 "
            + "                                        ON         o2.encounter_id = e.encounter_id "
            + "                                        WHERE      p2.gender = 'F' "
            + "                                        AND        e.encounter_type = ${51} "
            + "                                        AND        ( "
            + "                                                              o.concept_id = ${1982} "
            + "                                                   AND        o.value_coded = ${1065} ) "
            + "                                        AND        ( "
            + "                                                              o2.concept_id = ${23821} "
            + "                                                   AND        date(o2.value_datetime) <= :endDate ) "
            + "                                        AND        e.location_id = :location "
            + "                                        AND        p.voided = 0 "
            + "                                        AND        p2.voided = 0 "
            + "                                        AND        e.voided = 0 "
            + "                                        AND        o.voided = 0 "
            + "                                        AND        o2.voided = 0 ) pg "
            + "                  ON         vl.patient_id=pg.patient_id "
            + "                  WHERE      pg.pregnancy_date BETWEEN date_sub(vl.last_date, interval 9 month) AND        vl.last_date "
            + "                  GROUP BY   vl.patient_id ) pregnant "
            + "WHERE  NOT EXISTS "
            + "       ( "
            + "                  SELECT     e.patient_id "
            + "                  FROM       encounter e "
            + "                  INNER JOIN obs o "
            + "                  ON         o.encounter_id = e.encounter_id "
            + "                  WHERE      e.encounter_type IN ( ${5}, "
            + "                                                  ${6} ) "
            + "                  AND        ( "
            + "                                        o.concept_id = ${5599} "
            + "                             AND        o.value_datetime BETWEEN date_sub(pregnant.last_vl, interval 18 month) AND        pregnant.last_vl) "
            + "                  AND        e.location_id = :location "
            + "                  AND        pregnant.patient_id= e.patient_id "
            + "                  AND        o.value_datetime > pregnant.pg_date "
            + "                  AND        e.voided = 0 "
            + "                  AND        o.voided = 0 "
            + "                  UNION "
            + "                  SELECT     e.patient_id "
            + "                  FROM       encounter e "
            + "                  INNER JOIN obs o "
            + "                  ON         o.encounter_id = e.encounter_id "
            + "                  WHERE      e.encounter_type = ${6} "
            + "                  AND        e.location_id = :location "
            + "                  AND        ( "
            + "                                        o.concept_id = ${6332} "
            + "                             AND        o.value_coded = ${1065}) "
            + "                  AND        pregnant.patient_id= e.patient_id "
            + "                  AND        e.encounter_datetime > pregnant.pg_date "
            + "                  AND        e.encounter_datetime BETWEEN date_sub(pregnant.last_vl, interval 18 month) AND        pregnant.last_vl "
            + "                  AND        o.voided = 0 "
            + "                  AND        e.voided = 0 "
            + "                  UNION "
            + "                  SELECT     e.patient_id "
            + "                  FROM       encounter e "
            + "                  INNER JOIN obs o "
            + "                  ON         o.encounter_id = e.encounter_id "
            + "                  WHERE      e.encounter_type IN ( ${5}, "
            + "                                                  ${6} ) "
            + "                  AND        o.concept_id = ${6334} "
            + "                  AND        o.value_coded = ${6332} "
            + "                  AND        e.voided = 0 "
            + "                  AND        o.voided = 0 "
            + "                  AND        e.location_id = :location "
            + "                  AND        pregnant.patient_id= e.patient_id "
            + "                  AND        e.encounter_datetime > pregnant.pg_date "
            + "                  AND        e.encounter_datetime BETWEEN date_sub(pregnant.last_vl, interval 18 month) AND        pregnant.last_vl "
            + "                  UNION "
            + "                  SELECT     pp.patient_id "
            + "                  FROM       patient_program pp "
            + "                  INNER JOIN patient_state ps "
            + "                  ON         ps.patient_program_id = pp.patient_program_id "
            + "                  WHERE      pp.program_id = ${8} "
            + "                  AND        ps.state = ${27} "
            + "                  AND        pp.location_id = :location "
            + "                  AND        pp.voided = 0 "
            + "                  AND        ps.voided = 0 "
            + "                  AND        pregnant.patient_id= pp.patient_id "
            + "                  AND        ps.start_date > pregnant.pg_date "
            + "                  AND        ps.start_date BETWEEN date_sub(pregnant.last_vl, interval 18 month) AND        pregnant.last_vl "
            + "                  UNION "
            + "                  SELECT     e.patient_id "
            + "                  FROM       encounter e "
            + "                  INNER JOIN obs o "
            + "                  ON         e.encounter_id=o.encounter_id "
            + "                  INNER JOIN obs hist "
            + "                  ON         e.encounter_id=hist.encounter_id "
            + "                  WHERE      e.voided = 0 "
            + "                  AND        o.voided = 0 "
            + "                  AND        hist.voided=0 "
            + "                  AND        o.concept_id = ${6332} "
            + "                  AND        o.value_coded = ${1065} "
            + "                  AND        e.encounter_type = ${53} "
            + "                  AND        hist.concept_id = ${1190} "
            + "                  AND        hist.value_datetime BETWEEN date_sub(pregnant.last_vl, interval 18 month) AND        pregnant.last_vl "
            + "                  AND        hist.value_datetime > pregnant.pg_date "
            + "                  AND        e.patient_id = pregnant.patient_id "
            + "                  UNION "
            + "                  SELECT     e.patient_id "
            + "                  FROM       encounter e "
            + "                  INNER JOIN obs o "
            + "                  ON         o.encounter_id = e.encounter_id "
            + "                  INNER JOIN obs o2 "
            + "                  ON         o2.encounter_id = e.encounter_id "
            + "                  WHERE      e.encounter_type = ${51} "
            + "                  AND        o.concept_id = ${6332} "
            + "                  AND        o.value_coded = ${1065} "
            + "                  AND        o2.concept_id = ${23821} "
            + "                  AND        date(o2.value_datetime) BETWEEN date_sub(pregnant.last_vl, interval 18 month) AND        pregnant.last_vl "
            + "                  AND        pregnant.patient_id= e.patient_id "
            + "                  AND        date(o2.value_datetime) > pregnant.pg_date "
            + "                  AND        e.voided = 0 "
            + "                  AND        o.voided = 0 "
            + "                  AND        o2.voided = 0 "
            + "                  AND        e.location_id = :location )";
    StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
    String mappedQuery = stringSubstitutor.replace(query);
    sqlCohortDefinition.setQuery(mappedQuery);
    return sqlCohortDefinition;
  }
}
