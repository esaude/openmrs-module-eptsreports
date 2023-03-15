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
import org.openmrs.module.eptsreports.metadata.HivMetadata;
import org.openmrs.module.eptsreports.reporting.calculation.mq.BreastfeedingPregnantCalculation4MQ;
import org.openmrs.module.eptsreports.reporting.calculation.pvls.BreastfeedingPregnantCalculation;
import org.openmrs.module.eptsreports.reporting.calculation.pvls.OnArtForMoreThanXmonthsCalculation;
import org.openmrs.module.eptsreports.reporting.cohort.definition.CalculationCohortDefinition;
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

  @Autowired
  public TxPvlsCohortQueries(HivCohortQueries hivCohortQueries, HivMetadata hivMetadata) {
    this.hivCohortQueries = hivCohortQueries;
    this.hivMetadata = hivMetadata;
  }

  /**
   * <b>Description</b> On ART more than 3 months <blockqoute> Patients who have NOT been on ART for
   * 3 months based on the ART initiation date and date of last viral load registered </blockqoute>
   *
   * @return CohortDefinition
   */
  public CohortDefinition getPatientsWhoAreMoreThan3MonthsOnArt(
      List<EncounterType> encounterTypeList) {
    CalculationCohortDefinition cd =
        new CalculationCohortDefinition(
            "On ART for at least 3 months for pvls",
            Context.getRegisteredComponents(OnArtForMoreThanXmonthsCalculation.class).get(0));
    cd.addParameter(new Parameter("onOrBefore", "On or before Date", Date.class));
    cd.addParameter(new Parameter("location", "Location", Location.class));
    cd.addCalculationParameter("listOfEncounters", encounterTypeList);
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
        EptsReportUtils.map(
            getPatientsWhoArePregnantOrBreastfeedingBasedOnParameter(
                PregnantOrBreastfeedingWomen.BREASTFEEDINGWOMEN, null),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

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
        EptsReportUtils.map(
            getPatientsWhoArePregnantOrBreastfeedingBasedOnParameter(
                PregnantOrBreastfeedingWomen.BREASTFEEDINGWOMEN, null),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));

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
            "onOrBefore=${endDate},location=${location}"));
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
            "onOrBefore=${endDate},location=${location}"));
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
            "onOrBefore=${endDate},location=${location}"));
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
            "onOrBefore=${endDate},location=${location}"));
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
        EptsReportUtils.map(
            this.getPatientsWhoArePregnantOrBreastfeedingBasedOnParameter(
                PregnantOrBreastfeedingWomen.PREGNANTWOMEN, null),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
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
        EptsReportUtils.map(
            this.getPatientsWhoArePregnantOrBreastfeedingBasedOnParameter(
                PregnantOrBreastfeedingWomen.PREGNANTWOMEN, null),
            "onOrAfter=${startDate},onOrBefore=${endDate},location=${location}"));
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
}
